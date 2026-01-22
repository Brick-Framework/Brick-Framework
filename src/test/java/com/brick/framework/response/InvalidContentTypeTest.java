package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InvalidContentTypeTest {
	@Test
	public void success() {
		String contentType  = "application/xml";
		InvalidContentType invalidContentType = new InvalidContentType(contentType);
		invalidContentType.setUri("uri");
		invalidContentType.setMethod("method");
		
		assertEquals("Content-Type : "+contentType+" not allowed", invalidContentType.getErrorMessage());
		assertEquals(415, invalidContentType.getStatusCode());
		assertEquals("uri", invalidContentType.getUri());
		assertEquals("method", invalidContentType.getMethod());
	}
}
