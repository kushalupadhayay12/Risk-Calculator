package com.example.dynamic.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.dynamic.controllers.RiskController;
import com.example.dynamic.models.RiskCalculationLogic;
import com.example.dynamic.models.RiskScoreCap;
import com.example.dynamic.service.CalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RiskController.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RiskControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	protected CalculationService calculationService;
	
	@InjectMocks
	protected RiskController riskController;
	
	@Autowired
	ObjectMapper objectMapper;
	
	public List<RiskCalculationLogic> getFormulas() {
		List<RiskCalculationLogic>  formulas = new ArrayList<>();
		formulas.add(new RiskCalculationLogic("temp1", "res+info_sec"));
		formulas.add(new RiskCalculationLogic("temp2", "con+info_sec"));
		formulas.add(new RiskCalculationLogic("temp3", "render+info_sec"));
		return formulas;
	}
	
	@Test
	public void testInvalidFormulaRequestSoReturnBadRequest() throws Exception {
		
//		Arrange
		String formulaPostUri = "http://localhost:9090/formulas/add";
		
//		Act
		URI uri = UriComponentsBuilder
				.fromPath(formulaPostUri)
				.queryParam("elementName", "temp4")
				.queryParam("formulaValue", "temp-resilience+condcut")
				.build().toUri();
		
		MockHttpServletResponse response = mvc.perform(post(formulaPostUri)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(uri))
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

//		Assert
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}
	
	@Test
	public void testReturnFormulaIfValidPostFormula() throws Exception {
		
//		Arrange
		RiskCalculationLogic dummyFormula = new RiskCalculationLogic("temp", "resilience+conduct");
		Mockito.when(calculationService.addFormula(dummyFormula)).thenReturn(Optional.of(dummyFormula));
		
//		Act
		URI uri = UriComponentsBuilder
				.fromPath("")
				.queryParam("elementName", "temp")
				.queryParam("formula", "resilience+conduct")
				.build().toUri();
		
//		Assert
		ResponseEntity<RiskCalculationLogic> formulaAsResult = riskController.addFormula(dummyFormula);
		System.out.println(formulaAsResult.getStatusCodeValue());
		assertEquals(HttpStatus.OK.value(), formulaAsResult.getStatusCodeValue());
	}
	
	@Test
	public void testReturnOkIfValidDeleteFormula() throws Exception {
		
//		Arrange
		RiskCalculationLogic dummyFormula = new RiskCalculationLogic("res_weight", "resilience+conduct*100");
		
//		Act
		URI uri = UriComponentsBuilder
				.fromPath("")
				.queryParam("elementName", "res_weight")
				.build().toUri();
		
//		Assert
		ResponseEntity<String> formulaAsResult = riskController.deleteFormulaByName("res_weight");
		System.out.println(formulaAsResult.getStatusCodeValue());
		assertEquals(HttpStatus.OK.value(), formulaAsResult.getStatusCodeValue());
	}
	
	@Test
	public void testReturnAllRiskScoresFormDataBase() throws Exception {
		
//		Arrange
		RiskScoreCap scoreCap1 = new RiskScoreCap("High risk", 1, 60); 
		RiskScoreCap scoreCap2 = new RiskScoreCap("High risk", 2, 40);
		RiskScoreCap scoreCap3 = new RiskScoreCap("Low risk", 1, 20);
		RiskScoreCap scoreCap4 = new RiskScoreCap("Very low risk", 1, 80);
		
		List<RiskScoreCap> scoreCapList = Stream.of(scoreCap1, scoreCap2, scoreCap3, scoreCap4)
                .collect(Collectors.toList()); 
		
		when(calculationService.getAllScores()).thenReturn(scoreCapList);
		
//		Act
		String getScoreUri = "http://localhost:9090/riskscores";
		
		URI uri = UriComponentsBuilder
				.fromPath(getScoreUri)
				.build().toUri();
		
		mvc.perform(MockMvcRequestBuilders.get("/riskscores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
		
//		Assert
		verify(calculationService, times(1)).getAllScores();
	}

    @Test
    public void testGetAllFormulas() throws Exception {
        List<RiskCalculationLogic> formulaList = getFormulas();
        when(calculationService.getAllFormulas()).thenReturn(formulaList);

        mvc.perform(MockMvcRequestBuilders.get("/formulas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].elementName").value(formulaList.get(0).getElementName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].elementName").value(formulaList.get(1).getElementName()));

        verify(calculationService, times(1)).getAllFormulas();
    }
    
    @Test 
    public void deleteFormulaWithGivenNameWithSuccess() throws Exception {
    	
//    	Arrange
    	String formulaName = "res_weight";
    	Mockito.doNothing().when(calculationService).deleteByFormulaByName(formulaName);
    	
//    	Act
    	ResponseEntity<String> response = riskController.deleteFormulaByName(formulaName);
    	
//    	Assert
    	Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    	Assertions.assertEquals(formulaName + " has been deleted from the Database", response.getBody());
    }
}
