package com.example.chief.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chief.model.Questions;

@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Integer> {
	List<Questions> findByContestId(Integer contestId);
}
