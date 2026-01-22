package com.brick.framework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.brick.openapi.exception.InvalidValue;

public class ExecutionTypeTest {
	@Test
	public void success() throws InvalidValue {
		assertEquals(ExecutionType.SERIAL, ExecutionType.fromString("serial"));
		assertEquals(ExecutionType.PARALLEL, ExecutionType.fromString("parallel"));
		assertThrows(InvalidValue.class, ()->{
			ExecutionType.fromString("invalid");
		});
	}

}
