package com.brick.framework.exception;

public class ParallelServiceResponseMappingFound extends Exception {
	public ParallelServiceResponseMappingFound(String executionId, String param) {
		super("Service Response Mapping Found for Parallely Executing Services executionId : "+executionId+" paramName : "+param);
	}

}
