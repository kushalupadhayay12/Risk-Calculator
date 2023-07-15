package com.example.dynamic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.dynamic.models.RiskCalculationLogic;

@Repository
public interface RiskCalculationLogicRepository extends JpaRepository <RiskCalculationLogic, String> {

	@Query(value = "SELECT r FROM RiskCalculationLogic r WHERE r.elementName =:name")
    Optional<RiskCalculationLogic> findByName(@Param("name")String elementName);
	
	@Query(value = "SELECT COUNT(f) > 0 FROM RiskCalculationLogic f WHERE f.elementName =:name")
    boolean existsByName(@Param("name")String elementName);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM RiskCalculationLogic f WHERE f.elementName = :name")
	void deleteByName(@Param("name")String elementName);
}
