package com.example.dynamic.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "risk_dimension_table")
public class RiskDimension {

	@Id
	@Column
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	@NotNull
	private String name;
	
	@Column
	@NotNull
	@Min(1)
	@Max(99)
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
