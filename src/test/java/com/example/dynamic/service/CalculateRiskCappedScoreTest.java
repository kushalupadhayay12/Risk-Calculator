package com.example.dynamic.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.example.dynamic.exceptions.InvalidDimensionException;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.models.RiskScoreLevel;
import com.example.dynamic.repositories.RiskScoreCapRepository;
import com.example.dynamic.repositories.RiskScoreLevelRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CalculateRiskCappedScoreTest {

	@Mock
	private RiskScoreLevelRepository riskScoreLevelRepository;
	
	@Mock
	private RiskScoreCapRepository riskScoreCapRepository;
	
	@InjectMocks
	private CalculateRiskCappedScoreImpl calculateRiskCappedScore;
	
	 @BeforeEach
	    void setUp() {
	        MockitoAnnotations.openMocks(this);
	    }

	    @Test
	    void testGetRiskCappedScore() throws InvalidDimensionException {
	        // Mock data
	        CompanyRiskScore companyRiskScore = new CompanyRiskScore();
	        companyRiskScore.setProperties(new HashMap<>());
	        companyRiskScore.getProperties().put("dimension1", (int) 50.0);
	        companyRiskScore.getProperties().put("dimension2", (int) 75.0);

	        Map<String, Double> outputValues = new HashMap<>();

	        // Mock repository responses
	        List<RiskScoreLevel> riskScoreLevels = new ArrayList<>();
	        riskScoreLevels.add(new RiskScoreLevel("High risk", 41, 60));
	        riskScoreLevels.add(new RiskScoreLevel("Low risk", 61, 80));
	        Mockito.when(riskScoreLevelRepository.findAll()).thenReturn(riskScoreLevels);

	        List<RiskScoreCap> riskScoreCaps = new ArrayList<>();
	        riskScoreCaps.add(new RiskScoreCap("High risk", 1, 50));
	        riskScoreCaps.add(new RiskScoreCap("Low risk", 1, 80));
	        Mockito.when(riskScoreCapRepository.findAll()).thenReturn(riskScoreCaps);

	        // Call the method under test
	        calculateRiskCappedScore.getRiskCappedScore(companyRiskScore, outputValues);

	        // Verify the expected output
	        Assertions.assertEquals(50.0, outputValues.get("risk_capped_score"));
	        Mockito.verify(riskScoreLevelRepository).findAll();
	    }
	    
	    @Test
	    void testGetRiskCappedScore_NoMatchingLevels() throws InvalidDimensionException {
	        // Mock data
	        CompanyRiskScore companyRiskScore = new CompanyRiskScore();
	        companyRiskScore.setProperties(new HashMap<>());
	        companyRiskScore.getProperties().put("dimension1", (int) 90.0);
	        companyRiskScore.getProperties().put("dimension2", (int) 100.0);

	        Map<String, Double> outputValues = new HashMap<>();

	        // Mock repository responses
	        List<RiskScoreLevel> riskScoreLevels = new ArrayList<>();
	        riskScoreLevels.add(new RiskScoreLevel("High risk", 41, 60));
	        riskScoreLevels.add(new RiskScoreLevel("Low risk", 61, 80));
	        Mockito.when(riskScoreLevelRepository.findAll()).thenReturn(riskScoreLevels);

	        List<RiskScoreCap> riskScoreCaps = new ArrayList<>();
	        riskScoreCaps.add(new RiskScoreCap("High risk", 1, 50));
	        riskScoreCaps.add(new RiskScoreCap("Low risk", 1, 80));
	        Mockito.when(riskScoreCapRepository.findAll()).thenReturn(riskScoreCaps);

	        // Call the method under test
	        calculateRiskCappedScore.getRiskCappedScore(companyRiskScore, outputValues);

	        // Verify the expected output
	        Assertions.assertEquals(0.0, outputValues.get("risk_capped_score"));
	    }

}
