package com.brick.framework.response;


public class InvalidContentType extends FailureResponse{
	
	private static final int STATUS_CODE = 415; // Unsuported Media Type
	
	public InvalidContentType(String contentType) {
		super("Content-Type : "+contentType+" not allowed");
		
		this.statusCode = InvalidContentType.STATUS_CODE;
		this.errorMessage = "Content-Type : "+contentType+" not allowed";
	}
}
