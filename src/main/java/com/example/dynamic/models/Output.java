package com.example.dynamic.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "output_table")
public class Output {

	@Id
	@Column
	private String companyName;
	
	@ElementCollection
	@CollectionTable(name = "output_table_properties", 
			joinColumns = @JoinColumn(name = "output_table_company_name"))
	@MapKeyColumn(name = "output_type")
	@Column(name = "output_value")
	private Map<String, Double> properties = new HashMap<String, Double>();
	
	public Output () {}

	public Output(String companyName, Map<String, Double> properties) {
		this.companyName = companyName;
		this.properties = properties;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Map<String, Double> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Double> properties) {
		this.properties = properties;
	}
}
