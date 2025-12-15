package com.brick.framework.exception;

public class DuplicateValidatorFound extends Exception {
	public DuplicateValidatorFound(String validatorName) {
		super("Duplicate Validator Found with name : "+validatorName);
	}
}
