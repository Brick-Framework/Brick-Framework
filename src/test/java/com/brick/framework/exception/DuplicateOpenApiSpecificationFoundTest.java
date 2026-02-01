package com.brick.framework.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DuplicateOpenApiSpecificationFoundTest {
	@Test
	public void success() {
		DuplicateOpenApiSpecificationFound exception = new DuplicateOpenApiSpecificationFound("ABC");
		assertEquals("Duplicate OpenApi Specification Found for Enpoint : ABC", exception.getMessage());
	}
}
