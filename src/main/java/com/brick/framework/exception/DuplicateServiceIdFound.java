package com.brick.framework.exception;

public class DuplicateServiceIdFound extends Exception{
	public DuplicateServiceIdFound(String id) {
		super("Duplicate Service found with Id : " + id);
	}
}
