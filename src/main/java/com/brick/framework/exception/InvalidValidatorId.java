package com.brick.framework.exception;

public class InvalidValidatorId extends Exception {
	public InvalidValidatorId(String validatorId) {
		super("Invalid Validator Id : "+ validatorId);
	}

}
