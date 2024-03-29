package com.example.chief.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.chief.model.*;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
	Optional<Users> findByUsername(String username);
	long count();
	List<Users> findAllByOrderByRatingDesc();
}
