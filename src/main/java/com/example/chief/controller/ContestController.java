package com.example.chief.controller;

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
	
	@Autowired
	ContestController(ContestsRepository c, QuestionsRepository q){
		contest = c;
		questions = q;
	}

	public List<Contests> getAllContests() {
        return contest.findAllByOrderByStartDesc();
    }
	
	public Optional<Contests> getContest(Long id) {
		return contest.findById(id);
	}
	
	@GetMapping("/contest-list")
	public String main(HttpSession session, Model model) {
		System.out.println(DataCache.user_map.size());
		List<Contests> list = getAllContests();
		List<Contests> ls = new ArrayList<>();
		for(Contests c : list) if(c.getId() > 15) ls.add(c);
		model.addAttribute("contests", ls);
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
		else model.addAttribute("status", "My Profile");
		List<Questions> list = getQuestionsByContestId(id);
		Optional<Contests> c = getContest((long)id);
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
		else model.addAttribute("status", "My Profile");
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
