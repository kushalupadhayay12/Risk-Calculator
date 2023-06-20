package com.example.dynamic.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

@Entity
@Table(name = "risk_score_level")
public class RiskScoreLevel {

	@Id	
	@Column
	private String level;
	
	@Column
	private String score;
	
	public RiskScoreLevel() {}

	public RiskScoreLevel(String level, String score) {
		this.level = level;
		this.score = score;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
}
