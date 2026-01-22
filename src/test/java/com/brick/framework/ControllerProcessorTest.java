package com.brick.framework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class ControllerProcessorTest {
	@Test
	public void success() {
		assertDoesNotThrow(()->{
			ControllerProcessor controllerProcessor = new ControllerProcessor();
			controllerProcessor.getController("/process");
		});
	}
}
