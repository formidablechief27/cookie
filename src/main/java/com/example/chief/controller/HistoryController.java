package com.example.chief.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chief.model.*;
import com.example.chief.repository.*;

@Controller
public class HistoryController {
	
	private TestsRepository tests_repository;
	private QuestionsRepository ques_repo;
	private SubmissionsRepository subs_repo;
	private UserRepository user_repo;
	private ContestsRepository contest_repo;
	
	@Autowired
    public HistoryController(TestsRepository tests_Repository, QuestionsRepository ques_repo, SubmissionsRepository subsrepo, UserRepository user_repo, ContestsRepository c) {
        this.tests_repository = tests_Repository;
        this.ques_repo = ques_repo;
        this.subs_repo = subsrepo;
        this.user_repo = user_repo;
        contest_repo = c;
    }
	
	public Optional<Users> getContestById2(Integer id) {
        return user_repo.findById(id);
    }
	
	public Optional<Questions> getContestById1(Integer id) {
        return ques_repo.findById(id);
    }
	
	public Optional<Submissions> getContestById(Integer id) {
        return subs_repo.findById(id);
    }

	@GetMapping("/sub-view") 
	public String view(@RequestParam("sub-id") int id, @RequestParam("id") int contestid, Model model) {
		Optional<Submissions> sub = getContestById(id);
		if(sub.isPresent()) {
			Submissions s = sub.get();
			List<Subs> list = new ArrayList<>();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String formattedDateTime = s.getTimeSubmitted().format(formatter);
			Subs obj = new Subs(s.getId(), getContestById2(s.getUserId()).get().getUsername(), getContestById1(s.getQuestionId()).get().getQuestionName(), s.getVerdict(), s.getContestId(), s.getTimeExecution(), formattedDateTime);
			list.add(obj);
			String code = s.getCode();
			model.addAttribute("code", code);
			model.addAttribute("submissions", list);
			model.addAttribute("id", contestid);
		}
		return "code-view.html";
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
	
}
