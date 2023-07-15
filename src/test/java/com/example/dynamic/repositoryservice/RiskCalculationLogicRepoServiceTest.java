package com.example.dynamic.repositoryservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dynamic.exceptions.DataAlreadyExistException;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.repositories.RiskCalculationLogicRepository;

@ExtendWith(MockitoExtension.class)
class RiskCalculationLogicRepoServiceTest {

	@Mock
	private RiskCalculationLogicRepository riskCalculationLogicRepository;

	@InjectMocks
	private RiskCalculationLogicRepoServiceImpl riskCalculationLogicRepoService;

	private RiskCalculationLogic sampleFormula;

	@BeforeEach
	void setUp() {
		// Initialize a sample formula for testing
		sampleFormula = new RiskCalculationLogic();
		sampleFormula.setElementName("Test Element");
		sampleFormula.setFormula("1 + 2 * 3");
	}

	@Test
	void testAddRiskDimension_Success() throws DataAlreadyExistException {
		// Mock dependencies
		when(riskCalculationLogicRepository.findByName(sampleFormula.getElementName()))
				.thenReturn(Optional.empty());
		when(riskCalculationLogicRepository.save(sampleFormula)).thenReturn(sampleFormula);

		// Perform the test
		RiskCalculationLogic result = riskCalculationLogicRepoService.addRiskDimension(sampleFormula);

		// Verify the interactions and assertions
		verify(riskCalculationLogicRepository).findByName(sampleFormula.getElementName());
		verify(riskCalculationLogicRepository).save(sampleFormula);
		assertEquals(sampleFormula, result);
	}

	@Test
	void testAddRiskDimension_DataAlreadyExistException() {
		// Mock dependencies
		when(riskCalculationLogicRepository.findByName(sampleFormula.getElementName()))
				.thenReturn(Optional.of(sampleFormula));

		// Perform the test and assert the exception
		assertThrows(DataAlreadyExistException.class,
				() -> riskCalculationLogicRepoService.addRiskDimension(sampleFormula));

		// Verify the interactions
		verify(riskCalculationLogicRepository).findByName(sampleFormula.getElementName());
		verifyNoMoreInteractions(riskCalculationLogicRepository);
	}

}

