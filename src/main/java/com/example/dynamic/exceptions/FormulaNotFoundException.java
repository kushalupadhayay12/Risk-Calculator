package com.example.dynamic.exceptions;

public class FormulaNotFoundException extends RuntimeException {

	public FormulaNotFoundException(String message) {
		super(message);
	}
}
