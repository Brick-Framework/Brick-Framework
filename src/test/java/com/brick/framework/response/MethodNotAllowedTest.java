package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MethodNotAllowedTest {
	@Test
	public void success() {
		String method  = "GET";
		MethodNotAllowed methodNotAllowed = new MethodNotAllowed(method);
		methodNotAllowed.setUri("uri");
		methodNotAllowed.setMethod("method");
		
		assertEquals("Method : "+method+" not allowed for given uri", methodNotAllowed.getErrorMessage());
		assertEquals(405, methodNotAllowed.getStatusCode());
		assertEquals("uri", methodNotAllowed.getUri());
		assertEquals("method", methodNotAllowed.getMethod());
	}
}
