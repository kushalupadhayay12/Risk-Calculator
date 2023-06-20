package com.example.dynamic.models;

import javax.persistence.*;

@Entity
@Table(name = "risk_dimension_table")
public class RiskDimension {

	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	private String name;
	
	@Column
	private int value;
	
	public RiskDimension() {}

	public RiskDimension(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getDimensionName() {
		return name;
	}

	public void setDimensionName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
