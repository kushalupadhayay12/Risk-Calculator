package com.example.dynamic.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.dynamic.models.CompanyRiskScore;

@Repository
public interface CompanyRiskScoreRepository extends JpaRepository<CompanyRiskScore, Long> {

	@Query(value = "select c FROM CompanyRiskScore c WHERE c.companyName=:name")
	CompanyRiskScore findByName(@Param("name")String companyName);
	
	@Query("SELECT COUNT(c) > 0 FROM CompanyRiskScore c WHERE c.companyName =:name")
    boolean existsByName(@Param("name")String companyName);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM company_risk_score_properties WHERE company_risk_score_company_id IN (SELECT company_id FROM company_risk_score WHERE company_name = :name)", nativeQuery = true)
    void deletePropertiesByName(@Param("name") String companyName);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM CompanyRiskScore c WHERE c.companyName =:name")
	void deleteByName(@Param("name")String companyName);
}
