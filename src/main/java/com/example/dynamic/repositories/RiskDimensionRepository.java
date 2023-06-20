package com.example.dynamic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.RiskDimension;

@Repository
public interface RiskDimensionRepository extends JpaRepository<RiskDimension, String> {

	@Query(value = "SELECT r FROM RiskDimension r WHERE r.name =:name")
    Optional<RiskDimension> existsByName(@Param("name")String name);
	
	@Query(value = "DELETE FROM RiskDimension r WHERE r.name = :name")
	void deleteByName(@Param("name")String dimensionName);
	
}
