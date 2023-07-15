package com.example.dynamic.repositoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.repositories.RiskCalculationLogicRepository;

@Service
public class RiskCalculationLogicRepoServiceImpl implements RiskCalculationLogicRepoService {

	private static final Logger logger = LoggerFactory.getLogger(RiskCalculationLogicRepoServiceImpl.class);

	@Autowired
	protected RiskCalculationLogicRepository riskCalculationLogicRepository;

	@Override
	public RiskCalculationLogic addRiskDimension(RiskCalculationLogic newFormula) throws DataAlreadyExistException {
		logger.info("Adding new risk calculation formula: {}", newFormula.getElementName());

		String name = newFormula.getElementName();

		if (riskCalculationLogicRepository.findByName(name).isPresent()) {
			logger.warn("Formula with the same element name already exists: {}", name);
			throw new DataAlreadyExistException("Formula with the same element name already exists");
		}

		riskCalculationLogicRepository.save(newFormula);
		logger.info("Added new risk calculation formula: {}", newFormula.getElementName());
		return newFormula;
	}
}
