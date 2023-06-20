package com.example.dynamic.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "error_responce")
public class ErrorResponse {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column
	private long companyId;
	
	@Column
	private String companyName;
	
	@Column
    private String reason_of_failure;
	
	@Column
    private LocalDateTime timestamp;
    
    public ErrorResponse() {}

	public ErrorResponse(long companyId, String companyName, String reason_of_failure, LocalDateTime timestamp) {
		this.companyId = companyId;
		this.companyName = companyName;
		this.reason_of_failure = reason_of_failure;
		this.timestamp = timestamp;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getReason_of_failure() {
		return reason_of_failure;
	}

	public void setMessage(String reason_of_failure) {
		this.reason_of_failure = reason_of_failure;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}

