package com.example.dynamic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dynamic.models.RiskScoreCap;

public interface RiskScoreCapRepository extends JpaRepository<RiskScoreCap, Long>{

}
