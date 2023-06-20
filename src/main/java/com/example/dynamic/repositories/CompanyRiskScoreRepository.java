package com.example.dynamic.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.CompanyRiskScore;

@Repository
public interface CompanyRiskScoreRepository extends JpaRepository<CompanyRiskScore, Long> {

	@Query(value = "select c FROM CompanyRiskScore c WHERE c.companyName=:name")
	CompanyRiskScore findByName(@Param("name")String companyName);
	
	@Query("SELECT COUNT(u) > 0 FROM CompanyRiskScore c WHERE c.companyName =:name")
    boolean existsByName(@Param("name")String companyName);
	
	@Query("DELETE FROM CompanyRiskScore c WHERE c.companyName = :name")
	void deleteByName(@Param("name")String companyName);
}
