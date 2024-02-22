package com.example.chief.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.chief.model.*;

@Repository
public interface TestsRepository extends JpaRepository<Tests, Integer> {
	List<Tests> findAllByTestcaseNumberBetweenOrderByTestcaseNumberAsc(int min, int max);
	Optional<Tests> findByTestcaseNumber(int testcaseNumber);
}
