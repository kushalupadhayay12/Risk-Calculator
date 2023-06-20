package com.example.dynamic.service;

import java.util.List;
import java.util.Map;

import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;

public interface CalculateWeight {

	public void calculateWeight(CompanyRiskScore companyRiskScore, RiskCalculationLogic riskCalcLogic,
			List<RiskDimension> riskDimensionList, Map<String, Double> outputValues);
}
