package com.example.chief.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.chief.model.*;
import com.example.chief.repository.*;

@Controller
public class Board_Controller {
	
	private TestsRepository tests_repository;
	private QuestionsRepository ques_repo;
	private SubmissionsRepository subs_repo;
	private UserRepository user_repo;
	private ContestsRepository contest_repo;
	
	@Autowired
    public Board_Controller(TestsRepository tests_Repository, QuestionsRepository ques_repo, SubmissionsRepository subsrepo, UserRepository user_repo, ContestsRepository c) {
        this.tests_repository = tests_Repository;
        this.ques_repo = ques_repo;
        this.subs_repo = subsrepo;
        this.user_repo = user_repo;
        contest_repo = c;
    }
	
	public String getUserById(int Id) {
        return user_repo.findById(Id)
                .map(Users::getUsername)
                .orElse(null);
    }
	
	public Optional<Contests> getContestById(Long id) {
        return contest_repo.findById(id);
    }
	
	public List<Submissions> getAllSubmissionsByContestId(int contestId) {
        return subs_repo.findByContestId(contestId);
    }
	
	@GetMapping("/leaderboard")
	public String leaderboard(Model model) {
		List<Score> list = new ArrayList<>();
		Optional<Contests> contest = getContestById(1L);
		int count = contest.get().getCount();
		long blank[] = new long[count];
		List<Submissions> tlist = getAllSubmissionsByContestId(1);
		HashMap<String, long[]> map = new HashMap<>();
		HashMap<String, long[]> mat = new HashMap<>();
		HashMap<String, long[]> pen = new HashMap<>();
		tlist = tlist.stream()
		        .sorted(Comparator.comparing(Submissions::getTimeSubmitted))
		        .collect(Collectors.toList());
		for(Submissions sub : tlist) {
			String u = getUserById(sub.getUserId());
			LocalDateTime submissionTime = sub.getTimeSubmitted();
			LocalDate today = LocalDate.now();
			LocalTime targetTime = LocalTime.of(18, 57);
			LocalDateTime todayTargetTime = LocalDateTime.of(today, targetTime);
			if (submissionTime.isBefore(todayTargetTime)) continue;
			LocalTime time = sub.getTimeSubmitted().toLocalTime();
	        long minutesPassed = ChronoUnit.MINUTES.between(targetTime, time);
	        if(!map.containsKey(u)) {map.put(u, blank); mat.put(u, blank); pen.put(u, blank);}
			int ques_id = sub.getQuestionId()%10;
	        if(sub.getVerdict().endsWith("Wrong") && sub.getVerdict().endsWith("Time") && !sub.getVerdict().endsWith("1")) {
	        	pen.get(u)[ques_id-1] ++;
	        }
	        if(sub.getVerdict().endsWith("Passed")) {
	        	int penalty = (int) pen.get(u)[ques_id-1];
	        	map.get(u)[ques_id-1] = 500*ques_id - minutesPassed*2*ques_id - 50*penalty;
	        	System.out.println(ques_id + " " + minutesPassed + " " + ques_id + " " + penalty);
	        	mat.get(u)[ques_id-1] = minutesPassed;
	        	pen.get(u)[ques_id-1] ++;
	        }
		}
		for(Map.Entry<String, long[]> entry : map.entrySet()) {
			long a[] = entry.getValue();
			long b[] = mat.get(entry.getKey());
			long c[] = pen.get(entry.getKey());
			System.out.println(a[0] + " " + a[1] + " " + a[2] + " " + a[3] + " " + a[4] + " " + a[5]);
			String A = "", B = "", C = "", D = "", E = "", F = "";
			String tA = "", tB = "", tC = "", tD = "", tE = "", tF = "";
			if(b[0] >= 60) {
				if(b[0] - 60 < 10) tA = "1:0" + (b[0] - 60);
				else tA = "1:" + (b[0] - 60);
			}
			else {
				if(b[0] < 10) tA = "0:0" + (b[0]);
				else tA = "0:" + (b[0]);
			}
			if(b[1] >= 60) {
				if(b[1] - 60 < 10) tB = "1:0" + (b[1] - 60);
				else tB = "1:" + (b[1] - 60);
			}
			else {
				if(b[1] < 10) tB = "0:0" + (b[1]);
				else tB = "0:" + (b[1]);
			}
			if(b[2] >= 60) {
				if(b[2] - 60 < 10) tC = "1:0" + (b[2] - 60);
				else tC = "1:" + (b[2] - 60);
			}
			else {
				if(b[2] < 10) tC = "0:0" + (b[2]);
				else tC = "0:" + (b[2]);
			}
			if(b[3] >= 60) {
				if(b[3] - 60 < 10) tD = "1:0" + (b[3] - 60);
				else tD = "1:" + (b[3] - 60);
			}
			else {
				if(b[3] < 10) tD = "0:0" + (b[3]);
				else tD = "0:" + (b[3]);
			}
			if(b[4] >= 60) {
				if(b[4] - 60 < 10) tE = "1:0" + (b[4] - 60);
				else tE = "1:" + (b[4] - 60);
			}
			else {
				if(b[4] < 10) tE = "0:0" + (b[4]);
				else tE = "0:" + (b[4]);
			}
			if(b[5] >= 60) {
				if(b[5] - 60 < 10) tF = "1:0" + (b[5] - 60);
				else tF = "1:" + (b[5] - 60);
			}
			else {
				if(b[5] < 10) tF = "0:0" + (b[5]);
				else tF = "0:" + (b[5]);
			}
			if(a[0] != 0) A = a[0] + ""; else if (c[0] < 0) A = -c[0] + ""; if(a[0] <= 0) tA = "";
			if(a[1] != 0) B = a[1] + ""; else if (c[1] < 0) B = -c[1] + ""; if(a[1] <= 0) tB = "";
			if(a[2] != 0) C = a[2] + ""; else if (c[2] < 0) C = -c[2] + ""; if(a[2] <= 0) tC = "";
			if(a[3] != 0) D = a[3] + ""; else if (c[3] < 0) D = -c[3] + ""; if(a[3] <= 0) tD = "";
			if(a[4] != 0) E = a[4] + ""; else if (c[4] < 0) E = -c[4] + ""; if(a[4] <= 0) tE = "";
			if(a[5] != 0) F = a[5] + ""; else if (c[5] < 0) F = -c[5] + ""; if(a[5] <= 0) tF = "";
			Score s = new Score(entry.getKey(), (int)(Math.max(0,  a[0]) + Math.max(0,  a[1]) + Math.max(0,  a[2]) + Math.max(0,  a[3]) + Math.max(0,  a[4]) + Math.max(0,  a[5])), A, B, C, D, E, F, tA, tB, tC, tD, tE, tF);
			list.add(s);
		}
		sortScoresDescending(list);
		int rank = 1;
		for(Score S : list) S.setRank(rank++);
		model.addAttribute("scores", list);
		return "leaderboad.html";
	}
	
