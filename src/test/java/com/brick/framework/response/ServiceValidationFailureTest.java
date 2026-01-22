package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.brick.framework.service.Validator;
import com.brick.framework.service.ValidatorResponseType;

public class ServiceValidationFailureTest {
	@Test
	public void success_responseTypeVisible() {
		Validator validator = mock(Validator.class);
		when(validator.getType()).thenReturn(ValidatorResponseType.VISIBLE);
		when(validator.getMessage()).thenReturn("message");
		ServiceValidationFailure serviceValidationFailure = new ServiceValidationFailure(validator);
		serviceValidationFailure.setUri("uri");
		serviceValidationFailure.setMethod("method");
		
		assertEquals("message", serviceValidationFailure.getErrorMessage());
		assertEquals(400, serviceValidationFailure.getStatusCode());
		assertEquals("uri", serviceValidationFailure.getUri());
		assertEquals("method", serviceValidationFailure.getMethod());
	}
	
	@Test
	public void success_responseTypeHidden() {
		Validator validator = mock(Validator.class);
		when(validator.getType()).thenReturn(ValidatorResponseType.HIDDEN);
		ServiceValidationFailure serviceValidationFailure = new ServiceValidationFailure(validator);
		serviceValidationFailure.setUri("uri");
		serviceValidationFailure.setMethod("method");
		
		assertEquals("Validation Failure", serviceValidationFailure.getErrorMessage());
		assertEquals(400, serviceValidationFailure.getStatusCode());
		assertEquals("uri", serviceValidationFailure.getUri());
		assertEquals("method", serviceValidationFailure.getMethod());
	}
}
