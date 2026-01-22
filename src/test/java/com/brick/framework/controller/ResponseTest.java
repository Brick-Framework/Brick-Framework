package com.brick.framework.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.brick.framework.utility.ControllerConstants;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class ResponseTest {
	@Test
	public void success_keyValue() throws KeyNotFound {
		BrickMap brickMap = mock(BrickMap.class);
		when(brickMap.getString(ControllerConstants.RESPONSE_KEY)).thenReturn("key");
		when(brickMap.getString(ControllerConstants.RESPONSE_VALUE)).thenReturn("value");
		
		Response response = new Response(brickMap);
		assertEquals("key", response.getKey());
		assertEquals("value", response.getValue());
	}
	
	@Test
	public void success_string() throws KeyNotFound {
		String responseVal = "abc.response";
		
		Response response = new Response(responseVal);
		assertEquals("abc", response.getKey());
		assertEquals(responseVal, response.getValue());
		
		responseVal = "abc.response.key";
		
		response = new Response(responseVal);
		assertEquals("key", response.getKey());
		assertEquals(responseVal, response.getValue());
	}
}
