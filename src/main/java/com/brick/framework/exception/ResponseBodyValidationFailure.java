package com.brick.framework.exception;

import tools.jackson.databind.JsonNode;

public class ResponseBodyValidationFailure extends Exception {
	public ResponseBodyValidationFailure(JsonNode responseBody) {
		super("Could Not Validate Response Body : "+responseBody);
	}

}
