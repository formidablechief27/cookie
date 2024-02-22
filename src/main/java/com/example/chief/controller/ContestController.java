package com.example.chief.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	public String main(Model model) {
		List<Contests> list = getAllContests();
		List<Contests> ls = new ArrayList<>();
		for(Contests c : list) if(c.getId() > 15) ls.add(c);
		model.addAttribute("contests", ls);
		return "home.html";
	}
	
	@GetMapping("/practice-course")
	public String main2(Model model) {
		List<Contests> list = getAllContests();
		List<Contests> ls = new ArrayList<>();
		for(Contests c : list) if(c.getId() <= 15) ls.add(c);
		model.addAttribute("contests", ls);
		return "home.html";
	}
	
	public List<Questions> getQuestionsByContestId(Integer contestId) {
        return questions.findByContestId(contestId);
    }
	
	@GetMapping("/contest-load")
	public String load_contest(@RequestParam("id") Integer id, Model model) {
		List<Questions> list = getQuestionsByContestId(id);
		Optional<Contests> c = getContest((long)id);
		if(c.isPresent()) model.addAttribute("title", c.get().getTitle());
		model.addAttribute("id", id);
		model.addAttribute("questions", list);
		model.addAttribute("name", c.get().getTitle());
		return "contesthome.html";
	}
	
	@GetMapping("/error")
	public String error() {
		return "error.html";
	}
}
