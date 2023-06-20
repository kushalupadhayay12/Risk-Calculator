package com.example.dynamic.exceptions;

public class CompanyNotFoundException extends RuntimeException{
	
	public CompanyNotFoundException(String message) {
		super(message);
	}
}
