package com.example.chief.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chief.*;
import com.example.chief.model.*;
import com.example.chief.repository.*;

@Controller
public class CodeController {
	
	class Pair {String f; String s; Pair(String f, String s) {this.f = f;this.s = s;}@Override public boolean equals(Object o) {if (this == o) return true; if (o == null || getClass() != o.getClass()) return false;Pair pair = (Pair) o;return f.equals(pair.f) && s.equals(pair.s);}@Override public int hashCode() {return Objects.hash(f, s);}}
	
	private TestsRepository tests_repository;
	private QuestionsRepository ques_repo;
	private SubmissionsRepository subs_repo;
	private UserRepository user_repo;
	
	@Autowired
    public CodeController(TestsRepository tests_Repository, QuestionsRepository ques_repo, SubmissionsRepository subsrepo, UserRepository user) {
        this.tests_repository = tests_Repository;
        this.ques_repo = ques_repo;
        this.subs_repo = subsrepo;
        this.user_repo = user;
    }
	
	public String getQuestionNameById(int questionId) {
		if(DataCache.ques_map.containsKey(questionId)) return DataCache.ques_map.get(questionId).getQuestionName();
        return ques_repo.findById(questionId)
                .map(Questions::getQuestionName)
                .orElse(null);
    }
	
	public Optional<Users> getUser(int id) {
		return user_repo.findById(id);
	}
	
	public Optional<Questions> getQuestion(int id) {
		if(DataCache.ques_map.containsKey(id)) {
			Optional<Questions> q = Optional.of(DataCache.ques_map.get(id));
			return q;
		}
        return ques_repo.findById(id);
	}
	
	public List<Submissions> getAllSubmissionsByUserIdAndContestId(Integer userId, Integer contestId) {
		List<Submissions> list = new ArrayList<>();
//		for(int i=1;i<=subs_repo.count();i++) {
//			if(DataCache.sub_map.containsKey(i)) {
//				if(DataCache.sub_map.get(i).getUserId() == userId && DataCache.sub_map.get(i).getContestId() == contestId) list.add(DataCache.sub_map.get(i));
//				continue;
//			}
//			Optional<Submissions> s = subs_repo.findById(i);
//			if(s.isPresent()) {
//				if(!s.get().getVerdict().contains("Running")) DataCache.sub_map.put(i, s.get());
//				if(s.get().getUserId() == userId && s.get().getContestId() == contestId) list.add(s.get());
//			}
//		}
        return subs_repo.findByUserIdAndContestId(userId, contestId);
    }
	
	public List<Subs> subs(int id, HttpSession session, int user) {
		List<Submissions> list;
		if(user == 0) list = getAllSubmissionsByUserIdAndContestId((Integer) session.getAttribute("P"), id);
		else list = getAllSubmissionsByUserIdAndContestId(user, id);
		list = list.stream()
		        .sorted(Comparator.comparing(Submissions::getTimeSubmitted).reversed())
		        .collect(Collectors.toList());
		List<Subs> newlist = new ArrayList<>();
		for(Submissions sub : list) {
			int ques_id = sub.getQuestionId();
			newlist.add(new Subs(sub.getId(), (String)session.getAttribute("user"), getQuestionNameById(sub.getQuestionId()), sub.getVerdict(), sub.getContestId(), sub.getTimeExecution(), sub.getTimeSubmitted().plusHours(5).plusMinutes(30).toString().replace('T', ' ')));
		}
		return newlist;
	}
	
	class Subs{
		int id;
		String user;
		String question;
		String verdict;
		int contestId;
		int timeExecution;
		String timeSubmitted;
		
		public Subs(int id, String user, String question, String verdict, int contestId, int timeExecution, String timeSubmitted) {
	        this.id = id;
	        this.user = user;
	        this.question = question;
	        this.verdict = verdict;
	        this.contestId = contestId;
	        this.timeExecution = timeExecution;
	        this.timeSubmitted = timeSubmitted;
	    }
		
