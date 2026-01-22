package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InternalServerErrorTest {
	@Test
	public void success() {
		InternalServerError internalServerError = new InternalServerError();
		internalServerError.setUri("uri");
		internalServerError.setMethod("method");
		
		assertEquals("Internal Server Error", internalServerError.getErrorMessage());
		assertEquals(500, internalServerError.getStatusCode());
		assertEquals("uri", internalServerError.getUri());
		assertEquals("method", internalServerError.getMethod());
	}
}
