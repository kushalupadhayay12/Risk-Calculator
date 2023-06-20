package com.example.dynamic.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CalulcateCompositeRiskScoreImpl implements CalculateCompositeRiskScore{
	
	private static final Logger logger = LogManager.getLogger(CalulcateCompositeRiskScoreImpl.class);

	@Override
	public void getCompositeRiskScore(Map<String, Double> outputValues, String formula, String formulaName) {
		
//		formula.replace("min", "Math.min");
		formula = "Math.min(total_risk_score,risk_capped_score)";
		
		for (Map.Entry<String, Double> e : outputValues.entrySet()) {
			String regex = "(?<![a-zA-Z0-9_])" + Pattern.quote(e.getKey()) + "(?![a-zA-Z0-9_])";
			formula = formula.replaceAll(regex, e.getValue().toString());
		}
		
		double compositeRiskScore = 0.0;
		try {
			compositeRiskScore = evaluateFormula(formula);
		} catch (ScriptException e) {
			logger.error("Error occurred while evaluating formula for composite risk score: {}", formulaName);
            logger.error(e.getMessage());
		}
		
		BigDecimal bd = new BigDecimal(compositeRiskScore).setScale(2, RoundingMode.HALF_UP);
	    double result = bd.doubleValue();
		outputValues.put(formulaName, result);
		logger.debug("Composite risk score calculated for formula: {}", formulaName);
	}
	
	public double evaluateFormula(String formula) throws ScriptException {
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		Object result = engine.eval(formula);

		return Double.parseDouble(result.toString());
	}
}
