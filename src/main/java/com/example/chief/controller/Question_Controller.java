package com.example.chief.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chief.model.*;
import com.example.chief.repository.*;

@Controller
public class Question_Controller {
	private QuestionsRepository questions_repository;
	private TestsRepository tests_repository;
	
	@Autowired
    public Question_Controller(QuestionsRepository questionsRepository, TestsRepository tests_Repository) {
        this.questions_repository = questionsRepository;
        this.tests_repository = tests_Repository;
    }

	public Optional<Questions> getQuestion(int id) {
		return questions_repository.findById(id);
	}
	
	public List<Tests> getTests(int x, int y){
		return tests_repository.findAllByTestcaseNumberBetweenOrderByTestcaseNumberAsc(x, y);
	}
	
	public Optional<Tests> getTest(int id) {
		return tests_repository.findByTestcaseNumber(id);
	}
	
	@GetMapping("/start")
    public String handleFormSubmission(@RequestParam("ques-id") int buttonId,@RequestParam("id") int contestid, Model model, HttpSession session) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		int ques = buttonId;
        if(DataCache.ques_map.containsKey(ques)) {
        	Questions question = DataCache.ques_map.get(ques);
        	model.addAttribute("question_name", question.getQuestionName());
			model.addAttribute("question", question.getQuestion());
			model.addAttribute("input", question.getInputFormat());
			model.addAttribute("output", question.getOutputFormat());
			model.addAttribute("points", question.getMinPoints());
			int st = question.getTestcaseStart();
			int ed = question.getTestcaseEnd();
			for(int i=st;i<=ed;i++) {
				if(!DataCache.test_map.containsKey(i)) {
					final int ii = i;
					getTest(i).ifPresent(test -> {DataCache.test_map.put(ii, test);});
				}
			}
			model.addAttribute("input1", DataCache.test_map.get(st).getInput());
			model.addAttribute("output1", DataCache.test_map.get(st).getOutput());
			model.addAttribute("explain1", DataCache.test_map.get(st).getExplain());
			model.addAttribute("input2", DataCache.test_map.get(st+1).getInput());
			model.addAttribute("output2", DataCache.test_map.get(st+1).getOutput());
			model.addAttribute("explain2", DataCache.test_map.get(st+1).getExplain());
			model.addAttribute("input3", DataCache.test_map.get(st+2).getInput());
			model.addAttribute("output3", DataCache.test_map.get(st+2).getOutput());
			model.addAttribute("explain3", DataCache.test_map.get(st+2).getExplain());
			model.addAttribute("num", question.getId());
        }
        else {
        	getQuestion(ques).ifPresent(question -> {
            	DataCache.ques_map.put(ques, question);
    			model.addAttribute("question_name", question.getQuestionName());
    			model.addAttribute("question", question.getQuestion());
    			model.addAttribute("input", question.getInputFormat());
    			model.addAttribute("output", question.getOutputFormat());
    			model.addAttribute("points", question.getMinPoints());
    			int st = question.getTestcaseStart();
    			int ed = question.getTestcaseEnd();
    			for(int i=st;i<=ed;i++) {
    				if(!DataCache.test_map.containsKey(i)) {
    					final int ii = i;
    					getTest(i).ifPresent(test -> {DataCache.test_map.put(ii, test);});
    				}
    			}
    			model.addAttribute("input1", DataCache.test_map.get(st).getInput());
    			model.addAttribute("output1", DataCache.test_map.get(st).getOutput());
    			model.addAttribute("explain1", DataCache.test_map.get(st).getExplain());
    			model.addAttribute("input2", DataCache.test_map.get(st+1).getInput());
    			model.addAttribute("output2", DataCache.test_map.get(st+1).getOutput());
    			model.addAttribute("explain2", DataCache.test_map.get(st+1).getExplain());
    			model.addAttribute("input3", DataCache.test_map.get(st+2).getInput());
    			model.addAttribute("output3", DataCache.test_map.get(st+2).getOutput());
    			model.addAttribute("explain3", DataCache.test_map.get(st+2).getExplain());
    			model.addAttribute("num", question.getId());
    		});
        }
        model.addAttribute("ques-id", buttonId);
        model.addAttribute("id", contestid);
        return "question.html";
    }
	
	@GetMapping("/tutorial")
	public String tutorial(Model model, @RequestParam("ques-id") int ques_id, @RequestParam("id") int cid, HttpSession session) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		model.addAttribute("id", cid);
		model.addAttribute("qid", ques_id);
		return "tutorial.html";
	}
}