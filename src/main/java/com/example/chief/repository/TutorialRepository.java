package com.example.chief.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.chief.model.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, Integer> {

}
