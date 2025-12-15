package com.brick.framework.exception;

public class CyclicAutoInitilizationReferenceFound extends Exception{
	public CyclicAutoInitilizationReferenceFound(String message) {
		super(message);
	}

}
