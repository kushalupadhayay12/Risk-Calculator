package com.example.dynamic.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.dynamic.models.RiskDimension;

@Repository
public interface RiskDimensionRepository extends JpaRepository<RiskDimension, String> {
	
	@Query(value = "select d FROM RiskDimension d WHERE d.name=:name")
	Optional<RiskDimension> findByName(@Param("name")String dimensionName);

	@Query(value = "SELECT COUNT(d) > 0 FROM RiskDimension d WHERE d.name =:name")
    boolean existsByName(@Param("name")String dimensionName);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM RiskDimension r WHERE r.name = :name")
	void deleteByName(@Param("name")String dimensionName);
	
}
