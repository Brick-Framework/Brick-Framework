package com.brick.framework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.brick.openapi.exception.InvalidValue;

public class ValidatorResponseTypeTest {
	@Test
	public void success() throws InvalidValue {
		assertEquals(ValidatorResponseType.VISIBLE, ValidatorResponseType.fromString("visible"));
		assertEquals(ValidatorResponseType.HIDDEN, ValidatorResponseType.fromString("hidden"));
		assertThrows(InvalidValue.class, ()->{
			ValidatorResponseType.fromString("invalid");
		});
	}

}
