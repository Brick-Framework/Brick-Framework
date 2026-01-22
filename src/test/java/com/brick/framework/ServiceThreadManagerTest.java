package com.brick.framework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class ServiceThreadManagerTest {
	@Test
	public void success() {
		assertDoesNotThrow(()->{
			ServiceThreadManager.getInstance().submitTask(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
			});
		});
	}
}
