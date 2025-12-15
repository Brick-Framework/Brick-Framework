package com.brick.framework.response;

public class InternalServerError {
	
	private static final int STATUS_CODE = 500;
	
	private int statusCode;
	private String errorMessage;
	private String uri;
	private String method;
	
	public InternalServerError() {
		statusCode = InternalServerError.STATUS_CODE;
		this.errorMessage = "Internal Server Error";
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	

}
