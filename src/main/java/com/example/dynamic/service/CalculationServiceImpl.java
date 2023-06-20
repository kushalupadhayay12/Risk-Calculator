package com.example.dynamic.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.dynamic.exceptions.CompanyNotFoundException;
import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.ErrorResponse;
import com.example.dynamic.models.Output;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.repositories.CompanyRiskScoreRepository;
import com.example.dynamic.repositories.JobExecutionDetailsRepository;
import com.example.dynamic.repositories.OutputRepository;
import com.example.dynamic.repositories.RiskCalculationLogicRepository;
import com.example.dynamic.repositories.RiskDimensionRepository;
import com.example.dynamic.repositories.RiskScoreCapRepository;
import com.example.dynamic.repositoryservice.RiskCalculationLogicRepoService;
import com.example.dynamic.repositoryservice.RiskDimensionRepoService;
import com.example.dynamic.service.exceptions.DimensionNotFoundException;

@Service
public class CalculationServiceImpl implements CalculationService {

	@Autowired
	protected CompanyRiskScoreRepository companyRiskScoreRepository;

	@Autowired
	protected RiskDimensionRepository riskDimensionRepository;

	@Autowired
	protected RiskDimensionRepoService riskDimensionRepoService;
	
	@Autowired
	protected RiskScoreCapRepository riskScoreCapRepository;

	@Autowired
	protected RiskCalculationLogicRepository riskCalculationLogicRepository;
	
	@Autowired
	protected RiskCalculationLogicRepoService riskCalculationLogicRepoService;

	@Autowired
	private OutputRepository outputRepository;

	@Autowired
	private JobExecutionDetailsRepository statusRepo;

	@Autowired
	private CalculateWeight calculateWeight;

	@Autowired
	private ModelMapper modelMapper;

	private static final Logger logger = LoggerFactory.getLogger(CalculationServiceImpl.class);

	@Override
	public Output[] getAllCompanies() {

		List<Output> companyList = outputRepository.findAll();
		Output[] outputArray = new Output[companyList.size()];
		int i = 0;
		for (Output output : companyList) {
			outputArray[i++] = output;
		}
		return outputArray;
	}
	
	@Override
	public CompanyRiskScore[] getAllCompaniesData() {
		List<CompanyRiskScore> List = companyRiskScoreRepository.findAll();
		CompanyRiskScore [] companyArray = new CompanyRiskScore[List.size()];
		int i = 0;
		for(CompanyRiskScore company : List) {
			companyArray[i++] = company;
		}
		return companyArray;
	}

	@Override
	public Output getByOutputByName(String companyName) {
		Optional<Output> outputEntity = outputRepository.findById(companyName);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + outputEntity.get() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		return outputEntity.get();
	}

