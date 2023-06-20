package com.example.dynamic.models;

import java.util.*;
import javax.persistence.*;

import com.sun.istack.NotNull;

@Entity
@Table(name = "company_risk_score")
public class CompanyRiskScore {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long companyId;
	
	@Column
	@NotNull
	private String companyName;
	
	@ElementCollection
	@CollectionTable(name = "company_risk_score_properties", 
			joinColumns = @JoinColumn(name = "company_risk_score_company_id"))
	@MapKeyColumn(name = "dimension_type")
	@Column(name = "dimension_value")
	@NotNull
	private Map<String, Integer> properties = new HashMap<String, Integer>();

	public CompanyRiskScore() {}
	
	public CompanyRiskScore(String companyName, Map<String, Integer> properties) {
		this.companyName = companyName;
		this.properties = properties;
	}

	public Long getId() {
		return companyId;
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

	@Override
	public String toString() {
		return "CompanyRiskScore [companyId=" + companyId + ", companyName=" + companyName + ", properties="
				+ properties + "]";
	}
} 
