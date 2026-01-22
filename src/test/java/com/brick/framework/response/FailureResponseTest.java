package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FailureResponseTest {
	@Test
	public void success() {
		String message  = "responseMessage";
		FailureResponse failureResponse = new FailureResponse(message) {
		};
		failureResponse.setUri("uri");
		failureResponse.setMethod("method");
		
		assertEquals("uri", failureResponse.getUri());
		assertEquals("method", failureResponse.getMethod());
	}

}
