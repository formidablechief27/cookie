package com.example.chief.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chief.model.*;
import com.example.chief.repository.*;

@Controller
public class LoginController {
	
	UserRepository user_repo;
	ContestsRepository contest;
	long mod = (long)1e18;
	
	@Autowired
	public LoginController(UserRepository user_repo, ContestsRepository c) {
		this.user_repo = user_repo;
		contest = c;
	}
	
	public List<Contests> getAllContests() {
        return contest.findAllByOrderByStartDesc();
    }
	
	public long getTotalUserCount() {
        return user_repo.count();
    }
	
	@GetMapping("/")
	public String start(HttpSession session, Model model) {
		Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        totalMemory /= 1024;
        totalMemory /= 1024;
        freeMemory /= 1024;
        freeMemory /= 1024;
        usedMemory /= 1024;
        usedMemory /= 1024;
        
        System.out.println("Total Memory: " + totalMemory + " mb");
        System.out.println("Free Memory: " + freeMemory + " mb");
        System.out.println("Used Memory: " + usedMemory + " mb");
		if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		else model.addAttribute("status", "My Profile");
		return "start.html";
	}
	
	@GetMapping("/home")
	public String h(Model model) {
		model.addAttribute("val", 1);
		return "test2.html";
	}
	
	@GetMapping("/reg")
	public String reg() {
		return "test.html";
	}
	
	@GetMapping("/log")
	public String log() {
		return "test2.html";
	}
	
	@PostMapping("/register")
    public String registerUser(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("email") String email, @RequestParam("password") String password, Model model) {
		long pass = 0;
		for(int i=0;i<password.length();i++) {
			pass = mul(pass, 31);
			pass = add(pass, password.charAt(i));
		}
		Users user = new Users(username, email, pass, 0, 0, ",");
		Optional<Users> existingUser = user_repo.findByUsername(username);
		if(existingUser.isPresent()) {
			model.addAttribute("msg", 1);
			return "test.html";
		}
		else {
			user_repo.save(user);
			HttpSession session = request.getSession();
			Optional<Users> existing = user_repo.findByUsername(username);
			DataCache.user_map.put(existing.get().getId(), user);
			session.setAttribute("P", existing.get().getId());
			session.setAttribute("user", username);
			model.addAttribute("val", 1);
			return "test.html";
		}
    }
	
	@PostMapping("/login")
    public String LoginUser(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("password") String password, Model model) {
		Optional<Users> existingUser = user_repo.findByUsername(username);
		long pass2 = 0;
		for(int i=0;i<password.length();i++) {
			pass2 = mul(pass2, 31);
			pass2 = add(pass2, password.charAt(i));
		}
		if(existingUser.isPresent()) {
			HttpSession session = request.getSession();
			long pass = existingUser.get().getPassword();
			if(pass == pass2) {
				DataCache.user_map.put(existingUser.get().getId(), existingUser.get());
				session.setAttribute("P", existingUser.get().getId());
				session.setAttribute("user", existingUser.get().getUsername());
				model.addAttribute("val", 1);
				return "test2.html";
			}
			else {
				model.addAttribute("msg", 1);
				return "test2.html";
			}
		}
		else {
			model.addAttribute("msg", 2);
			return "test2.html";
		}
    }
	
	long add(long a, long b) {return (((a + mod) % mod + (b + mod) % mod) % mod);}
    long sub(long a, long b) {return (((a + mod) % mod + ((-b) + mod) % mod) % mod);}
    long mul(long a, long b) {return ((a % mod * b % mod) % mod);}
    long inv(long x) {return pow(x, mod - 2);}
    long div(long x, long y) {return mul(x, inv(y));}
    long pow(long a, long b) {a %= mod;long res = 1;while (b > 0) {if ((b & 1) != 0)res = mul(res, a);a = mul(a, a);b /= 2;}return res;}
    
}
