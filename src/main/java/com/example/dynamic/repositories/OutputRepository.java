package com.example.dynamic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.dynamic.models.Output;

@Repository
public interface OutputRepository extends JpaRepository<Output, String>{
	
	@Query("SELECT COUNT(o) > 0 FROM Output o WHERE o.companyName =:name")
    boolean existsByName(@Param("name")String companyName);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM output_table_properties WHERE output_table_company_name IN (SELECT company_name FROM output_table WHERE company_name = :name)", nativeQuery = true)
    void deletePropertiesByName(@Param("name") String companyName);
	
	@Transactional
    @Modifying
	@Query("DELETE FROM Output o WHERE o.companyName = :name")
	void deleteByName(@Param("name")String companyName);
}
