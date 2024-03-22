package com.example.chief;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.chief.model.Contests;
import com.example.chief.model.Questions;
import com.example.chief.model.Submissions;
import com.example.chief.model.Tests;
import com.example.chief.model.Users;
import com.example.chief.repository.ContestsRepository;
import com.example.chief.repository.DataCache;
import com.example.chief.repository.QuestionsRepository;
import com.example.chief.repository.SubmissionsRepository;
import com.example.chief.repository.TestsRepository;
import com.example.chief.repository.UserRepository;

import java.util.List;

import javax.annotation.PostConstruct;

@Component
public class MyInitializer {

    private QuestionsRepository questions_repository;
    private TestsRepository tests_repository;
    private SubmissionsRepository subs_repo;
    private ContestsRepository contest_repo;
    private UserRepository user_repo;

    @Autowired
    public MyInitializer(QuestionsRepository questionsRepository, TestsRepository tests_Repository, SubmissionsRepository subs_repo, ContestsRepository c, UserRepository user) {
        this.questions_repository = questionsRepository;
        this.tests_repository = tests_Repository;
        this.subs_repo = subs_repo;
        this.contest_repo = c;
        this.user_repo = user;
    }

    public List<Questions> getAllQuestions() {
        return questions_repository.findAll();
    }

    public List<Tests> getAllTestCases() {
        return tests_repository.findAll();
    }
    
    public List<Submissions> get() {
    	return subs_repo.findAll();
    }
    
    public List<Contests> getAll() {
    	return contest_repo.findAll();
    }
    
    public List<Users> getAgain() {
    	return user_repo.findAll();
    }

    @PostConstruct
    public void initialize() {
        Thread initializationThread = new Thread(() -> {
            List<Questions> list = getAllQuestions();
            for (Questions Q : list) {
                int id = Q.getId();
                DataCache.ques_map.put(id, Q);
            }
            List<Users> list5 = getAgain();
            for(Users u : list5) {
            	int id = u.getId();
            	DataCache.user_map.put(id, u);
            }
            List<Contests> list4 = getAll();
            for(Contests c : list4) {
            	long id = c.getId();
            	DataCache.contest_map.put((int)id, c);
            }
            List<Tests> list2 = getAllTestCases();
            for (Tests t : list2) {
                int id = t.getTestcaseNumber();
                DataCache.test_map.put(id, t);
            }
            List<Submissions> list3 = get();
            for(Submissions s : list3) {
            	int id = s.getId();
            	DataCache.sub_map.put(id, s);
            }
        });
        initializationThread.start();
    }
}
