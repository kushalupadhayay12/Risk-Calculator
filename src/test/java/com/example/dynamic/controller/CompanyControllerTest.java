package com.example.dynamic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.dynamic.controllers.CompanyController;
import com.example.dynamic.exchanges.CompanyDimensionRequest;
import com.example.dynamic.exchanges.CompanyDto;
import com.example.dynamic.service.CalculationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = CompanyController.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CompanyControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	protected CalculationService calculationService;
	
	@InjectMocks
	protected CompanyController companyController;
	
	@Autowired
	ObjectMapper objectMapper;
	
	public List<CompanyDto> getCompanyList() {
		Map<String, Integer> props = new HashMap<String, Integer>();
		
		props.put("information_security", 34);
		props.put("conduct", 23);
		CompanyDto dummyCompany1 = new CompanyDto("Nagarro", props);
		
		props.put("information_security", 26);
		props.put("conduct", 18);
		CompanyDto dummyCompany2 = new CompanyDto("Google", props);
		
		List<CompanyDto> companyList = new ArrayList<>();
		companyList.add(dummyCompany1);
		companyList.add(dummyCompany2);
		
		return companyList;
	}
	
	@Test
	public void testEndPointReturnAlltheCompanyDataSuccess() throws Exception {
		
//		Arrange
		List<CompanyDto> companyList = getCompanyList();
		Mockito.when(calculationService.getAllCompaniesData()).thenReturn(companyList);
		
//		Act
		MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/companies")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(MockMvcResultMatchers.status().isOk())
	            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
	            .andExpect(MockMvcResultMatchers.jsonPath("$[0].companyName").value("Nagarro"))
	            .andExpect(MockMvcResultMatchers.jsonPath("$[1].companyName").value("Google"))
	            .andReturn();
		
		String responseBody = result.getResponse().getContentAsString();
		
//		Assert
		List<CompanyDto> responseList = objectMapper.readValue(responseBody, new TypeReference<List<CompanyDto>>() {});

		Assertions.assertEquals(companyList.size(), responseList.size());
	    Assertions.assertEquals(companyList.get(0).getCompanyName(), responseList.get(0).getCompanyName());
	    Assertions.assertEquals(companyList.get(1).getCompanyName(), responseList.get(1).getCompanyName());
	}
	
	@Test
	public void testMethodGetAllCompaniesDataReturnEmptyListWhenNoCompaniesPresent() throws Exception {
		
//		Arrange
		List<CompanyDto> companyList = new ArrayList<CompanyDto>();
		Mockito.when(calculationService.getAllCompaniesData()).thenReturn(companyList);
		
//		Act
		ResponseEntity<List<CompanyDto>> companyListAsResult = companyController.ListOfAllCompanies();
		
//		Assert
		Assertions.assertEquals(HttpStatus.NO_CONTENT, companyListAsResult.getStatusCode());
		
		if(companyListAsResult.getBody() == null) {
			List<CompanyDto> emptyList = new ArrayList<CompanyDto>(); 
			Assertions.assertEquals(0, emptyList.size());
		}
	}
	
	@Test
	public void testMethodGetDetailsOfaPerticularCompanyReturnThatCompanyWithSuccess() throws Exception {
		
//		Arrange
		CompanyDto company = getCompanyList().get(0);
		Mockito.when(calculationService.getDataByCompanyName("Nagarro")).thenReturn(company);
		
//		Act
		ResponseEntity<CompanyDto> companyAsResult = companyController.getDataOfCompanyByName("Nagarro");
		
//		Assert
		Assertions.assertEquals(HttpStatus.OK, companyAsResult.getStatusCode());
		Assertions.assertEquals(companyAsResult.getBody(), company);
	}
	
	@Test
	public void TestAddNewColumnToTheCompanyRiskScoreTableReturnCompanyWithSuccess() throws Exception {
		
//		Arrange
		String dimensionName = "resilience";
		String postColumnUri = "http://localhost:9090/companies/dimensions/add";
		
		URI uri = UriComponentsBuilder
				.fromPath(postColumnUri)
				.queryParam("dimensionName", dimensionName)
				.build().toUri();
		
		CompanyDimensionRequest dimensionRequest = new CompanyDimensionRequest(dimensionName);
		Mockito.when(calculationService.addNewCompanyDimension(dimensionName)).thenReturn(Optional.of(dimensionRequest));
		
//		Act
		MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post(postColumnUri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(uri))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isConflict())
				.andReturn().getResponse();
	}
}
