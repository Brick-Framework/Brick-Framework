package com.brick.framework.exception;

public class InvalidValidatorSigantature extends Exception {
	public InvalidValidatorSigantature(String validatorId,Class<?> returnType) {
		super("Invalid Method Signature for Validator Id :"+ validatorId+", Found "+ returnType + " Should be boolean");
	}

}
