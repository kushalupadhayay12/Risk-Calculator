package com.example.dynamic.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.exceptions.CompanyNotFoundException;
import com.example.dynamic.exchanges.CompanyDimensionRequest;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.Output;
import com.example.dynamic.service.CalculationService;

@RestController
@CrossOrigin("http://localhost:5173/")
public class CompanyController {

	@Autowired
	protected CalculationService calculationService;
	
	@GetMapping("/companies")
	public ResponseEntity<List<CompanyDto>> ListOfAllCompanies() {
		
		List<CompanyDto> companyList = calculationService.getAllCompaniesData();
		if(companyList.size() == 0) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(companyList);
	}
	
//	Get Output details of particular company
	@GetMapping("/company/outputs/{companyName}")
	public ResponseEntity<Output> getCompanyByName(@PathVariable String companyName) {
		Output result = calculationService.getByOutputByName(companyName);
		return ResponseEntity.ok(result);
	}
	
//	Get details of a company from CompanyRiskScore table
	@GetMapping("/company/companydata/{companyName}")
	public ResponseEntity<CompanyDto> getDataOfCompanyByName(@PathVariable String companyName) {
		CompanyDto companyData = calculationService.getDataByCompanyName(companyName);
		return ResponseEntity.ok(companyData);
	}

//	Edit the values of fields company
	@PutMapping("/company/companydata/{companyName}")
	public ResponseEntity<Void> updateCompanyFieldData(@RequestBody CompanyDto newCompany,
			@PathVariable String companyName) {
		calculationService.updateCompanyData(companyName, newCompany);
		return ResponseEntity.ok().build();
	}
	
//	Add new Dimension to company Risk Score table
	@PostMapping("/companies/dimensions/add")
	public ResponseEntity<CompanyDimensionRequest> addNewDimensionToCompanyRiskScore(@RequestBody CompanyDimensionRequest newDimension) {
		String dimensionName = newDimension.getDimensionName();
		Optional<CompanyDimensionRequest> newlyAddedDimension = calculationService.addNewCompanyDimension(dimensionName);
		
		if(newlyAddedDimension.isPresent()) {
			return ResponseEntity.ok().body(newlyAddedDimension.get());
		}
		
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	
//	Add New Company
	@PostMapping("/companies/add")
	public ResponseEntity<CompanyDto> addNewCompany(@RequestBody CompanyDto newCompany) {
		Optional<CompanyDto> newlyAddedCompany = calculationService.addNewCompany(newCompany);
		
		if(newlyAddedCompany.isPresent()) {
			return ResponseEntity.ok().body(newlyAddedCompany.get());
		}
		
		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}

//	Delete the Company with given id
	@DeleteMapping("/companies/{companyName}")
	public ResponseEntity<String> deleteCompanyByName(@PathVariable String companyName) {

		try {
			calculationService.deleteByCompanyName(companyName);
		} catch (CompanyNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().body(companyName + " has been deleted from the Database");
	}
}
