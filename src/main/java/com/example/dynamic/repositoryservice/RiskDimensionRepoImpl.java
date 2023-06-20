package com.example.dynamic.repositoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.repositories.RiskDimensionRepository;

@Service
public class RiskDimensionRepoImpl implements RiskDimensionRepoService {
	
	@Autowired
	protected RiskDimensionRepository riskDimensionRepo;

	@Override
	public RiskDimension addRiskDimension(RiskDimension newDimension) throws DataAlreadyExistException {
		
		String name = newDimension.getDimensionName();
		
		if(riskDimensionRepo.existsByName(name).isPresent()) {
			throw new DataAlreadyExistException("Same Dimension already exist");
		}
		
		riskDimensionRepo.save(newDimension);
		return newDimension;
	}
}
