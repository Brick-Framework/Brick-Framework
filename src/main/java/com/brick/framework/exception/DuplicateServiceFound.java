package com.brick.framework.exception;

public class DuplicateServiceFound extends Exception {
	public DuplicateServiceFound(String serviceName) {
		super("Duplicate Service Found with name : " + serviceName);
	}

}
