package com.brick.framework.exception;

public class ExecutionIdNotUnique extends Exception {
	
	public ExecutionIdNotUnique(String path) {
		super("Execution Id Not Unique for "+path);
	}

}
