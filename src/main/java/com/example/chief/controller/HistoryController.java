package com.example.chief.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chief.controller.CodeController.Subs;
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
	public String view(@RequestParam("sub-id") int id, @RequestParam("id") int contestid, Model model, HttpSession session) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
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
	
	@GetMapping("/user")
	public String user(Model model, @RequestParam("username") String username) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		Optional<Users> user = user_repo.findByUsername(username);
		List<Subs> list = subs(user.get().getId());
    	ArrayList<Long> flist = new ArrayList<>();
    	flist.add(0L); flist.add(0L); flist.add(0L); flist.add(0L);
    	for(Subs S : list) {
    		if(S.getVerdict().contains("Passed") || S.getVerdict().contains("Accepted")) flist.set(0, flist.get(0) + 1L);
    		if(S.getVerdict().contains("Wrong")) flist.set(1, flist.get(1) + 1L);
    		if(S.getVerdict().contains("Time")) flist.set(2, flist.get(2) + 1L);
    		if(S.getVerdict().contains("Runtime")) flist.set(3, flist.get(3) + 1L);
    	}
    	String text = user.get().getQuestions();
    	String p[] = text.split(",");
    	int count = 0;
    	for(String ele : p) if(ele.trim().length() > 0) count++;
    	model.addAttribute("solved", count);
    	model.addAttribute("rating", user.get().getRating());
    	model.addAttribute("uname", user.get().getUsername());
    	model.addAttribute("list", flist);
    	model.addAttribute("rank", "Newbie");
    	model.addAttribute("id", (Integer)session.getAttribute("P"));
		return "profile.html";
	}
	
	@GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
    	if(session.getAttribute("P") == null) return "test2.html";
    	if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
    	Optional<Users> user = user_repo.findById((Integer)session.getAttribute("P"));
    	List<Subs> list = subs(user.get().getId());
    	ArrayList<Long> flist = new ArrayList<>();
    	flist.add(0L); flist.add(0L); flist.add(0L); flist.add(0L);
    	for(Subs S : list) {
    		if(S.getVerdict().contains("Passed") || S.getVerdict().contains("Accepted")) flist.set(0, flist.get(0) + 1L);
    		if(S.getVerdict().contains("Wrong")) flist.set(1, flist.get(1) + 1L);
    		if(S.getVerdict().contains("Time")) flist.set(2, flist.get(2) + 1L);
    		if(S.getVerdict().contains("Runtime")) flist.set(3, flist.get(3) + 1L);
    	}
    	String text = user.get().getQuestions();
    	String p[] = text.split(",");
    	int count = 0;
    	for(String ele : p) if(ele.trim().length() > 0) count++;
    	model.addAttribute("solved", count);
    	model.addAttribute("rating", user.get().getRating());
    	model.addAttribute("uname", user.get().getUsername());
    	model.addAttribute("list", flist);
    	model.addAttribute("rank", "Newbie");
    	model.addAttribute("id", (Integer)session.getAttribute("P"));
		return "profile.html";
    }
	
	@GetMapping("/prob-list")
	public String problems(Model model, @RequestParam("id") int id) {
		Optional<Users> user = user_repo.findById(id);
		String text = user.get().getQuestions();
		String p[] = text.split(",");
    	List<Questions> list = new ArrayList<>();
    	for(String ele : p) {
    		if(ele.trim().length() > 0) {
    			int quesId = Integer.parseInt(ele.trim());
    			Optional<Questions> ques = ques_repo.findById(quesId);
    			list.add(ques.get());
    		}
    	}
    	model.addAttribute("questions", list);
		return "display.html";
	}
	
	public String getQuestionNameById(int questionId) {
        return ques_repo.findById(questionId)
                .map(Questions::getQuestionName)
                .orElse(null);
    }
	
	public List<Subs> subs(int user) {
		List<Submissions> list;
		list = subs_repo.findByUserId(user);
		list = list.stream()
		        .sorted(Comparator.comparing(Submissions::getTimeSubmitted).reversed())
		        .collect(Collectors.toList());
		List<Subs> newlist = new ArrayList<>();
		Optional<Users> us = user_repo.findById(user);
		for(Submissions sub : list) newlist.add(new Subs(sub.getId(), us.get().getUsername(), getQuestionNameById(sub.getQuestionId()), sub.getVerdict(), sub.getContestId(), sub.getTimeExecution(), sub.getTimeSubmitted().toString()));
		return newlist;
	}
	
	@GetMapping("/sub-list") 
	public String subs(Model model, @RequestParam("id") int id, @RequestParam(name = "type", required = false) String type) {
		List<Subs> list = subs(id);
		List<Subs> flist = new ArrayList<>();
		for(Subs S : list) {
			if(type == null) {
				flist.add(S);
				continue;
			}
			else if (S.getVerdict().contains(type)) flist.add(S);
		}
		model.addAttribute("uid", id);
		model.addAttribute("submissions", flist);
		return "displaysub.html";
	}
	
}
