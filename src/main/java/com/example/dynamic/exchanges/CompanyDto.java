package com.example.dynamic.exchanges;

import java.util.HashMap;
import java.util.Map;

public class CompanyDto {

	private String companyName;
	
	private Map<String, Integer> properties = new HashMap<String, Integer>();
	
	public CompanyDto() {}

	public CompanyDto(String companyName, Map<String, Integer> properties) {
		this.companyName = companyName;
		this.properties = properties;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Map<String, Integer> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Integer> properties) {
		this.properties = properties;
	}
}