		public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    // Getter and Setter methods for user
	    public String getUser() {
	        return user;
	    }

	    public void setUser(String user) {
	        this.user = user;
	    }

	    // Getter and Setter methods for question
	    public String getQuestion() {
	        return question;
	    }

	    public void setQuestion(String question) {
	        this.question = question;
	    }

	    // Getter and Setter methods for verdict
	    public String getVerdict() {
	        return verdict;
	    }

	    public void setVerdict(String verdict) {
	        this.verdict = verdict;
	    }

	    // Getter and Setter methods for contestId
	    public int getContestId() {
	        return contestId;
	    }

	    public void setContestId(int contestId) {
	        this.contestId = contestId;
	    }

	    // Getter and Setter methods for timeExecution
	    public int getTimeExecution() {
	        return timeExecution;
	    }

	    public void setTimeExecution(int timeExecution) {
	        this.timeExecution = timeExecution;
	    }

	    // Getter and Setter methods for timeSubmitted
	    public String getTimeSubmitted() {
	        return timeSubmitted;
	    }

	    public void setTimeSubmitted(String timeSubmitted) {
	        this.timeSubmitted = timeSubmitted;
	    }
	}
	
	public List<Tests> getTests(int x, int y){
		return tests_repository.findAllByTestcaseNumberBetweenOrderByTestcaseNumberAsc(x, y);
	}
	
	public Optional<Tests> getTest(int id) {
		return tests_repository.findByTestcaseNumber(id);
	}
	
	@GetMapping("/submit-button")
    public String handleFormSubmission(HttpSession session, @RequestParam("ques-id") String quesId, @RequestParam("id") int contestid, Model model) {
		if(session.getAttribute("P") == null) return "test2.html";
		int userId = (Integer)(session.getAttribute("P"));
		model.addAttribute("status", "My Profile");
		model.addAttribute("num", quesId);
		model.addAttribute("id", contestid);
		Questions q = DataCache.ques_map.get(Integer.parseInt(quesId));
		model.addAttribute("name", q.getQuestionName());
        return "submit.html";
    }
	
