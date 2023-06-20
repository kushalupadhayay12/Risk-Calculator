package com.example.dynamic.service;


import java.util.List;
import java.util.Optional;

import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.Output;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.service.exceptions.DimensionNotFoundException;

public interface CalculationService {

	Output[] getAllCompanies();
	List<Output> calculateForAllCompanies();
	Output calculateForNewOne(CompanyRiskScore company);
	Output getByOutputByName(String companyName);
	CompanyDto getDataByCompanyName(String companyName);
	void updateCompanyData(String companyName, CompanyDto newCompany);
	void deleteByCompanyName(String companyName);
	Optional<RiskDimension> addDimension(RiskDimension newDimension);
	void deleteByDimensionName(String dimensionName) throws DimensionNotFoundException;
	RiskDimension[] getAllDimensions();
	RiskDimension getDataByDimensionName(String dimensionName);
	void updateDimensionData(String dimensionName, RiskDimension newDimension);
	CompanyRiskScore[] getAllCompaniesData();
	RiskCalculationLogic[] getAllFormulas();
	RiskScoreCap[] getAllScores();
	Optional<RiskCalculationLogic> addFormula(RiskCalculationLogic newFormula);
}
