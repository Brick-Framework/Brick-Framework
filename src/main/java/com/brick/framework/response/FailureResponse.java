package com.brick.framework.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.servlet.http.HttpServletRequest;

@JsonIgnoreProperties({
    "depth", "stackTrace", "cause", "suppressed",
    "localizedMessage", "message"
})
public abstract class FailureResponse extends Exception {
	protected int statusCode;
	private String uri;
	private String method;
	protected String errorMessage;
	
	public FailureResponse(String message) {
		super(message);
	}


	public void setUri(String uri) {
		this.uri = uri;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public int getStatusCode() {
		return statusCode;
	}

	public String getUri() {
		return uri;
	}

	public String getMethod() {
		return method;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
