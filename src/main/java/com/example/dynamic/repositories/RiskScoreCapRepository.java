package com.example.dynamic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.RiskScoreCap;

@Repository
public interface RiskScoreCapRepository extends JpaRepository<RiskScoreCap, Long>{

	@Query(value = "SELECT r FROM RiskScoreCap r WHERE r.risk=:risk AND r.counter=:counter")
	RiskScoreCap findByRiskScoreAndCounter(@Param("risk")String riskScore, @Param("counter")int counter);
}