	@Override
	public List<Output> calculateForAllCompanies() {
		logger.info("Calculating risk scores for all companies...");

		List<Output> outputList = new ArrayList<>();

		List<Map<String, Double>> ans = new ArrayList<>();

//		It will give all the company data present in company risk score table
		List<CompanyRiskScore> companyList = companyRiskScoreRepository.findAll();

		for (CompanyRiskScore companyRiskScore : companyList) {
			Output output = null;
			Map<String, Double> outputValues = new HashMap<>();
			try {

				outputValues = calculate(companyRiskScore, outputValues);
				ans.add(outputValues);

				output = new Output();
				output.setCompanyName(companyRiskScore.getCompanyName());
				output.setProperties(outputValues);
				outputRepository.save(output);

			} catch (Exception ex) {

//				Make Custom error response to save job execution details in DB
				ErrorResponse errorResponse = new ErrorResponse(companyRiskScore.getId(),
						companyRiskScore.getCompanyName(), ex.toString(), LocalDateTime.now());
				statusRepo.save(errorResponse);
				logger.error(companyRiskScore.getCompanyName() + " " + ex.toString());

			}
			logger.info("Risk score calculated and saved for company: {}", companyRiskScore.getCompanyName());
		}

		logger.info("Risk scores calculated for all companies.");
		outputList = outputRepository.findAll();
		return outputList;
	}

//	Calculate for only one company 
	@Override
	public Output calculateForNewOne(CompanyRiskScore company) {

		Map<String, Double> outputValues = new HashMap<String, Double>();
		Output output = null;
		try {
			companyRiskScoreRepository.save(company);
			outputValues = calculate(company, outputValues);

			output = new Output();
			output.setCompanyName(company.getCompanyName());
			output.setProperties(outputValues);
			outputRepository.save(output);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return output;
	}

	public Map<String, Double> calculate(CompanyRiskScore companyRiskScore, Map<String, Double> outputValues) {

		List<RiskCalculationLogic> formulaTable = riskCalculationLogicRepository.findAll();
		logger.debug("Total formulas in the calculation logic table: {}", formulaTable.size());

//		Get all the weights from the risk dimension table		
		List<RiskDimension> riskDimensionList = riskDimensionRepository.findAll();

//		This function will be called for every formula in Risk calculation logic table
		for (RiskCalculationLogic riskCalcLogic : formulaTable) {
			calculateWeight.calculateWeight(companyRiskScore, riskCalcLogic, riskDimensionList, outputValues);
		}
		return outputValues;
	}

	@Override
	public CompanyDto getDataByCompanyName(String companyName) {
		CompanyRiskScore company = companyRiskScoreRepository.findByName(companyName);
		CompanyDto companyData = modelMapper.map(company, CompanyDto.class);
		return companyData;
	}

	@Override
	public void updateCompanyData(String companyName, CompanyDto newCompany) {

		CompanyRiskScore company = companyRiskScoreRepository.findByName(companyName);
		Map<String, Integer> properties = newCompany.getProperties();
		company.setProperties(properties);
		companyRiskScoreRepository.save(company);
	}

	@Override
	public void deleteByCompanyName(String companyName) {

//		If company with the given name do not exist throw exception
		if (!companyRiskScoreRepository.existsByName(companyName)) {
			throw new CompanyNotFoundException("Company Not Found!");
		}

//		if (found) -> delete the company from company risk score table
		companyRiskScoreRepository.deleteByName(companyName);

		if (!outputRepository.existsByName(companyName)) {
			throw new CompanyNotFoundException("No Data present of" + " " + companyName + " " + "in output table");
		}

//		if (found) -> delete the output details from the table
		outputRepository.deleteByName(companyName);
	}

	@Override
	public Optional<RiskDimension> addDimension(RiskDimension newDimension) {

		Optional<RiskDimension> saveDimension = Optional.empty();

		try {
			RiskDimension tempDimension = riskDimensionRepoService.addRiskDimension(newDimension);
			saveDimension = Optional.of(tempDimension);
		} catch (DataAlreadyExistException e) {
			e.getMessage();
		}

		return saveDimension;
	}

	@Override
	public void deleteByDimensionName(String dimensionName) throws DimensionNotFoundException {

//		If dimension with the given name do not exist throw exception
//		if (riskDimensionRepository.existsByName(dimensionName).isEmpty()) {
//			throw new DimensionNotFoundException("Dimension Not Found!");
//		}

//		if (found) -> delete the dimension from risk dimension table
		riskDimensionRepository.deleteByName(dimensionName);
	}

	@Override
	public RiskDimension[] getAllDimensions() {

//		Get all dimensions from the DB 
		List<RiskDimension> list = riskDimensionRepository.findAll();
		int size = list.size();
		RiskDimension[] dimensionList = new RiskDimension[size];
		int i = 0;
		for (RiskDimension dimension : list) {
			dimensionList[i++] = dimension;
		}
		return dimensionList;
	}

	@Override
	public RiskDimension getDataByDimensionName(String dimensionName) {
		RiskDimension dimension = riskDimensionRepository.existsByName(dimensionName).get();
		return dimension;
	}

	@Override
	public void updateDimensionData(String dimensionName, RiskDimension newDimension) {
		
		RiskDimension dimension = riskDimensionRepository.existsByName(dimensionName).get();
		dimension.setDimensionName(newDimension.getDimensionName());
		dimension.setValue(newDimension.getValue());
		riskDimensionRepository.save(dimension);
	}

	@Override
	public RiskCalculationLogic[] getAllFormulas() {
		List<RiskCalculationLogic> list = riskCalculationLogicRepository.findAll();
		int size = list.size();
		RiskCalculationLogic[] formulaList = new RiskCalculationLogic[size];
		int i = 0;
		for (RiskCalculationLogic formula : list) {
			formulaList[i++] = formula;
		}
		return formulaList;
	}

	@Override
	public RiskScoreCap[] getAllScores() {
		List<RiskScoreCap> list = riskScoreCapRepository.findAll();
		int size = list.size();
		RiskScoreCap[] scoreCapList = new RiskScoreCap[size];
		int i = 0;
		for (RiskScoreCap scoreCap : list) {
			scoreCapList[i++] = scoreCap;
		}
		return scoreCapList;
	}

	@Override
	public Optional<RiskCalculationLogic> addFormula(RiskCalculationLogic newFormula) {
		
		Optional<RiskCalculationLogic> saveFormula = Optional.empty();

		try {
			RiskCalculationLogic tempFormula = riskCalculationLogicRepoService.addRiskDimension(newFormula);
			saveFormula = Optional.of(tempFormula);
		} catch (DataAlreadyExistException e) {
			e.getMessage();
		}

		return saveFormula;
	}
}
