package com.brick.framework.response;


public class MethodNotAllowed extends FailureResponse {
	
	private static final int STATUS_CODE = 405; // Method Not Allowed
	
	public MethodNotAllowed(String method) {
		super("Method : "+method+" not allowed for given uri");
		
		this.statusCode = MethodNotAllowed.STATUS_CODE;
		this.errorMessage = "Method : "+method+" not allowed for given uri";
	}
}
