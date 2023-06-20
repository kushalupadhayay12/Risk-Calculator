package com.example.dynamic.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.exceptions.CompanyNotFoundException;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.Output;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.service.CalculationService;
import com.example.dynamic.service.exceptions.DimensionNotFoundException;

@RestController
@CrossOrigin("http://localhost:5173/")
public class RiskController {

	@Autowired
	protected CalculationService calculationService;

//	Get all the Companies outputs
	@GetMapping("/")
	public ResponseEntity<Output[]> ListOfAllCompaniesOutput() {

		Output[] outputList = calculationService.getAllCompanies();
		if (outputList.length == 0)
			return ResponseEntity.noContent().build();

		return ResponseEntity.ok(outputList);
	}
	
	@GetMapping("/companies")
	public ResponseEntity<CompanyRiskScore[]> ListOfAllCompanies() {
		
		CompanyRiskScore [] companyList = calculationService.getAllCompaniesData();
		if(companyList.length == 0) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(companyList);
	}

//	Calculating Formulas for all companies that are already in the database
	@GetMapping("/calculate")
	public List<Output> calculate() {

		List<Output> outputList = calculationService.calculateForAllCompanies();
		return outputList;
	}

//	Calculating for only one company which has been given as input
	@PostMapping("/calculate/company")
	public Output calculateForOneCompany(@Validated @RequestBody CompanyRiskScore company) {

		Output output = calculationService.calculateForNewOne(company);
		return output;
	}

//	Get details of particular company
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

//	Edit the values of fields o
	@PutMapping("/company/companydata/{companyName}")
	public ResponseEntity<Void> updateCompanyFieldData(@RequestBody CompanyDto newCompany,
			@PathVariable String companyName) {
		calculationService.updateCompanyData(companyName, newCompany);
		return ResponseEntity.ok().build();
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
	
//	Get all the dimensions from the risk dimension table
	@GetMapping("/dimensions")
	public ResponseEntity<RiskDimension[]> getAllDimensions() {
		RiskDimension [] dimensionList = calculationService.getAllDimensions();
		return ResponseEntity.ok(dimensionList);
	}
	
//	GET dimension with given name
	@GetMapping("/dimensions/dimensiondata/{dimensionName}")
	public ResponseEntity<RiskDimension> getDataOfDimensionByName(@PathVariable String dimensionName) {
		RiskDimension dimensionData = calculationService.getDataByDimensionName(dimensionName);
		return ResponseEntity.ok(dimensionData);
	}
	

//	Add new dimension in risk dimension table
	@PostMapping("/dimensions/add")
	public ResponseEntity<RiskDimension> addDimension(@RequestBody RiskDimension newDimension) {

		Optional<RiskDimension> returnedDimension = calculationService.addDimension(newDimension);

		if (returnedDimension.isPresent()) {
			return ResponseEntity.ok().body(returnedDimension.get());
		}

		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	
//	Edit data of dimension
	@PutMapping("/dimensions/dimensiondata/{dimensionName}")
	public ResponseEntity<Void> updateDimensionData(@RequestBody RiskDimension newDimension,
			@PathVariable String dimensionName) {
		calculationService.updateDimensionData(dimensionName, newDimension);
		return ResponseEntity.ok().build();
	}
	

//	Delete given dimension
	@DeleteMapping("/dimensions/delete/{dimensionName}")
	public ResponseEntity<String> deleteDimensionByName(@PathVariable String dimensionName) {

		try {
			calculationService.deleteByDimensionName(dimensionName);
		} catch (DimensionNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().body(dimensionName + " has been deleted from the Database");
	}
	
// 	Get all the formulas from the table
	@GetMapping("/formulas")
	public ResponseEntity<RiskCalculationLogic[]> getAllFormulas() {
		RiskCalculationLogic [] formulaList = calculationService.getAllFormulas();
		return ResponseEntity.ok(formulaList);
	}
	
//	Add new Formula in risk dimension table
	@PostMapping("/formulas/add")
	public ResponseEntity<RiskCalculationLogic> addFormula(@RequestBody RiskCalculationLogic newFormula) {

		Optional<RiskCalculationLogic> returnedFormula = calculationService.addFormula(newFormula);

		if (returnedFormula.isPresent()) {
			return ResponseEntity.ok().body(returnedFormula.get());
		}

		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	
//	Get All the risk cap scores from the riskScoreLevel table
	@GetMapping("/riskscores")
	public ResponseEntity<RiskScoreCap[]> getAllRiskScores() {
		RiskScoreCap [] scoreCapList = calculationService.getAllScores();
		return ResponseEntity.ok(scoreCapList);
	}
}
