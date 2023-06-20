package com.example.dynamic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.dynamic.models.Output;

@Repository
public interface OutputRepository extends JpaRepository<Output, String>{
	
	@Query("SELECT COUNT(u) > 0 FROM Output o WHERE o.companyName =:name")
    boolean existsByName(@Param("name")String companyName);
	
	@Query("DELETE FROM Output o WHERE o.companyName = :name")
	void deleteByName(@Param("name")String companyName);
}
