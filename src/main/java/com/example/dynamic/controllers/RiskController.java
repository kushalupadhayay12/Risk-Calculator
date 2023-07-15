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

import com.example.dynamic.exceptions.FormulaNotFoundException;
import com.example.dynamic.exceptions.ScoreCapNotFoundException;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.ErrorResponse;
import com.example.dynamic.models.Output;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.service.CalculationService;

@RestController
@CrossOrigin("http://localhost:5173/")
public class RiskController {

	@Autowired
	protected CalculationService calculationService;

//	Get all the Companies outputs
	@GetMapping("/")
	public ResponseEntity<List<Output>> ListOfAllCompaniesOutput() {

		List<Output> outputList = calculationService.getAllCompanies();
		if (outputList.size() == 0)
			return ResponseEntity.noContent().build();

		return ResponseEntity.ok(outputList);
	}

//	Get the Job status 
	@GetMapping("/jobstatus")
	public ResponseEntity<List<ErrorResponse>> getJobStatus() {

		List<ErrorResponse> listOfJobStatus = calculationService.getAllJobStatus();

		if (listOfJobStatus.size() == 0) {
			return ResponseEntity.ok().body(null);
		}

		return ResponseEntity.ok().body(listOfJobStatus);
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

// 	Get all the formulas from the table
	@GetMapping("/formulas")
	public ResponseEntity<List<RiskCalculationLogic>> getAllFormulas() {
		List<RiskCalculationLogic> formulaList = calculationService.getAllFormulas();
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

//	Get formula with given name
	@GetMapping("/formulas/formuladata/{elementName}")
	public ResponseEntity<RiskCalculationLogic> getDataOfFormulaByName(@PathVariable String elementName) {
		RiskCalculationLogic formulaData = calculationService.getDataByFormulaName(elementName);
		return ResponseEntity.ok(formulaData);
	}

//	Delete formula with given name
	@DeleteMapping("/formulas/{elementName}")
	public ResponseEntity<String> deleteFormulaByName(@PathVariable String elementName) {

		try {
			calculationService.deleteByFormulaByName(elementName);
		} catch (FormulaNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().body(elementName + " has been deleted from the Database");
	}

//	Get All the risk cap scores from the riskScoreLevel table
	@GetMapping("/riskscores")
	public ResponseEntity<List<RiskScoreCap>> getAllRiskScores() {
		List<RiskScoreCap> scoreCapList = calculationService.getAllScores();
		return ResponseEntity.ok(scoreCapList);
	}

//	Get Risk cap score with given level
	@GetMapping("/riskscorecaps/riskscoredata/{riskScore}/{counter}")
	public ResponseEntity<RiskScoreCap> getDataOfRiskScoreLevelByName(@PathVariable String riskScore,
			@PathVariable int counter) {
		RiskScoreCap riskScoreCap = calculationService.getDataByRiskScoreCapName(riskScore, counter);
		return ResponseEntity.ok(riskScoreCap);
	}

//	Edit Score Cap
	@PutMapping("/scorecaps/score/{riskScore}/{counter}")
	public ResponseEntity<Void> updateScoreCap(@RequestBody RiskScoreCap riskScoreCap, @PathVariable String riskScore,
			@PathVariable int counter) {
		calculationService.updateScoreCap(riskScoreCap, riskScore, counter);
		return ResponseEntity.ok().build();
	}
	
//	Delete Score Cap 
	@DeleteMapping("/scorecaps/score/{riskScore}/{counter}")
	public ResponseEntity<String> deleteScoreCap(@PathVariable String riskScore, @PathVariable int counter) {
		
		try {
			calculationService.deleteScoreCap(riskScore, counter);
		} catch (ScoreCapNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().body(riskScore + " Score Cap with counter " + counter + " has been deleted");
	}
}
