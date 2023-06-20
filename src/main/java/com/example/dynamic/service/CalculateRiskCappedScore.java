package com.example.dynamic.service;

import java.util.Map;

import com.example.dynamic.exceptions.InvalidDimensionException;
import com.example.dynamic.models.CompanyRiskScore;

public interface CalculateRiskCappedScore {

	public void getRiskCappedScore(CompanyRiskScore companyRiskScore, Map<String, Double> outputValues) ;
}
