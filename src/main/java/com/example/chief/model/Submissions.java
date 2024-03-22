package com.example.chief.model;
import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "submissions")
public class Submissions {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "question_id")
    private int questionId;

    @Column(name = "code")
    private String code;

    @Column(name = "verdict")
    private String verdict;

    @Column(name = "contest_id")
    private int contestId;

    @Column(name = "time_exe")
    private int timeExecution;

    @Column(name = "time_submitted")
    private LocalDateTime timeSubmitted;
    
    @Column(name = "pts")
    private int pts;
    
    public Submissions(int sub_id, int userId, int questionId, String code, String verdict, int contestId, int timeExecution, LocalDateTime timeSubmitted, int pts) {
    	this.id = sub_id;
		this.userId = userId;
		this.questionId = questionId;
		this.code = code;
		this.verdict = verdict;
		this.contestId = contestId;
		this.timeExecution = timeExecution;
		this.timeSubmitted = timeSubmitted;
		this.pts = pts;
    }

    // Constructors

    public Submissions() {
        // Default constructor
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public int getContestId() {
        return contestId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public int getTimeExecution() {
        return timeExecution;
    }

    public void setTimeExecution(int timeExecution) {
        this.timeExecution = timeExecution;
    }

    public LocalDateTime getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(LocalDateTime timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }
    
    public int getPts() {
    	return pts;
    }
    
    public void setPts(int pts) {
    	this.pts = pts;
    }

    // Other methods if needed
}

