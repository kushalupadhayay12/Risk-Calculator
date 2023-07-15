package com.example.dynamic.repositoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.repositories.RiskDimensionRepository;

@Service
public class RiskDimensionRepoImpl implements RiskDimensionRepoService {
	
	private static final Logger logger = LoggerFactory.getLogger(RiskDimensionRepoImpl.class);

	@Autowired
	protected RiskDimensionRepository riskDimensionRepo;

	@Override
	public RiskDimension addRiskDimension(RiskDimension newDimension) throws DataAlreadyExistException {
		logger.info("Adding new risk dimension: {}", newDimension.getDimensionName());
		
		String name = newDimension.getDimensionName();
		
		if (riskDimensionRepo.findByName(name).isPresent()) {
			logger.warn("Same dimension already exists: {}", name);
			throw new DataAlreadyExistException("Same dimension already exists");
		}
		
		riskDimensionRepo.save(newDimension);
		logger.info("Added new risk dimension: {}", newDimension.getDimensionName());
		return newDimension;
	}
}
