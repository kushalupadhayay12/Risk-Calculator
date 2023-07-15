package com.example.dynamic.repositoryservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.exchanges.CompanyDimensionRequest;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.repositories.CompanyRiskScoreRepository;

@Service
public class CompanyRepositorySericeImpl implements CompanyRepositoryService {
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyRepositorySericeImpl.class);

	@Autowired
	protected CompanyRiskScoreRepository companyRiskScoreRepository;
	
	@Autowired
	protected ModelMapper modelMapper;

	@Override
	public CompanyDto addNewCompany(CompanyDto newCompany) throws DataAlreadyExistException {
		logger.info("Adding new company: {}", newCompany.getCompanyName());
		
		String name = newCompany.getCompanyName();
		Map<String, Integer> properties = newCompany.getProperties();
		
		if(companyRiskScoreRepository.existsByName(name)) {
			logger.warn("Company with the same name already exists: {}", name);
			throw new DataAlreadyExistException("Company with the same name already exists!");
		}
		
		CompanyRiskScore companyEntity = new CompanyRiskScore();
		companyEntity.setCompanyName(name);
		companyEntity.setProperties(properties);
		companyRiskScoreRepository.save(companyEntity);
		
		CompanyDto savedCompany = modelMapper.map(companyEntity, CompanyDto.class);
		logger.info("Added new company: {}", savedCompany.getCompanyName());
		return savedCompany;
	}

	@Override
	public CompanyDimensionRequest addNewCompanyDimension(String dimensionName) throws DataAlreadyExistException {
		logger.info("Adding new dimension to all companies: {}", dimensionName);
		
		List<CompanyRiskScore> companies = companyRiskScoreRepository.findAll();
		
		if(companies.get(0).getProperties().containsKey(dimensionName)) {
			logger.warn("Given company dimension already exists in table: {}", dimensionName);
			throw new DataAlreadyExistException("Given company dimension already exists in table");
		}
		
		for (CompanyRiskScore company : companies) {
			Map<String, Integer> newDimensions = new HashMap<String, Integer>();
			newDimensions = company.getProperties();
			newDimensions.put(dimensionName, 0);
			
			company.setProperties(newDimensions);
			companyRiskScoreRepository.save(company);
		}
		
		CompanyDimensionRequest response = new CompanyDimensionRequest();
		response.setDimensionName(dimensionName);
		
		logger.info("Added new dimension to all companies: {}", dimensionName);
		return response;
	}
}

