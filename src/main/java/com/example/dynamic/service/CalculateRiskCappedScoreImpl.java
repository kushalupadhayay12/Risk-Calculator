package com.example.dynamic.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dynamic.exceptions.InvalidDimensionException;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.models.RiskScoreLevel;
import com.example.dynamic.repositories.RiskScoreCapRepository;
import com.example.dynamic.repositories.RiskScoreLevelRepository;

@Service
public class CalculateRiskCappedScoreImpl implements CalculateRiskCappedScore {

	@Autowired
	private RiskScoreLevelRepository riskScoreLevelRepository;
	
	@Autowired
	private RiskScoreCapRepository riskScoreCapRepository;
	
	private static final Logger logger = LogManager.getLogger(CalculateRiskCappedScoreImpl.class);

	@Override
	public void getRiskCappedScore(CompanyRiskScore companyRiskScore, Map<String, Double> outputValues) throws InvalidDimensionException{
		logger.debug("Calculating risk capped score");
		
//		To store the level and each level's count -> how many time each level is occurring
		HashMap<String, Integer> levelCounter = new HashMap<String, Integer>();
//		Get the list of riskScoreLevel from Database
		List<RiskScoreLevel> riskScoreList = riskScoreLevelRepository.findAll();

		for (String dimension : companyRiskScore.getProperties().keySet()) {
			double value = companyRiskScore.getProperties().get(dimension);

			for (RiskScoreLevel riskScoreLevel : riskScoreList) {
//				Example -> 41-60
				int lowerBoundScore = riskScoreLevel.getLowerBound();
				int upperBoundScore = riskScoreLevel.getUpperBound();

				if (value >= lowerBoundScore && value <= upperBoundScore) {
					levelCounter.put(riskScoreLevel.getLevel(),
							levelCounter.getOrDefault(riskScoreLevel.getLevel(), 0) + 1);
				}
			}
		}
		
//		If no conditions matched
		if(levelCounter.size() == 0) {
			outputValues.put("risk_capped_score", 0.0);
			return;
		}
		
		List<Double> riskValues = new ArrayList<>();
		List<RiskScoreCap> scoreCapList = riskScoreCapRepository.findAll();
		
		for(Map.Entry<String, Integer> e: levelCounter.entrySet()) {
// 			level = e.getKey(); // High risk, very high risk, low risk ......
			if(e.getValue() > 2) throw new InvalidDimensionException("INVALID_DIMENSIONS : SCORE CAP DOES NOT EXIST");
			for(RiskScoreCap scoreCapLevel : scoreCapList) {
				if(scoreCapLevel.getRisk().equals(e.getKey())) {
					if(scoreCapLevel.getCounter().equals(e.getValue())) {
						int levelScore = scoreCapLevel.getValue();
						riskValues.add((double)levelScore);
					}
				}
			}
		}
		
		double value = riskValues.stream().min(Double::compareTo).orElse(0.0);

		BigDecimal bd = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
	    double newValue = bd.doubleValue();
		outputValues.put("risk_capped_score", newValue);
		logger.debug("Risk capped score calculated");
	}
}
