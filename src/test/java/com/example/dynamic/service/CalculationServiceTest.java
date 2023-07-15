package com.example.dynamic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.models.Output;
import com.example.dynamic.repositories.CompanyRiskScoreRepository;
import com.example.dynamic.repositories.OutputRepository;
import com.example.dynamic.repositories.RiskCalculationLogicRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    protected OutputRepository outputRepository;

    @Mock
    protected CompanyRiskScoreRepository companyRiskScoreRepository;

    @Mock
    protected RiskCalculationLogicRepository riskCalculationLogicRepository;

    @InjectMocks
    protected CalculationServiceImpl calculationService;

    @Test
    void testGetAllCompanies() {
        // Mocking the output repository
        List<Output> outputList = new ArrayList<>();
        Output output1 = new Output();
        output1.setCompanyName("Company 1");
        Output output2 = new Output();
        output2.setCompanyName("Company 2");
        outputList.add(output1);
        outputList.add(output2);
        when(outputRepository.findAll()).thenReturn(outputList);

        // Calling the method under test
        List<Output> result = calculationService.getAllCompanies();

        // Verifying the results
        assertEquals(2, result.size());
        assertEquals("Company 1", result.get(0).getCompanyName());
        assertEquals("Company 2", result.get(1).getCompanyName());

        // Verifying the mock interactions
        verify(outputRepository, times(1)).findAll();
        verifyNoMoreInteractions(outputRepository);
    }

    @Test
    void testGetByOutputByName() {
        // Mocking the output repository
        String companyName = "Company 1";
        Output output = new Output();
        output.setCompanyName(companyName);
        when(outputRepository.findById(companyName)).thenReturn(Optional.of(output));

        // Calling the method under test
        Output result = calculationService.getByOutputByName(companyName);

        // Verifying the results
        assertEquals(companyName, result.getCompanyName());

        // Verifying the mock interactions
        verify(outputRepository, times(1)).findById(companyName);
        verifyNoMoreInteractions(outputRepository);
    }
    
    @Test
    void testGetAllCompanies_ReturnEmptyArrayWhenNoCompaniesExist() {
        // Mocking the output repository to return an empty list
        when(outputRepository.findAll()).thenReturn(Collections.emptyList());

        // Calling the method under test
        List<Output> result = calculationService.getAllCompanies();

        // Verifying the result is an empty array
        assertEquals(0, result.size());

        // Verifying the mock interactions
        verify(outputRepository, times(1)).findAll();
        verifyNoMoreInteractions(outputRepository);
    }

    @Test
    void testGetAllCompaniesData_ReturnEmptyListWhenNoCompaniesExist() {
        // Mocking the company risk score repository to return an empty list
        when(companyRiskScoreRepository.findAll()).thenReturn(Collections.emptyList());

        // Calling the method under test
        List<CompanyDto> result = calculationService.getAllCompaniesData();

        // Verifying the result is an empty list
        assertEquals(0, result.size());

        // Verifying the mock interactions
        verify(companyRiskScoreRepository, times(1)).findAll();
        verifyNoMoreInteractions(companyRiskScoreRepository);
    }
//
//    @Test
//    void testCalculateForAllCompanies_ReturnEmptyListWhenNoCompaniesExist() {
//        // Mocking the company risk score repository to return an empty list
//        when(companyRiskScoreRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Mocking the risk calculation logic repository to return an empty list
//        when(riskCalculationLogicRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // Calling the method under test
//        List<Output> result = calculationService.calculateForAllCompanies();
//
//        // Verifying the result is an empty list
//        assertEquals(0, result.size());
//
//        // Verifying the mock interactions
//        verify(companyRiskScoreRepository, times(1)).findAll();
//        verify(riskCalculationLogicRepository, times(1)).findAll();
//        verifyNoMoreInteractions(companyRiskScoreRepository);
//        verifyNoMoreInteractions(riskCalculationLogicRepository);
//    }
}
