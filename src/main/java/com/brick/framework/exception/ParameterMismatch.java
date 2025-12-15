package com.brick.framework.exception;

public class ParameterMismatch extends Exception {
	public ParameterMismatch(int expectedParameterCount, int actualParameterCount, String methodId) {
		super(actualParameterCount + " parameters found for method with Id : "+methodId+" should be "+expectedParameterCount);
	}

}
