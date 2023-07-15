package com.example.dynamic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskScoreLevel;

@Repository
public interface RiskScoreLevelRepository extends JpaRepository<RiskScoreLevel, String>{
}
