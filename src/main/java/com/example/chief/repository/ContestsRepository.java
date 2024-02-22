package com.example.chief.repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.chief.model.Contests;

@Repository
public interface ContestsRepository extends JpaRepository<Contests, Long> {
	List<Contests> findAllByOrderByStartDesc();
}
