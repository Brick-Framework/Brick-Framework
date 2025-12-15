package com.brick.framework.response;

public class BadRequest extends FailureResponse {
	
	private static final int STATUS_CODE = 400;
	
	public BadRequest() {
		super("Request Body Could not be Validated");
		
		this.statusCode = BadRequest.STATUS_CODE;
		this.errorMessage = "Request Body Could not be Validated";
	}

}
