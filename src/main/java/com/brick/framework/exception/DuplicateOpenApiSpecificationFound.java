package com.brick.framework.exception;

public class DuplicateOpenApiSpecificationFound extends Exception {
	private final String duplicateEndpoint;
	
	public DuplicateOpenApiSpecificationFound(String endpoint) {
		super("Duplicate OpenApi Specification Found for Enpoint : "+endpoint);
		this.duplicateEndpoint = endpoint;
	}

}
