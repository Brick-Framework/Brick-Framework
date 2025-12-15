package com.brick.framework.exception;

public class DuplicateValidatorIdFound extends Exception{
	public DuplicateValidatorIdFound(String id) {
		super("Duplicate Validator found with Id : " + id);
	}

}
