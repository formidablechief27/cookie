package com.example.chief.controller;

import java.io.BufferedReader;
import java.net.URLDecoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class IDEController {
	
	@GetMapping("/ide")
	public String start(HttpSession session, Model model) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		return "ide.html";
	}
	
	@PostMapping("/run-code")
	public ResponseEntity<String> run_code(@RequestParam String code, @RequestParam String input, @RequestParam String language){
		try {code = URLDecoder.decode(code, "UTF-8");}
		catch (UnsupportedEncodingException e) {e.printStackTrace();}
		System.out.println("Language Selected => " + language);
		StringBuilder f_output = new StringBuilder();
		if(language.equals("Java")) f_output = run(code, input);
		else if(language.equals("C++")) f_output = run_cpp(code, input); 
		else f_output = runpy(code, input);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("output", f_output.toString());
	    ObjectMapper objectMapper = new ObjectMapper();
	    try {
	        String jsonResponse = objectMapper.writeValueAsString(responseMap);
	        System.out.println(jsonResponse);
	        return ResponseEntity.status(HttpStatus.OK).body(jsonResponse);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the response");
	    }
	}
	
	public StringBuilder run(String code, String input) {
		if(code.contains("ProcessBuilder") || code.contains("Executors") || code.contains("StreamHandler") || code.contains("CommandLine") || code.contains("Executor") || code.contains("getRuntime")) {
			return null;
		}
		File sourceFile = new File(extract(code) + ".java");
        FileWriter writer;
		try {
			writer = new FileWriter(sourceFile);
			writer.write(code);
	        writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder output = new StringBuilder();
		ExecutorService executor = Executors.newSingleThreadExecutor();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult = compiler.run(null, null, null, sourceFile.getPath());
        // Use a Future to track the execution
        Future<StringBuilder> future = executor.submit(() -> {
            // Compile the Java source file
			if (compilationResult == 0) {
			    // Compilation succeeded, execute the compiled class
			    InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
			    String className = sourceFile.getName().replace(".java", "");
			    ProcessBuilder processBuilder = new ProcessBuilder("java", "-Xint", className);
			    processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
			    processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
			    long start = System.currentTimeMillis();
			    Process process = processBuilder.start();

                // Write the input to the standard input of the process
                try (OutputStream outputStream = process.getOutputStream()) {
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(inputBytes);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
			    while ((line = reader.readLine()) != null) {
			    	output.append(line);
			    	output.append("\n");
	            }
			    
			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    while ((line = reader.readLine()) != null) {
			    	output.append(line);
			    	output.append("\n");
		        }
			    return output;
			} else {
			    output.append("Compilation Error ");
			    return output;
			}
        });
        try {
            StringBuilder result = future.get(5, TimeUnit.SECONDS); // 5 seconds timeout
            return result;
        } catch (Exception e) {
            future.cancel(true);
            output.append("Time Limit Exceeded");
            return output;
        }
	}
	
	public StringBuilder run_cpp(String code, String input) {
		if(code.contains("FILE") || code.contains("pipe") || code.contains("spawn.h") || code.contains("sys/wait.h") || code.contains("unistd.h") || code.contains("sys/types.h") || code.contains("pid_t")) {
			return null;
		}
    	File sourceFile = new File("main.cpp");
    	Random rand = new Random();
    	int num = rand.nextInt(1000);
        FileWriter writer;
        try {
            writer = new FileWriter(sourceFile);
            writer.write(code);
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ProcessBuilder compileProcessBuilder = new ProcessBuilder("g++", "-O0", sourceFile.getPath(), "-o", "output" + num);
        Process compileProcess;
        StringBuilder output = new StringBuilder();

        try {
            compileProcess = compileProcessBuilder.start();

            // Capture compilation errors
            try (BufferedReader compileErrorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))) {
                String line;
                while ((line = compileErrorReader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int compileExitCode = compileProcess.waitFor();

            if (compileExitCode != 0) {
                // Compilation error occurred
                output.insert(0, "Compilation Error:\n");
                return output;
            }

        } catch (IOException | InterruptedException e1) {
            e1.printStackTrace();
            output.append("Internal Error during compilation");
            return output;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<StringBuilder> future = executor.submit(() -> {
            ProcessBuilder runtimeProcessBuilder = new ProcessBuilder("./output" + num);
            runtimeProcessBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
            runtimeProcessBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

            try {
                Process runtimeProcess = runtimeProcessBuilder.start();

                // Pass input to the runtime process
                try (OutputStream outputStream = runtimeProcess.getOutputStream()) {
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(inputBytes);
                }

                // Capture runtime errors
                try (BufferedReader runtimeErrorReader = new BufferedReader(new InputStreamReader(runtimeProcess.getErrorStream()))) {
                    String line;
                    while ((line = runtimeErrorReader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                // Capture runtime output
                try (BufferedReader runtimeOutputReader = new BufferedReader(new InputStreamReader(runtimeProcess.getInputStream()))) {
                    String line;
                    while ((line = runtimeOutputReader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                int runtimeExitCode = runtimeProcess.waitFor();

                if (runtimeExitCode != 0) {
                    // Runtime error occurred
                    output.insert(0, "Runtime Error:\n");
                }

                return output;

            } catch (IOException e) {
                e.printStackTrace();
                output.append("Internal Error during runtime");
                return output;
            }
        });

        try {
            StringBuilder result = future.get(5, TimeUnit.SECONDS); // 5 seconds timeout
            return result;
        } catch (Exception e) {
            future.cancel(true);
            output.append("Time Limit Exceeded");
            return output;
        }
	}
	
	public StringBuilder runpy(String code, String input) {
        ArrayList<Long> times = new ArrayList<>();
        // Write the Python code to a file
        File sourceFile = new File("main.py");
        if(code.contains("import os") || code.contains("import subprocess")) return null;
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(code);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        StringBuilder output = new StringBuilder();
        Future<StringBuilder> future = executor.submit(() -> {
            // Run Python code
            ProcessBuilder processBuilder = new ProcessBuilder("python3", sourceFile.getPath());
            processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

            try {
                Process process = processBuilder.start();

                // Write the input to the standard input of the process
                try (OutputStream outputStream = process.getOutputStream()) {
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(inputBytes);
                }

                // Capture the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                int i = 0;
			    boolean flag = true;
			    String foutput = "";
			    while ((line = reader.readLine()) != null) {
			    	output.append(line);
			    	output.append("\n");
	            }
			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    while ((line = reader.readLine()) != null) {
			    	output.append(line);
			    	output.append("\n");
	            }
			    return output;
            } catch (IOException e) {
                e.printStackTrace();
                output.append("Python Error");
                return output;
            }
        });
        try {
            StringBuilder result = future.get(5, TimeUnit.SECONDS); // 5 seconds timeout
            return result;
        } catch (Exception e) {
            future.cancel(true);
            output.append("Python Limit Exceeded");
            return output;
        }
    }
	
	public String extract(String code) {
	   String className = null;
       Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
       Matcher matcher = pattern.matcher(code);
       if (matcher.find()) className = matcher.group(1);
       else className = "Main";
       return className;
	}
	
}