	 public void sortScoresDescending(List<Score> scores) {
	        // Using Collections.sort with a custom comparator
	        Collections.sort(scores, new Comparator<Score>() {
	            @Override
	            public int compare(Score score1, Score score2) {
	                // Compare in descending order based on total score
	                return Integer.compare(score2.getTotal(), score1.getTotal());
	            }
	        });
	    }
	
	 public class Score {
		    private String name;
		    private int total;
		    private String A;
		    private String B;
		    private String C;
		    private String D;
		    private String E;
		    private String F;
		    private String timeA;
		    private String timeB;
		    private String timeC;
		    private String timeD;
		    private String timeE;
		    private String timeF;
		    private int rank;

		    // Constructor
		    public Score(String name, int total, String A, String B, String C, String D, String E, String F,
		                 String timeA, String timeB, String timeC, String timeD, String timeE, String timeF) {
		        this.name = name;
		        this.total = total;
		        this.A = A;
		        this.B = B;
		        this.C = C;
		        this.D = D;
		        this.E = E;
		        this.F = F;
		        this.timeA = timeA;
		        this.timeB = timeB;
		        this.timeC = timeC;
		        this.timeD = timeD;
		        this.timeE = timeE;
		        this.timeF = timeF;
		    }

		    // Getter methods
		    public String getName() {
		        return name;
		    }

