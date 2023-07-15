package com.example.dynamic.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.example.dynamic.exceptions.InvalidDimensionException;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.models.RiskScoreLevel;
import com.example.dynamic.repositories.RiskScoreCapRepository;
import com.example.dynamic.repositories.RiskScoreLevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CalculateCompositeRiskScoreTest {

    @Mock
    private RiskScoreLevelRepository riskScoreLevelRepository;

    @Mock
    private RiskScoreCapRepository riskScoreCapRepository;

    @InjectMocks
    private CalculateRiskCappedScoreImpl calculateRiskCappedScore;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRiskCappedScore_MatchingLevels() throws InvalidDimensionException {
        // Mock data
        CompanyRiskScore companyRiskScore = new CompanyRiskScore();
        companyRiskScore.setProperties(new HashMap<>());
        companyRiskScore.getProperties().put("dimension1", (int) 50.0);
        companyRiskScore.getProperties().put("dimension2", (int) 70.0);

        Map<String, Double> outputValues = new HashMap<>();

        // Mock repository responses
        List<RiskScoreLevel> riskScoreLevels = new ArrayList<>();
        riskScoreLevels.add(new RiskScoreLevel("High risk", 41, 60));
        riskScoreLevels.add(new RiskScoreLevel("Low risk", 61, 80));
        when(riskScoreLevelRepository.findAll()).thenReturn(riskScoreLevels);

        List<RiskScoreCap> riskScoreCaps = new ArrayList<>();
        riskScoreCaps.add(new RiskScoreCap("High risk", 1, 50));
        riskScoreCaps.add(new RiskScoreCap("Low risk", 1, 80));
        when(riskScoreCapRepository.findAll()).thenReturn(riskScoreCaps);

        // Call the method under test
        calculateRiskCappedScore.getRiskCappedScore(companyRiskScore, outputValues);

        // Verify the expected output
        assertEquals(50.0, outputValues.get("risk_capped_score"));
        verify(riskScoreLevelRepository, times(1)).findAll();
        verify(riskScoreCapRepository, times(1)).findAll();
    }
}

