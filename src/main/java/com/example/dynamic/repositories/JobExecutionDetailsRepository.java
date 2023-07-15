package com.example.dynamic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.ErrorResponse;

@Repository
public interface JobExecutionDetailsRepository extends JpaRepository<ErrorResponse, Long>{

	@Query(value = "select status FROM ErrorResponse status WHERE status.companyId=:companyId")
	Optional<ErrorResponse> findByCompanyId(@Param("companyId")Long companyId);
	
}
