package com.example.dynamic.repositoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.repositories.RiskCalculationLogicRepository;

@Service
public class RiskCalculationLogicRepoServiceImpl implements RiskCalculationLogicRepoService{
	
	@Autowired
	protected RiskCalculationLogicRepository riskCalculationLogicRepository;

	@Override
	public RiskCalculationLogic addRiskDimension(RiskCalculationLogic newFormula) throws DataAlreadyExistException {
		
		String name = newFormula.getElementName();
		
		if(riskCalculationLogicRepository.existsByName(name).isPresent()) {
			throw new DataAlreadyExistException("Formula with same element name already exist");
		}
		
		riskCalculationLogicRepository.save(newFormula);
		return newFormula;
	}

}
