package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BadRequestTest {
	@Test
	public void success() {
		String message  = "responseMessage";
		BadRequest badRequest = new BadRequest();
		badRequest.setUri("uri");
		badRequest.setMethod("method");
		
		assertEquals("Request Body Could not be Validated", badRequest.getErrorMessage());
		assertEquals(400, badRequest.getStatusCode());
		assertEquals("uri", badRequest.getUri());
		assertEquals("method", badRequest.getMethod());
	}
}
