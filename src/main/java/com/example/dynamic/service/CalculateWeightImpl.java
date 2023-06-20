package com.example.dynamic.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dynamic.exceptions.InvalidDimensionException;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;

@Service
public class CalculateWeightImpl implements CalculateWeight {

	@Autowired
	private CalculateRiskCappedScore calculateRiskCappedScore;

	@Autowired
	private CalculateCompositeRiskScore calculateCompositeRiskScore;
	
	private static final Logger logger = LogManager.getLogger(CalculateWeightImpl.class);

	@Override
	public void calculateWeight(CompanyRiskScore companyRiskScore, RiskCalculationLogic riskCalcLogic,
			List<RiskDimension> riskDimensionList, Map<String, Double> outputValues) throws InvalidDimensionException{

//		Current formula
		String formula = riskCalcLogic.getFormula();

//		Iterating in a perticular's company Map
//		Example: companyRiskScore = TCS so this will be TCS.keySet()
		for (String dimension : companyRiskScore.getProperties().keySet()) {
			String regex = "(?<![a-zA-Z0-9_])" + Pattern.quote(dimension) + "(?![a-zA-Z0-9_])";
			formula = formula.replaceAll(regex, companyRiskScore.getProperties().get(dimension).toString());
		}

//		Getting the weight
		for (RiskDimension riskDimension : riskDimensionList) {
				String regex = "(?<![a-zA-Z0-9_])" + riskDimension.getDimensionName() + "(?![a-zA-Z0-9_])";
				formula = formula.replaceAll(regex, String.valueOf(riskDimension.getValue()));
		}

//		Get the total risk score
		for (Map.Entry<String, Double> e : outputValues.entrySet()) {
			String regex = "(?<![a-zA-Z0-9_])" + Pattern.quote(e.getKey()) + "(?![a-zA-Z0-9_])";
			formula = formula.replaceAll(regex, e.getValue().toString());
		}
		
		Double result = 0.0;
		try {
			result = evaluateFormula(formula);
		} catch (Exception e) {
			logger.error("Error occurred while evaluating formula for risk calculation logic: {}", riskCalcLogic.getElementName());
		}
		
//		Update the values in map
		BigDecimal bd = new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
	    double newResult = bd.doubleValue();
		outputValues.put(riskCalcLogic.getElementName(), newResult);

		calculateRiskCappedScore.getRiskCappedScore(companyRiskScore, outputValues);
		logger.debug("Risk capped score calculated for risk calculation logic: {}", riskCalcLogic.getElementName());
		
//		If the formula has been computed no need to call composite risk score
		if(result != 0.0) return;
		
		calculateCompositeRiskScore.getCompositeRiskScore(outputValues, formula, riskCalcLogic.getElementName());
		logger.debug("Composite risk score calculated for risk calculation logic: {}", riskCalcLogic.getElementName());
		
	}

	public double evaluateFormula(String formula) throws ScriptException {

		// Use the ScriptEngine API to evaluate the formula string as a mathematical
		// expression
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		Object result = engine.eval(formula);

		// Convert the result object to an integer and return it
		return Double.parseDouble(result.toString());
	}
}
