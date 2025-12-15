package com.brick.framework.response;

public class NoPathDefinitionFound extends FailureResponse{
	
	private static final int STATUS_CODE = 404; // Path Not Found
	
	public NoPathDefinitionFound(String path) {
		super("Could Not Find Path Definition for path : "+path);
		
		this.statusCode = NoPathDefinitionFound.STATUS_CODE;
		this.errorMessage = "Could Not Find Path Definition for path : "+path;
	}

}
