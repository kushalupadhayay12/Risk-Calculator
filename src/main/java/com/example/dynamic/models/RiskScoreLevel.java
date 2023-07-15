package com.example.dynamic.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.example.dynamic.contraints.ValidScoreRange;

@Entity
@Table(name = "risk_score_level")
public class RiskScoreLevel {

	@Id	
	@NotNull
	@Column
	private String level;
	
	@Column
	@NotNull
	private int lowerBound;
	
	@Column
	@NotNull
	private int upperBound;
	
	public RiskScoreLevel() {}
	
	public RiskScoreLevel(@NotNull String level, @NotNull int lowerBound, @NotNull int upperBound) {
		this.level = level;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}
}
