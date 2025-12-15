package com.brick.framework.controller;

import com.brick.framework.utility.ControllerConstants;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class Response {
	private String key;
	private String value;
	
	public Response(BrickMap brickMap) throws KeyNotFound {
		this.key = brickMap.getString(ControllerConstants.RESPONSE_KEY);
		this.value = brickMap.getString( ControllerConstants.RESPONSE_VALUE );
	}
	
	public Response(String responseVal) {
		String[] responseValParts = responseVal.split("\\.");
		if( responseValParts.length > 2 ) {
			this.key = responseValParts[responseValParts.length-1];
		}else {
			this.key = responseValParts[0];
		}
		this.value = responseVal;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
