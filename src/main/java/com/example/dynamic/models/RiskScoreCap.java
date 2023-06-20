package com.example.dynamic.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "score_cap_table")
public class RiskScoreCap {
	
	@Id
	@Column
	private long id;

    @Column
    private String risk;

    @Column
    private int counter;

    @Column
    private int value;

    // Constructors, getters, and setters

    public RiskScoreCap() {}

    public RiskScoreCap(String risk, int counter, int value) {
        this.risk = risk;
        this.counter = counter;
        this.value = value;
    }

    // Getters and setters

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
