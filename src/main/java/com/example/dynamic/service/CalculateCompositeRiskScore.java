package com.example.dynamic.service;

import java.util.Map;

public interface CalculateCompositeRiskScore {

	public void getCompositeRiskScore(Map<String, Double> outputValues, String formula, String formulaName);
}
