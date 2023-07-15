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
import com.example.dynamic.exceptions.DimensionNotFoundException;
import com.example.dynamic.exceptions.FormulaNotFoundException;
import com.example.dynamic.exceptions.RiskLevelNotFoundException;
import com.example.dynamic.exceptions.ScoreCapNotFoundException;
import com.example.dynamic.exchanges.CompanyDimensionRequest;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.models.ErrorResponse;
import com.example.dynamic.models.Output;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.models.RiskScoreLevel;
import com.example.dynamic.repositories.CompanyRiskScoreRepository;
import com.example.dynamic.repositories.JobExecutionDetailsRepository;
import com.example.dynamic.repositories.OutputRepository;
import com.example.dynamic.repositories.RiskCalculationLogicRepository;
import com.example.dynamic.repositories.RiskDimensionRepository;
import com.example.dynamic.repositories.RiskScoreCapRepository;
import com.example.dynamic.repositories.RiskScoreLevelRepository;
import com.example.dynamic.repositoryservice.CompanyRepositoryService;
import com.example.dynamic.repositoryservice.RiskCalculationLogicRepoService;
import com.example.dynamic.repositoryservice.RiskDimensionRepoService;

@Service
public class CalculationServiceImpl implements CalculationService {

	@Autowired
	protected CompanyRiskScoreRepository companyRiskScoreRepository;
	
	@Autowired
	protected CompanyRepositoryService companyRepositoryService;

	@Autowired
	protected RiskDimensionRepository riskDimensionRepository;

	@Autowired
	protected RiskDimensionRepoService riskDimensionRepoService;
	
	@Autowired
	protected RiskScoreCapRepository riskScoreCapRepository;
	
	@Autowired
	protected RiskScoreLevelRepository riskScoreLevelRepository;

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
	public List<Output> getAllCompanies() {
		logger.info("Retrieving all companies.");
		List<Output> outputList = outputRepository.findAll();
		logger.info("Retrieved all companies successfully.");
		return outputList;
	}

	@Override
	public List<CompanyDto> getAllCompaniesData() {
		logger.info("Retrieving all company data.");
		List<CompanyRiskScore> List = companyRiskScoreRepository.findAll();
		List<CompanyDto> companyList = new ArrayList<>();
		for (CompanyRiskScore companyRiskScore : List) {
			CompanyDto companyData = modelMapper.map(companyRiskScore, CompanyDto.class);
			companyList.add(companyData);
		}
		logger.info("Retrieved all company data successfully.");
		return companyList;
	}

	@Override
	public Output getByOutputByName(String companyName) {
		logger.info("Retrieving output for company: {}", companyName);
		Optional<Output> outputEntity = outputRepository.findById(companyName);
		Output output = outputEntity.orElse(null);
		if (output != null) {
			logger.info("Retrieved output for company: {}", companyName);
		} else {
			logger.warn("Output not found for company: {}", companyName);
		}
		return output;
	}

