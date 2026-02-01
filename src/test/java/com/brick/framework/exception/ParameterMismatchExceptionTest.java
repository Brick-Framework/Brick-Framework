package com.brick.framework.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ParameterMismatchExceptionTest {
	@Test
	public void success() {
		ParameterMismatch exception = new ParameterMismatch(1, 2, "ABC");
		assertEquals("2 parameters found for method with Id : ABC should be 1", exception.getMessage());
	}
}
