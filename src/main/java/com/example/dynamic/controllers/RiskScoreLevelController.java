package com.example.dynamic.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.models.RiskScoreLevel;
import com.example.dynamic.service.CalculationService;

@RestController
@CrossOrigin("http://localhost:5173/")
public class RiskScoreLevelController {

	@Autowired
	protected CalculationService calculationService;
	
//	Get All the Risk Levels 
	@GetMapping("/risklevels")
	public ResponseEntity<List<RiskScoreLevel>> getRiskLevels() {
		
		List<RiskScoreLevel> riskLevelsList = calculationService.getRiskScoreLevels();
		
		if(riskLevelsList.size() == 0) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok().body(riskLevelsList);
	}
	
	@GetMapping("/risklevel/{level}")
	public ResponseEntity<RiskScoreLevel> getRiskScoreWithId(@PathVariable String level) {
		
		RiskScoreLevel riskScore = calculationService.getRiskLevelById(level);
		return ResponseEntity.ok().body(riskScore);
	}
	
//	Edit the Risk Level
	@PutMapping("/risklevel/edit/{level}")
	public ResponseEntity<Void> EditRiskLevel(@RequestBody RiskScoreLevel newRiskScoreLevel, @PathVariable String level) {
		
		calculationService.editRiskLevel(newRiskScoreLevel, level);
		return ResponseEntity.ok().body(null);
	}
	
	@DeleteMapping("/risklevel/delete/{level}")
	public ResponseEntity<String> deleteRiskLevel(@PathVariable String level) {
		
		try {
			calculationService.deleteRiskLevelById(level);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().body(level + " has been deleted");
	}
}
