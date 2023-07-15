package com.example.dynamic.repositoryservice;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.exchanges.CompanyDimensionRequest;
import com.example.dynamic.exchanges.CompanyDto;

public interface CompanyRepositoryService {

	CompanyDto addNewCompany(CompanyDto newCompany) throws DataAlreadyExistException;

	CompanyDimensionRequest addNewCompanyDimension(String dimensionName) throws DataAlreadyExistException;
}
