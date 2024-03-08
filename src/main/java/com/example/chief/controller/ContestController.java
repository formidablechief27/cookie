package com.example.chief.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class ContestController {
	ContestsRepository contest;
	QuestionsRepository questions;
	UserRepository user_repo;
	
	@Autowired
	ContestController(ContestsRepository c, QuestionsRepository q, UserRepository user){
		contest = c;
		questions = q;
		user_repo = user;
	}

	public List<Contests> getAllContests() {
        return contest.findAllByOrderByStartDesc();
    }
	
	public Optional<Contests> getContest(Long id) {
		return contest.findById(id);
	}
	
	public class Contest {
	    private Long id;
	    private String start;
	    private String title;
	    private String end;

	    public Contest(Long id, String start, String end, String title) {
	        this.id = id;
	        this.start = start;
	        this.end = end;
	        this.title = title;
	    }

	    // Getter methods
	    public Long getId() {
	        return id;
	    }

	    public String getStart() {
	        return start;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public String getEnd() {
	        return end;
	    }

	    // Setter methods
	    public void setId(Long id) {
	        this.id = id;
	    }

	    public void setStart(String start) {
	        this.start = start;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public void setEnd(String end) {
	        this.end = end;
	    }
	}

	
	@GetMapping("/contest-list")
	public String main(HttpSession session, Model model) {
		System.out.println(DataCache.user_map.size());
		List<Contests> list = getAllContests();
		List<Contests> ls = new ArrayList<>();
		for(Contests c : list) if(c.getId() > 15) ls.add(c);
		List<Contest> fls = new ArrayList<>();
		for(int i=0;i<ls.size();i++) {
			Contests c = ls.get(i);
			c.setStart(c.getStart().plusHours(5).plusMinutes(30));
			c.setEd(c.getEd().plusHours(5).plusMinutes(30));
			Contest con = new Contest(c.getId(), c.getStart().toString().replace('T', ' '), c.getEd().toString().replace('T', ' '), c.getTitle());
			fls.add(con);
		}
		model.addAttribute("contests", fls);
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		model.addAttribute("cname", "Contests for Practice");
		return "home.html";
	}
	
	@GetMapping("/practice-course")
	public String main2(HttpSession session, Model model) {
		List<Contests> list = getAllContests();
		List<Contests> ls = new ArrayList<>();
		for(Contests c : list) if(c.getId() <= 15) ls.add(c);
		//model.addAttribute("contests", ls);
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		model.addAttribute("cname", "DSA Course launching on June 1 ;)");
		return "home.html";
	}
	
	public List<Questions> getQuestionsByContestId(Integer contestId) {
        return questions.findByContestId(contestId);
    }
	
	@GetMapping("/contest-load")
	public String load_contest(HttpSession session, @RequestParam("id") Integer id, Model model) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else {
			model.addAttribute("status", "My Profile");
			Optional<Users> user = user_repo.findById((Integer)session.getAttribute("P"));
			String text = user.get().getQuestions();
			String p[] = text.split(",");
	    	ArrayList<Integer> green = new ArrayList<>();
	    	for(String ele : p) {
	    		if(ele.trim().length() > 0) {
	    			int quesId = Integer.parseInt(ele.trim());
	    			green.add(quesId);
	    		}
	    	}
	    	model.addAttribute("queslist", green);
			
		}
		List<Questions> list = getQuestionsByContestId(id);
		Optional<Contests> c = getContest((long)id);
		LocalDateTime time = c.get().getStart();
		LocalDateTime endTime = LocalDateTime.now(); // Replace with your end time
        Duration duration = Duration.between(endTime, time);
        long totalMinutes = duration.toMinutes();
        long totalSeconds = duration.getSeconds();
        long remainingSeconds = totalSeconds % 60;
        System.out.println("Total Minutes: " + totalMinutes);
        System.out.println("Total Seconds: " + totalSeconds);
        System.out.println("Remaining Seconds (excluding minutes): " + remainingSeconds);
        if(totalSeconds > 0) {
        	if(c.isPresent()) model.addAttribute("name", c.get().getTitle());
        	model.addAttribute("min", totalMinutes);
        	model.addAttribute("sec", remainingSeconds);
        	model.addAttribute("id", id);
        	return "timer.html";
        }
		List<Questions> flist = new ArrayList<>(list.size());
		for(int i=0;i<list.size();i++) flist.add(null);
		int start = c.get().getSt();
		for(Questions Q : list) {
			int qid = Q.getId();
			int index = qid - start;
			flist.set(index, Q);
		}
		if(c.isPresent()) model.addAttribute("title", c.get().getTitle());
		model.addAttribute("id", id);
		model.addAttribute("questions", flist);
		model.addAttribute("name", c.get().getTitle());
		return "contesthome.html";
	}
	
	@GetMapping("/error")
	public String error(HttpSession session, Model model) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		return "error.html";
	}
	
	@GetMapping("/problemset")
	public String problems(HttpSession session, Model model, @RequestParam(name = "diff", required = false) String diff) {
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else {
			model.addAttribute("status", "My Profile");
			Optional<Users> user = user_repo.findById((Integer)session.getAttribute("P"));
			String text = user.get().getQuestions();
			String p[] = text.split(",");
	    	ArrayList<Integer> green = new ArrayList<>();
	    	for(String ele : p) {
	    		if(ele.trim().length() > 0) {
	    			int quesId = Integer.parseInt(ele.trim());
	    			green.add(quesId);
	    		}
	    	}
	    	model.addAttribute("queslist", green);
			
		}
		List<Questions> list = questions.findAll();
		List<Questions> flist = new ArrayList<>();
		for(Questions Q : list) {
			if(Q.getId() > 200) {
				if(diff == null) {
					flist.add(Q);
					continue;
				}
				if(diff.equals("Newbie") && Q.getMinPoints() < 1200) flist.add(Q);
				else if (diff.equals("Pupil") && Q.getMinPoints() >= 1200 && Q.getMinPoints() < 1400) flist.add(Q);
				else if (diff.equals("Specialist") && Q.getMinPoints() >= 1400 && Q.getMinPoints() < 1600) flist.add(Q);
				else if (diff.equals("Expert") && Q.getMinPoints() >= 1600 && Q.getMinPoints() < 1900) flist.add(Q);
				else if (diff.equals("Candidate Master") && Q.getMinPoints() >= 1900) flist.add(Q);
			}
		}
		model.addAttribute("questions", flist);
		return "problemset.html";
	}
}
