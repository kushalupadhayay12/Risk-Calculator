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

import com.example.dynamic.exceptions.DimensionNotFoundException;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.service.CalculationService;

@RestController
@CrossOrigin("http://localhost:5173/")
public class RiskDimensionController {
	
	@Autowired
	protected CalculationService calculationService;

//	Get all the dimensions from the risk dimension table
	@GetMapping("/dimensions")
	public ResponseEntity<List<RiskDimension>> getAllDimensions() {
		List<RiskDimension>  dimensionList = calculationService.getAllDimensions();
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
	@DeleteMapping("/dimensions/{dimensionName}")
	public ResponseEntity<String> deleteDimensionByName(@PathVariable String dimensionName) {

		try {
			calculationService.deleteByDimensionName(dimensionName);
		} catch (DimensionNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().body(dimensionName + " has been deleted from the Database");
	}
}

