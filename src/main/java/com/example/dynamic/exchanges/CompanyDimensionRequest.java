package com.example.dynamic.exchanges;

public class CompanyDimensionRequest {

	private String dimensionName;
	
	public CompanyDimensionRequest() {}

	public CompanyDimensionRequest(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}
}
