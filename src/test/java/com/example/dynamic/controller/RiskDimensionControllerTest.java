package com.example.dynamic.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.dynamic.controllers.RiskDimensionController;
import com.example.dynamic.models.RiskDimension;
import com.example.dynamic.service.CalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = RiskDimensionController.class)
public class RiskDimensionControllerTest {

	@Autowired
    private MockMvc mvc;

    @MockBean
    private CalculationService calculationService;

    @InjectMocks
    private RiskDimensionController riskController;
    
    @Autowired
	ObjectMapper objectMapper;

    @Test
    public void testReturnAllDimensionDataSuccess() throws Exception {
        // Arrange
        RiskDimension dimension1 = new RiskDimension("info_sec", 50);
        RiskDimension dimension2 = new RiskDimension("res", 30);
        List<RiskDimension> dimensionList = new ArrayList<>();
        dimensionList.add(dimension1);
        dimensionList.add(dimension2);
        Mockito.when(calculationService.getAllDimensions()).thenReturn(dimensionList);

        // Act
        String getUri = "/dimensions";

        URI uri = UriComponentsBuilder.fromPath(getUri).build().toUri();

//        Assert
        mvc.perform(MockMvcRequestBuilders.get(uri))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    public void testReturnDimensionWithGivenName() throws Exception {
//    	Arrange
    	RiskDimension dimension1 = new RiskDimension("info_sec", 50);
    	Mockito.when(calculationService.getDataByDimensionName("info_sec")).thenReturn(dimension1);
    	
//    	Act
    	String getUri = "/dimensions";
    	
    	URI uri = UriComponentsBuilder.fromPath(getUri)
    			.queryParam("name", "info_sec")
    			.build().toUri();
    	
//    	Assert
    	mvc.perform(MockMvcRequestBuilders.get(uri))
    			.andExpect(MockMvcResultMatchers.status().isOk());
    	
    }
    
    @Test
    public void testReturnBadRequestWhenAddingDimensionWhichIsInvalid() throws Exception {
    	
//    	Arrange
    	RiskDimension dimension1 = new RiskDimension("res", 56);
    	Mockito.when(calculationService.addDimension(dimension1)).thenReturn(Optional.empty());
    	
//    	Act
    	String postUri = "/dimensions/add";
    	
    	URI uri = UriComponentsBuilder.fromPath(postUri)
    			.queryParam("name", "res")
    			.queryParam("value", 56)
    			.build().toUri();
				
//    	Assert
    	MockHttpServletResponse response = mvc.perform(post(postUri)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objectMapper.writeValueAsString(uri))
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
    	
    	Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }
    
    @Test
    public void testDeleteDimensionByNameSuccess() throws Exception {
    	
        // Arrange
        String dimensionName = "info_sec";
        Mockito.doNothing().when(calculationService).deleteByDimensionName(dimensionName);

        // Act
        MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/dimensions/{dimensionName}", dimensionName)
        		.contentType(MediaType.APPLICATION_JSON))
        		.andExpect(MockMvcResultMatchers.status().isOk())
        		.andReturn().getResponse();

        // Assert
        verify(calculationService, times(1)).deleteByDimensionName(dimensionName);
        assertEquals(dimensionName + " has been deleted from the Database", response.getContentAsString());
    }
    
    @Test
    public void testUpdateDimensionData() throws Exception {
    	
//         Arrange
        String dimensionName = "resilience";
        RiskDimension newDimension = new RiskDimension("render", 60);
        Mockito.doNothing().when(calculationService).updateDimensionData(dimensionName, newDimension);

//         Act and Assert
        mvc.perform(MockMvcRequestBuilders.put("/dimensions/dimensiondata/{dimensionName}", dimensionName)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newDimension)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
