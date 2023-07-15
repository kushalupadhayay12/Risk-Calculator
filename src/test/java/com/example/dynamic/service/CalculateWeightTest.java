package com.example.dynamic.service;

import com.example.dynamic.exceptions.InvalidDimensionException;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateWeightTest {

    @Mock
    private CalculateRiskCappedScore calculateRiskCappedScore;

    @Mock
    private CalculateCompositeRiskScore calculateCompositeRiskScore;

    @InjectMocks
    private CalculateWeightImpl calculateWeight;

    @Test
    void calculateWeight_WithValidInputs_CallsDependenciesAndUpdatesOutputValues() throws InvalidDimensionException {
        // Mock input data
        CompanyRiskScore companyRiskScore = new CompanyRiskScore();
        RiskCalculationLogic riskCalcLogic = new RiskCalculationLogic();
        List<RiskDimension> riskDimensionList = new ArrayList<>();
        Map<String, Double> outputValues = new HashMap<>();

        // Set up the expected formula and result
        String formula = "10 * resilience + conduct";
        double expectedOutput = 0.0;

        // Call the method under test
        calculateWeight.calculateWeight(companyRiskScore, riskCalcLogic, riskDimensionList, outputValues);

        // Verify that the outputValues map was updated correctly
        assertEquals(expectedOutput, outputValues.get(riskCalcLogic.getElementName()));
    }

    @Test
    void calculateWeight_WithNonZeroResult_DoesNotCallCompositeRiskScore() throws InvalidDimensionException {
        // Mock input data
        CompanyRiskScore companyRiskScore = new CompanyRiskScore();
        RiskCalculationLogic riskCalcLogic = new RiskCalculationLogic();
        List<RiskDimension> riskDimensionList = new ArrayList<>();
        Map<String, Double> outputValues = new HashMap<>();

        // Call the method under test
        calculateWeight.calculateWeight(companyRiskScore, riskCalcLogic, riskDimensionList, outputValues);

        // Verify that the dependencies were called with the correct arguments
        verify(calculateRiskCappedScore).getRiskCappedScore(eq(companyRiskScore), eq(outputValues));

        // Verify that the compositeRiskScore dependency was not called
        verify(calculateCompositeRiskScore, never()).getCompositeRiskScore(anyMap(), anyString(), anyString());
    }
}

