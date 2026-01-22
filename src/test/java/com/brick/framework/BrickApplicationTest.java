package com.brick.framework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class BrickApplicationTest {
	@Test
	public void success() {
		assertDoesNotThrow(()->{
			new BrickApplication("com.brick,.framework.test_classes.success").startApplication();
		});
	}
}
