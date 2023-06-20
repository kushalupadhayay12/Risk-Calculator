package com.example.dynamic.repositoryservice;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskDimension;

public interface RiskDimensionRepoService {

	RiskDimension addRiskDimension(RiskDimension newDimension) throws DataAlreadyExistException;
}