	@Override
	public List<Output> calculateForAllCompanies() {
		logger.info("Calculating risk scores for all companies...");

		List<Output> outputList = new ArrayList<>();

	List<Map<String, Double>> ans = new ArrayList<>();

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
				// Handle the exception and log the error
				logger.error("Error calculating risk score for company: {}", companyRiskScore.getCompanyName(), ex);

				Optional<ErrorResponse> existedStatus = statusRepo.findByCompanyId(companyRiskScore.getId());

				if (existedStatus.isPresent()) {
					ErrorResponse updatedStatus = existedStatus.get();
					updatedStatus.setTimestamp(LocalDateTime.now());
					statusRepo.save(updatedStatus);
				} else {
					ErrorResponse errorResponse = new ErrorResponse(companyRiskScore.getId(),
							companyRiskScore.getCompanyName(), ex.toString(), LocalDateTime.now());
					statusRepo.save(errorResponse);
				}
			}
			logger.info("Risk score calculated and saved for company: {}", companyRiskScore.getCompanyName());
		}

		logger.info("Risk scores calculated for all companies.");
		outputList = outputRepository.findAll();
		return outputList;
	}

	@Override
	public Output calculateForNewOne(CompanyRiskScore company) {
		logger.info("Calculating risk score for new company: {}", company.getCompanyName());

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
			logger.error("Error calculating risk score for new company: {}", company.getCompanyName(), e);
		}
		logger.info("Risk score calculated and saved for new company: {}", company.getCompanyName());
		return output;
	}

	public Map<String, Double> calculate(CompanyRiskScore companyRiskScore, Map<String, Double> outputValues) {
		logger.info("Calculating risk score for company: {}", companyRiskScore.getCompanyName());

		List<RiskCalculationLogic> formulaTable = riskCalculationLogicRepository.findAll();
		logger.debug("Total formulas in the calculation logic table: {}", formulaTable.size());

		List<RiskDimension> riskDimensionList = riskDimensionRepository.findAll();

		for (RiskCalculationLogic riskCalcLogic : formulaTable) {
			calculateWeight.calculateWeight(companyRiskScore, riskCalcLogic, riskDimensionList, outputValues);
		}
		logger.info("Risk score calculated for company: {}", companyRiskScore.getCompanyName());
		return outputValues;
	}

	@Override
	public CompanyDto getDataByCompanyName(String companyName) {
		logger.info("Retrieving data for company: {}", companyName);
		CompanyRiskScore company = companyRiskScoreRepository.findByName(companyName);
		CompanyDto companyData = modelMapper.map(company, CompanyDto.class);
		logger.info("Retrieved data for company: {}", companyName);
		return companyData;
	}

	@Override
	public void updateCompanyData(String companyName, CompanyDto newCompany) {
		logger.info("Updating data for company: {}", companyName);
		CompanyRiskScore company = companyRiskScoreRepository.findByName(companyName);
		Map<String, Integer> properties = newCompany.getProperties();
		company.setProperties(properties);
		companyRiskScoreRepository.save(company);
		logger.info("Updated data for company:{}", companyName);
	}

	@Override
	public void deleteByCompanyName(String companyName) {
		logger.info("Deleting data for company: {}", companyName);

		if (!companyRiskScoreRepository.existsByName(companyName)) {
			logger.warn("Company not found: {}", companyName);
			throw new CompanyNotFoundException("Company Not Found!");
		}

		companyRiskScoreRepository.deletePropertiesByName(companyName);
		companyRiskScoreRepository.deleteByName(companyName);

		if (!outputRepository.existsByName(companyName)) {
			logger.warn("Output data not found for company: {}", companyName);
			throw new CompanyNotFoundException("No Data present of" + " " + companyName + " " + "in output table");
		}

		outputRepository.deletePropertiesByName(companyName);
		outputRepository.deleteByName(companyName);

		logger.info("Deleted data for company: {}", companyName);
	}

	@Override
	public Optional<RiskDimension> addDimension(RiskDimension newDimension) {
		logger.info("Adding new risk dimension: {}", newDimension.getDimensionName());

		Optional<RiskDimension> saveDimension = Optional.empty();

		try {
			RiskDimension tempDimension = riskDimensionRepoService.addRiskDimension(newDimension);
			saveDimension = Optional.of(tempDimension);
			logger.info("Added new risk dimension: {}", newDimension.getDimensionName());
		} catch (DataAlreadyExistException e) {
			logger.error("Failed to add risk dimension due to existing data: {}", newDimension.getDimensionName());
			e.getMessage();
		}

		return saveDimension;
	}

	@Override
	public void deleteByDimensionName(String dimensionName) throws DimensionNotFoundException {
		logger.info("Deleting risk dimension: {}", dimensionName);

		if (!riskDimensionRepository.existsByName(dimensionName)) {
			logger.warn("Risk dimension not found: {}", dimensionName);
			throw new DimensionNotFoundException("Dimension Not Found!");
		}

		riskDimensionRepository.deleteByName(dimensionName);

		logger.info("Deleted risk dimension: {}", dimensionName);
	}

	@Override
	public List<RiskDimension> getAllDimensions() {
		logger.info("Retrieving all risk dimensions.");

		List<RiskDimension> dimensionList = riskDimensionRepository.findAll();

		logger.info("Retrieved all risk dimensions.");
		return dimensionList;
	}

	@Override
	public RiskDimension getDataByDimensionName(String dimensionName) {
		logger.info("Retrieving data for risk dimension: {}", dimensionName);

		RiskDimension dimension = riskDimensionRepository.findByName(dimensionName).orElse(null);

		if (dimension == null) {
			logger.warn("Risk dimension not found: {}", dimensionName);
		} else {
			logger.info("Retrieved data for risk dimension: {}", dimensionName);
		}

		return dimension;
	}

	@Override
	public void updateDimensionData(String dimensionName, RiskDimension newDimension) {
		logger.info("Updating data for risk dimension: {}", dimensionName);

		RiskDimension dimension = riskDimensionRepository.findByName(dimensionName).orElse(null);

		if (dimension != null) {
			dimension.setDimensionName(newDimension.getDimensionName());
			dimension.setValue(newDimension.getValue());
			riskDimensionRepository.save(dimension);
			logger.info("Updated data for risk dimension: {}", dimensionName);
		} else {
			logger.warn("Risk dimension not found: {}", dimensionName);
		}
	}

	@Override
	public List<RiskCalculationLogic> getAllFormulas() {
		logger.info("Retrieving all risk calculation formulas.");

		List<RiskCalculationLogic> formulaList = riskCalculationLogicRepository.findAll();

		logger.info("Retrieved all risk calculation formulas.");
		return formulaList;
	}

	@Override
	public List<RiskScoreCap> getAllScores() {
		logger.info("Retrieving all risk score caps.");

		List<RiskScoreCap> list = riskScoreCapRepository.findAll();

		logger.info("Retrieved all risk score caps.");
		return list;
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

	@Override
	public Optional<CompanyDto> addNewCompany(CompanyDto newCompany) {
		
		Optional<CompanyDto> addCompany = Optional.empty();
		
		try {
			CompanyDto tempCompany = companyRepositoryService.addNewCompany(newCompany);
			addCompany = Optional.of(tempCompany);
		} catch (DataAlreadyExistException e) {
			System.out.println(e.getMessage());
		}
		
		return addCompany;
	}

	@Override
	public Optional<CompanyDimensionRequest> addNewCompanyDimension(String dimensionName) {
		
		 Optional<CompanyDimensionRequest> addedCompanyDimension = Optional.empty();
		 
		 try {
			 CompanyDimensionRequest tempCompanyDimension = companyRepositoryService.addNewCompanyDimension(dimensionName);
			 addedCompanyDimension = Optional.of(tempCompanyDimension);
		} catch (DataAlreadyExistException e) {
			System.out.println(e.getMessage());
		}
		 
		 return addedCompanyDimension;
	}

	@Override
	public List<ErrorResponse> getAllJobStatus() {
		
		List<ErrorResponse> jobStatusList = statusRepo.findAll();
		return jobStatusList;
	}

	@Override
	public void deleteByFormulaByName(String elementName) {
		
//		If formula with the given name do not exist throw exception
		if (!riskCalculationLogicRepository.existsByName(elementName)) {
			throw new FormulaNotFoundException("Formula Not Found!");
		}

//		if (found) -> delete the formula from risk formula table
		riskCalculationLogicRepository.deleteByName(elementName);
	}

	@Override
	public RiskCalculationLogic getDataByFormulaName(String elementName) {
		
		logger.info("Retrieving data for formula: {}", elementName);

		RiskCalculationLogic formula = riskCalculationLogicRepository.findByName(elementName).orElse(null);

		if (formula == null) {
			logger.warn("formula not found: {}", elementName);
		} else {
			logger.info("Retrieved data for formula: {}", elementName);
		}

		return formula;
	}

	@Override
	public RiskScoreCap getDataByRiskScoreCapName(String riskScore, int counter) {

		RiskScoreCap riskScoreCap = riskScoreCapRepository.findByRiskScoreAndCounter(riskScore, counter);
		return riskScoreCap;
	}

	@Override
	public void updateScoreCap(RiskScoreCap riskCapScore, String riskScore, int counter) {
		
		logger.info("Retrieving data for riskScoreCap: {}", riskScore + "with counter " + counter);
		
		RiskScoreCap riskScoreCap = riskScoreCapRepository.findByRiskScoreAndCounter(riskScore, counter);
		
		riskScoreCap.setCounter(riskCapScore.getCounter());
		riskScoreCap.setValue(riskCapScore.getValue());
		
		riskScoreCapRepository.save(riskScoreCap);
	}

	@Override
	public void deleteScoreCap(String riskScore, int counter) {
		
		RiskScoreCap riskScoreCap = riskScoreCapRepository.findByRiskScoreAndCounter(riskScore, counter);
		
		if(riskScoreCap.equals(null)) {
			throw new ScoreCapNotFoundException("The Score Cap you are trying to delete is not available");
		} else {
			riskScoreCapRepository.delete(riskScoreCap);
		}
	}

	@Override
	public List<RiskScoreLevel> getRiskScoreLevels() {
		
		List<RiskScoreLevel> riskLevelList = riskScoreLevelRepository.findAll();
		return riskLevelList;
	}
	
	@Override
	public RiskScoreLevel getRiskLevelById(String level) {

		Optional<RiskScoreLevel> riskScore = riskScoreLevelRepository.findById(level);
		return riskScore.get();
	}

	@Override
	public void editRiskLevel(RiskScoreLevel newRiskScoreLevel, String level) {
		
		RiskScoreLevel riskScoreLevelEntity = riskScoreLevelRepository.findById(level).orElse(null);
		
		riskScoreLevelEntity.setLowerBound(newRiskScoreLevel.getLowerBound());
		riskScoreLevelEntity.setUpperBound(newRiskScoreLevel.getUpperBound());
		
		riskScoreLevelRepository.save(riskScoreLevelEntity);
	}

	@Override
	public void deleteRiskLevelById(String level) throws RiskLevelNotFoundException {
		
		RiskScoreLevel riskScoreLevelEntity = riskScoreLevelRepository.findById(level).orElse(null);
		
		if(riskScoreLevelEntity.equals(null)) {
			throw new RiskLevelNotFoundException("Risk Level Do Not Exist");
		} else {
			riskScoreLevelRepository.deleteById(level);
		}
	}
}
