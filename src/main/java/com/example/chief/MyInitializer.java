package com.example.chief;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.chief.model.Questions;
import com.example.chief.model.Tests;
import com.example.chief.repository.DataCache;
import com.example.chief.repository.QuestionsRepository;
import com.example.chief.repository.TestsRepository;

import java.util.List;

import javax.annotation.PostConstruct;

@Component
public class MyInitializer {

    private QuestionsRepository questions_repository;
    private TestsRepository tests_repository;

    @Autowired
    public MyInitializer(QuestionsRepository questionsRepository, TestsRepository tests_Repository) {
        this.questions_repository = questionsRepository;
        this.tests_repository = tests_Repository;
    }

    public List<Questions> getAllQuestions() {
        return questions_repository.findAll();
    }

    public List<Tests> getAllTestCases() {
        return tests_repository.findAll();
    }

    @PostConstruct
    public void initialize() {
        Thread initializationThread = new Thread(() -> {
            List<Questions> list = getAllQuestions();
            for (Questions Q : list) {
                int id = Q.getId();
                DataCache.ques_map.put(id, Q);
            }
            List<Tests> list2 = getAllTestCases();
            for (Tests t : list2) {
                int id = t.getTestcaseNumber();
                DataCache.test_map.put(id, t);
            }
        });
        initializationThread.start();
    }
}
