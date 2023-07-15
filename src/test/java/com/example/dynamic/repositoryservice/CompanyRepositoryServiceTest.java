package com.example.dynamic.repositoryservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.CompanyRiskScore;
import com.example.dynamic.repositories.CompanyRiskScoreRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyRepositoryServiceTest {
	
	@Mock
	private CompanyRiskScoreRepository companyRiskScoreRepository;
	
	@Mock
	private ModelMapper modelMapper;
	
	@InjectMocks
	private CompanyRepositorySericeImpl companyRepositoryService;
	
	private CompanyDto sampleCompanyDto;
	private CompanyRiskScore sampleCompanyEntity;

	@BeforeEach
	void setUp() {
		// Initialize sample objects for testing
		sampleCompanyDto = new CompanyDto();
		sampleCompanyDto.setCompanyName("Test Company");
		sampleCompanyDto.setProperties(Collections.singletonMap("property", 123));
		
		sampleCompanyEntity = new CompanyRiskScore();
		sampleCompanyEntity.setCompanyName("Test Company");
		sampleCompanyEntity.setProperties(Collections.singletonMap("property", 123));
	}

	@Test
	void testAddNewCompany_DataAlreadyExistException() {
		// Mock dependencies
		when(companyRiskScoreRepository.existsByName(sampleCompanyDto.getCompanyName())).thenReturn(true);
		
		// Perform the test and assert the exception
		assertThrows(DataAlreadyExistException.class,
				() -> companyRepositoryService.addNewCompany(sampleCompanyDto));
	}

	@Test
	void testAddNewCompanyDimension_DataAlreadyExistException() {
		 // Mock dependencies
		List<CompanyRiskScore> companies = Collections.singletonList(sampleCompanyEntity);
		when(companyRiskScoreRepository.findAll()).thenReturn(companies);
		when(companyRiskScoreRepository.save(sampleCompanyEntity)).thenReturn(sampleCompanyEntity);
		
		// Perform the test and assert the exception
		assertThrows(DataAlreadyExistException.class,
				() -> companyRepositoryService.addNewCompanyDimension("property"));
		
		// Verify the interactions
		verify(companyRiskScoreRepository).findAll();
		verifyNoMoreInteractions(companyRiskScoreRepository);
	}

}

