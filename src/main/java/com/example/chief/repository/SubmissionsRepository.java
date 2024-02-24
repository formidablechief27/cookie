package com.example.chief.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.chief.model.Submissions;


public interface SubmissionsRepository extends JpaRepository<Submissions, Integer> {
	List<Submissions> findByUserIdAndContestId(Integer userId, Integer contestId);
	List<Submissions> findByContestId(Integer contestId);
	List<Submissions> findByUserId(Integer userId);
	long count();
}