		    public int getTotal() {
		        return total;
		    }

		    public String getA() {
		        return A;
		    }

		    public String getB() {
		        return B;
		    }

		    public String getC() {
		        return C;
		    }

		    public String getD() {
		        return D;
		    }

		    public String getE() {
		        return E;
		    }

		    public String getF() {
		        return F;
		    }

		    public String getTimeA() {
		        return timeA;
		    }

		    public String getTimeB() {
		        return timeB;
		    }

		    public String getTimeC() {
		        return timeC;
		    }

		    public String getTimeD() {
		        return timeD;
		    }

		    public String getTimeE() {
		        return timeE;
		    }

		    public String getTimeF() {
		        return timeF;
		    }

		    // Setter methods
		    public void setName(String name) {
		        this.name = name;
		    }

		    public void setTotal(int total) {
		        this.total = total;
		    }

		    public void setA(String A) {
		        this.A = A;
		    }

		    public void setB(String B) {
		        this.B = B;
		    }

		    public void setC(String C) {
		        this.C = C;
		    }

		    public void setD(String D) {
		        this.D = D;
		    }

		    public void setE(String E) {
		        this.E = E;
		    }

		    public void setF(String F) {
		        this.F = F;
		    }

		    public void setTimeA(String timeA) {
		        this.timeA = timeA;
		    }

		    public void setTimeB(String timeB) {
		        this.timeB = timeB;
		    }

		    public void setTimeC(String timeC) {
		        this.timeC = timeC;
		    }

		    public void setTimeD(String timeD) {
		        this.timeD = timeD;
		    }

		    public void setTimeE(String timeE) {
		        this.timeE = timeE;
		    }

		    public void setTimeF(String timeF) {
		        this.timeF = timeF;
		    }
		    
		    public int getRank() {
		    	return rank;
		    }
		    
		    public void setRank(int val) {
		    	rank = val;
		    }
		}
	 
	 @GetMapping("/board")
	 public String go(HttpSession session, @RequestParam("id") int contestid, Model model) {
		 if(session.getAttribute("P") == null) model.addAttribute("status", "Login");
		 else model.addAttribute("status", "My Profile");
		 int probs = 0;
		 Long key = (long) contestid;
		 Optional<Contests> contest = getContestById(key);
		 Contests con = contest.get();
		 System.out.println(con.getCount());
		 probs = con.getCount();
		 model.addAttribute("numberOfColumns", con.getCount());
		 List<Submissions> list = getAllSubmissionsByContestId(contestid);
		 HashMap<Integer, int[]> map = new HashMap<>();
		 for(Submissions sub : list) {
			 int user = sub.getUserId();
			 if(!map.containsKey(user)) {
				 int empty[] = new int[probs];
				 map.put(user, empty); 
			 }
			 int ques_id = sub.getQuestionId();
			 int st = con.getSt();
			 int ind = ques_id - st;
			 if(sub.getVerdict().contains("Passed") || sub.getVerdict().contains("Accepted")) map.get(user)[ind] = 1;
			 if((sub.getVerdict().contains("Time") || sub.getVerdict().contains("Wrong")) && map.get(user)[ind] == 0) map.get(user)[ind] = -1;
		 }
		 List<String> arr[] = new ArrayList[map.size()];
		 for(int i=0;i<map.size();i++) arr[i] = new ArrayList<>();
		 int ind = 0;
		 for(Map.Entry<Integer, int[]> entry : map.entrySet()) {
			 String user = getUserById(entry.getKey());
			 arr[ind].add(user);
			 int p[] = entry.getValue();
			 int sum = 0;
			 for(int i=0;i<p.length;i++) if(p[i] == 1) sum++;
			 arr[ind].add(sum + "");
			 for(int i=0;i<p.length;i++) {
				 if(p[i] == 1) arr[ind].add("+");
				 if(p[i] == -1) arr[ind].add("-");
				 if(p[i] == 0) arr[ind].add(" ");
			 }
			 System.out.println(arr[ind]);
			 ind++;
		 }
		 model.addAttribute("arr", arr);
		 model.addAttribute("id", contestid);
		 model.addAttribute("name", contest.get().getTitle());
		 return "board.html";
	 }
}