	@PostMapping("/submit-code")
	public ResponseEntity<String> execute(HttpSession session, @RequestParam("code") String code, @RequestParam("ques-id") String quesId, @RequestParam("lang") String lang, Model model, @RequestParam("id") int contest_id) {
		System.out.println(lang);
		int sub = (int)subs_repo.count() + 1;
		session.setAttribute("sub-id", sub);
		try {
			code = URLDecoder.decode(code, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ZoneId istZone = ZoneId.of("Asia/Kolkata");
        // Get the current date and time in the specified time zone
        LocalDateTime istDateTime = LocalDateTime.now(istZone);
		LocalDateTime date = java.time.LocalDateTime.now();
		dataentry(session, code, "Running", sub, istDateTime.toString(), Integer.parseInt(quesId), contest_id, 0);
		if(!DataCache.ques_map.containsKey(Integer.parseInt(quesId))){
			getQuestion(Integer.parseInt(quesId)).ifPresent(question -> {
            	DataCache.ques_map.put(Integer.parseInt(quesId), question);
    			int st = question.getTestcaseStart();
    			int ed = question.getTestcaseEnd();
    			for(int i=st;i<=ed;i++) {
    				if(!DataCache.test_map.containsKey(i)) {
    					final int ii = i;
    					getTest(i).ifPresent(test -> {DataCache.test_map.put(ii, test);});
    				}
    			}
    		});
		}
		if(code.contains("ProcessBuilder") || code.contains("Executors") || code.contains("StreamHandler") || code.contains("CommandLine") || code.contains("Executor") || code.contains("getRuntime")) {
			return null;
		}
		if(code.contains("FILE") || code.contains("pipe") || code.contains("spawn.h") || code.contains("sys/wait.h") || code.contains("unistd.h") || code.contains("sys/types.h") || code.contains("pid_t")) {
			return null;
		}
		if(code.contains("import os") || code.contains("import subprocess")) return null;
		Questions quest = DataCache.ques_map.get(Integer.parseInt(quesId));
		int start = quest.getTestcaseStart();
		int end = quest.getTestcaseEnd();
		int time = 0;
		for(int i=start;i<=end;i++) {
			System.out.println("Running on Test " + (i - start + 1));
			//System.out.println(DataCache.test_map);
			Tests testcase = DataCache.test_map.get(i);
			String input = testcase.getInput().trim();
			String output = testcase.getOutput().trim();
			input = input.replace('$', ' ');
			String code_final = code;
			String verd = "";
			Pair p = null;
			if(lang.equals("1")) p = run(code_final, input, output);
			if(lang.equals("2")) p = run_cpp(code_final, input, output);
			if(lang.equals("3")) p = runpy(code_final, input, output);
			verd = p.f;
			String ti = p.s.substring(p.s.lastIndexOf(' ') + 1, p.s.length() - 2);
			int ppp = Integer.parseInt(ti);
			System.out.println(ppp);
			if(ppp > time) time = ppp;
			//String time = verd.substring(verd.lastIndexOf(' ')+1, verd.length());
			//time = time.substring(0, time.length()-2);
			//String time = verd.substring(verd.lastIndexOf(' ') + 1, verd.length());
			//verd = verd.substring(0, verd.lastIndexOf(' '));
			int te = i - start;
			te++;
			String fverd = "";
			if(verd.contains("Passed")) {
				System.out.println("Test passed ");
				fverd = "Running on Pretest " + (te + 1);
				if(i == end) fverd = "Pretests Passed";
				dataentry(session, code, fverd, sub, date.toString(), Integer.parseInt(quesId), contest_id, time);
				if(i == end) {
					Optional<Users> user = getUser(((Integer) session.getAttribute("P")));
					if(user.isPresent()) {
						String text = user.get().getQuestions();
						String[] ids = text.split(",");
						System.out.println(ids.length);
						boolean flag = false;
						for(String ele : ids) if(ele.equals(quesId)) {flag = true; break;}
						if(!flag) {
							text += quesId + ",";
							user.get().setQuestions(text);
							user_repo.save(user.get());
						}
					}
				}
			}
			else {
				System.out.println(verd);
				fverd = verd + " on Pretest " + (te);
				dataentry(session, code, fverd, sub, date.toString(), Integer.parseInt(quesId), contest_id, time);
				break;
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body("Request processed successfully");
	}
	
	public Pair run(String code, String input, String output) {
		String expected[] = output.trim().split("\n");
		String filename = extract(code);
		String og_class = filename;
		Random rand = new Random();
		filename += rand.nextInt(1000);
		code = code.replace(og_class, filename);
		File sourceFile = new File(filename + ".java");
        FileWriter writer;
		try {
			writer = new FileWriter(sourceFile);
			writer.write(code);
	        writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ExecutorService executor = Executors.newSingleThreadExecutor();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult = compiler.run(null, null, null, sourceFile.getPath());
		if(compilationResult != 0) return new Pair("Compilation Error ", " -1ms");
		InputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
	    String className = sourceFile.getName().replace(".java", "");
	    ProcessBuilder processBuilder = new ProcessBuilder("java", "-Xint", className);
	    processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
	    processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
        // Use a Future to track the execution
	    long start = System.currentTimeMillis();
        Future<Pair> future = executor.submit(() -> {
            // Compile the Java source file
		    Process process = processBuilder.start();
            // Write the input to the standard input of the process
            try (OutputStream outputStream = process.getOutputStream()) {
                byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                outputStream.write(inputBytes);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            long end = System.currentTimeMillis();   
            System.out.println(end - start + "ms");
            if(end - start >= 5000) {
            	System.out.println("gadbad");
            	throw new Exception();
            }
            String line;
		    int i = 0;
		    boolean flag = true;
		    String foutput = "";
		    while ((line = reader.readLine()) != null) {
		    	//System.out.println("output : " + line.trim());
		    	if(i == expected.length) flag = false;
		    	if(!line.trim().equals(expected[i++].trim())) flag = false;
		    	foutput += line.trim() + "\n";
            }
		    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		    while ((line = reader.readLine()) != null) {
		    	if(line.trim().length() > 0) return new Pair("Runtime Error ", foutput + " " + (end - start) + "ms");
	        }
		    //System.out.println(foutput);
		    if(i != expected.length) flag = false;
		    if(!flag) return new Pair("Wrong Answer ", foutput + " " + (end - start) + "ms");
		    return new Pair("Passed ", foutput + " " + (end - start) + "ms");
        });
        try {
            Pair result = future.get(20, TimeUnit.SECONDS); // 5 seconds timeout
            sourceFile.delete();
            return result;
        } catch (Exception e) {
            future.cancel(true);
            sourceFile.delete();
            //System.out.println("TLE ");
            return new Pair("Time Limit Exceeded  ", " -1ms");
        }
	}
	
	public Pair run_cpp(String f_code, String input, String output) {
		Random rand = new Random();
		int num = rand.nextInt(1000);
    	File sourceFile = new File("main.cpp");
        FileWriter writer;
        try {
            writer = new FileWriter(sourceFile);
            writer.write(f_code);
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String expected[] = output.trim().split("\n");
        ProcessBuilder processBuilder = new ProcessBuilder("g++", "-O0" , sourceFile.getPath(), "-o", "output" + num);
        Process compileProcess;
		try {
			compileProcess = processBuilder.start();
			int code = compileProcess.waitFor();
			if(code != 0) return new Pair("Compilation Error ", " -1ms");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Pair> future = executor.submit(() -> {
            ProcessBuilder processbuilder = new ProcessBuilder("./output" + num);
			processbuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
			processbuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
			long start = System.currentTimeMillis();
			try {
			    Process process = processbuilder.start();
			    try (OutputStream outputStream = process.getOutputStream()) {
			        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
			        outputStream.write(inputBytes);
			    }
			    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    long end = System.currentTimeMillis();
			    if(end - start >= 5000) throw new Exception();
			    String line;
			    int i = 0;
			    boolean flag = true;
			    String foutput = "";
			    while ((line = reader.readLine()) != null) {
			    	if(i == expected.length) flag = false;
			    	if(!line.trim().equals(expected[i++].trim())) flag = false;
			    	foutput += line.trim() + "\n";
			    }
			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    while ((line = reader.readLine()) != null) {
			    	if(line.trim().length() > 0) return new Pair("Runtime Error ", foutput + " " + (end - start) + "ms");
		        }
			    if(i != expected.length) flag = false;
			    if(!flag) return new Pair("Wrong Answer ", foutput + " " + (end - start) + "ms");
			    return new Pair("Passed ", foutput + " " + (end - start) + "ms");
			} catch (IOException e) {
			    e.printStackTrace();
			    return new Pair("Internal Error ", " -1ms");
			}
        });

        try {
            Pair result = future.get(10, TimeUnit.SECONDS); // 5 seconds timeout
            return result;
        } catch (Exception e) {
            future.cancel(true);
            //System.out.println("TLE ");
            return new Pair("Time Limit Exceeded  ", " -1 ms");
        }
	}
	
	public Pair runpy(String code, String input, String output) {
        String[] expected = output.trim().split("\n");
        ArrayList<Long> times = new ArrayList<>();
        // Write the Python code to a file
        Random rand = new Random();
        int num = rand.nextInt(1000);
        String file_name = "main" + num + ".py";
        File sourceFile = new File(file_name);
        try (FileWriter writer = new FileWriter(sourceFile)) {
            writer.write(code);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Pair> future = executor.submit(() -> {
            // Run Python code
            ProcessBuilder processBuilder = new ProcessBuilder("python3", sourceFile.getPath());
            processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

            try {
            	long start = System.currentTimeMillis();
                Process process = processBuilder.start();

                // Write the input to the standard input of the process
                try (OutputStream outputStream = process.getOutputStream()) {
                    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(inputBytes);
                }

                // Capture the output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                long end = System.currentTimeMillis();
                if(end - start >= 5000) throw new Exception();
                String line;
                int i = 0;
			    boolean flag = true;
			    String foutput = "";
			    while ((line = reader.readLine()) != null) {
			    	//System.out.println(line);
			    	if(i == expected.length) flag = false;
			    	if(!line.trim().equals(expected[i++].trim())) flag = false;
			    	foutput += line.trim() + "\n";
	            }
			    reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			    while ((line = reader.readLine()) != null) {
			    	if(line.trim().length() > 0) return new Pair("Runtime Error ", foutput + " " + (end - start) + "ms");
		        }
			    if(i != expected.length) flag = false;
			    if(!flag) return new Pair("Wrong Answer", foutput + " " + (end - start) + "ms");
			    return new Pair("Passed", foutput + " " + (end - start) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                return new Pair("Execution Error", " -1ms");
            }
        });
        try {
            Pair result = future.get(15, TimeUnit.SECONDS); // 5 seconds timeout
            return result;
        } catch (Exception e) {
            System.out.println("Time Out occurred ");
            future.cancel(true);
            //System.out.println("TLE ");
            return new Pair("Time Limit Exceeded ", " -1ms");
        }
    }
	
	@GetMapping("/subs-self")
	public String ex(@RequestParam("id") int id,  HttpSession session, Model model) {
		model.addAttribute("submissions", subs(id, session, 0));
		model.addAttribute("id", id);
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		int userId = (Integer) (session.getAttribute("P"));
		if(DataCache.queue.containsKey(userId)) model.addAttribute("reload", 1);
		return "submissions.html";
	}
	
	@GetMapping("/subs")
	public String exe(@RequestParam("id") int id, @RequestParam("user") int userid, HttpSession session, Model model) {
		if(session.getAttribute("P") == null) return "test2.html";
		model.addAttribute("submissions", subs(id, session, userid));
		model.addAttribute("id", id);
		model.addAttribute("status", "My Profile");
		int userId = (Integer) (session.getAttribute("P"));
		if(DataCache.queue.containsKey(userId)) model.addAttribute("reload", 1);
		return "submissions.html";
	}
	
	@GetMapping("/verdict")
	public String verdict(HttpSession session, Model model) {
		int id = (int) session.getAttribute("sub-id");
		Optional<Submissions> list = getSubmissionById(id);
		if(list.isPresent()) {
			model.addAttribute("id", list.get().getId());
			model.addAttribute("time", list.get().getTimeSubmitted());
			model.addAttribute("user", session.getAttribute("user"));
			model.addAttribute("quesid", list.get().getQuestionId());
			model.addAttribute("quesname", DataCache.ques_map.get(list.get().getQuestionId()).getQuestionName());
			model.addAttribute("verdict", list.get().getVerdict());
		}
		return "verdict.html";
	}
	
	public Optional<Submissions> getSubmissionById(Integer submissionId) {
        return subs_repo.findById(submissionId);
    }
	
	public String extract(String code) {
	   String className = null;
       Pattern pattern = Pattern.compile("public\\s+class\\s+(\\w+)");
       Matcher matcher = pattern.matcher(code);
       if (matcher.find()) className = matcher.group(1);
       else className = "Main";
       return className;
	}
	
	public void dataentry(HttpSession session, String code, String verdict, int sub_id, String date, int ques_id, int contest_id, int time) {
		String dateString = date.substring(0, date.lastIndexOf('.'));
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	    LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
		Submissions sub = new Submissions(sub_id, (Integer) session.getAttribute("P"), ques_id, code, verdict, contest_id, time, dateTime);
		addSubmission(sub);
	}
	
	public void addSubmission(Submissions submission) {
        subs_repo.save(submission);
    }

}
