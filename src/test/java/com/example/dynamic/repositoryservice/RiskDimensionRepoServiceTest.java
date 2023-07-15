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
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.repositories.RiskDimensionRepository;

@ExtendWith(MockitoExtension.class)
class RiskDimensionRepoServiceTest {

	@Mock
	private RiskDimensionRepository riskDimensionRepo;

	@InjectMocks
	private RiskDimensionRepoImpl riskDimensionRepoService;

	private RiskDimension sampleDimension;

	@BeforeEach
	void setUp() {
		// Initialize a sample dimension for testing
		sampleDimension = new RiskDimension();
		sampleDimension.setDimensionName("Test Dimension");
	}

	@Test
	void testAddRiskDimension_DataAlreadyExistException() {
		// Mock dependencies
		when(riskDimensionRepo.findByName(sampleDimension.getDimensionName()))
				.thenReturn(Optional.of(sampleDimension));

		 // Perform the test and assert the exception
		assertThrows(DataAlreadyExistException.class,
				() -> riskDimensionRepoService.addRiskDimension(sampleDimension));

		// Verify the interactions
		verify(riskDimensionRepo).findByName(sampleDimension.getDimensionName());
		verifyNoMoreInteractions(riskDimensionRepo);
	}

}

