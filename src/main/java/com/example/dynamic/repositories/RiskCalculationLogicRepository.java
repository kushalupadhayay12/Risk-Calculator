package com.example.dynamic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.RiskCalculationLogic;

@Repository
public interface RiskCalculationLogicRepository extends JpaRepository <RiskCalculationLogic, String> {

	@Query(value = "SELECT r FROM RiskCalculationLogic r WHERE r.elementName =:name")
    Optional<RiskCalculationLogic> existsByName(@Param("name")String elementName);
}
