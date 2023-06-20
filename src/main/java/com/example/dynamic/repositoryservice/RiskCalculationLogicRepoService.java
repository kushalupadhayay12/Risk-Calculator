package com.example.dynamic.repositoryservice;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskCalculationLogic;

public interface RiskCalculationLogicRepoService {

	RiskCalculationLogic addRiskDimension(RiskCalculationLogic newFormula) throws DataAlreadyExistException;

	
}
