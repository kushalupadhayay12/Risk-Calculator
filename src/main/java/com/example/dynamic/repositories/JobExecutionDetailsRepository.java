package com.example.dynamic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.ErrorResponse;

@Repository
public interface JobExecutionDetailsRepository extends JpaRepository<ErrorResponse, Long>{

}
