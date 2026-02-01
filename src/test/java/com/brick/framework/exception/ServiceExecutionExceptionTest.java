package com.brick.framework.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ServiceExecutionExceptionTest {
	@Test
	public void success() {
		Exception excp = new Exception("ABC");
		ServiceExecutionException customException = new ServiceExecutionException(excp);
		assertTrue(customException.getMessage().endsWith("ABC"));
	}
}
