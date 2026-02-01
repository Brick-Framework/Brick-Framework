package com.brick.framework.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MultipleControllerDefintionFoundTest {
	@Test
	public void success() {
		MultipleControllerDefinitionFound exception = new MultipleControllerDefinitionFound("ABC");
		assertEquals("Multiple Controller Defintion Found for Path : ABC", exception.getMessage());
	}
}
