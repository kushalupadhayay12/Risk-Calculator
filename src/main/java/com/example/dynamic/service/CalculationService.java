package com.example.dynamic.service;


import java.util.List;
import java.util.Optional;

import com.example.dynamic.exceptions.DimensionNotFoundException;
import com.example.dynamic.exceptions.RiskLevelNotFoundException;
import com.example.dynamic.exchanges.CompanyDimensionRequest;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.ErrorResponse;
import com.example.dynamic.models.Output;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.models.RiskScoreLevel;

public interface CalculationService {

	List<Output> getAllCompanies();
	List<Output> calculateForAllCompanies();
	Output calculateForNewOne(CompanyRiskScore company);
	Output getByOutputByName(String companyName);
	CompanyDto getDataByCompanyName(String companyName);
	void updateCompanyData(String companyName, CompanyDto newCompany);
	void deleteByCompanyName(String companyName);
	Optional<RiskDimension> addDimension(RiskDimension newDimension);
	void deleteByDimensionName(String dimensionName) throws DimensionNotFoundException;
	List<RiskDimension> getAllDimensions();
	RiskDimension getDataByDimensionName(String dimensionName);
	void updateDimensionData(String dimensionName, RiskDimension newDimension);
	List<CompanyDto> getAllCompaniesData();
	List<RiskCalculationLogic> getAllFormulas();
	List<RiskScoreCap> getAllScores();
	Optional<RiskCalculationLogic> addFormula(RiskCalculationLogic newFormula);
	Optional<CompanyDto> addNewCompany(CompanyDto newCompany);
	Optional<CompanyDimensionRequest> addNewCompanyDimension(String dimensionName);
	List<ErrorResponse> getAllJobStatus();
	void deleteByFormulaByName(String elementName);
	RiskCalculationLogic getDataByFormulaName(String elementName);
	RiskScoreCap getDataByRiskScoreCapName(String riskScore, int counter);
	void updateScoreCap(RiskScoreCap riskCapScore, String riskScore, int counter);
	void deleteScoreCap(String riskScore, int counter);
	List<RiskScoreLevel> getRiskScoreLevels();
	void editRiskLevel(RiskScoreLevel newRiskScoreLevel, String level);
	RiskScoreLevel getRiskLevelById(String level);
	void deleteRiskLevelById(String level) throws RiskLevelNotFoundException;
}
