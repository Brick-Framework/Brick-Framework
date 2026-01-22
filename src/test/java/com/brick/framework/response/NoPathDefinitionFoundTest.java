package com.brick.framework.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class NoPathDefinitionFoundTest {
	@Test
	public void success() {
		String path  = "/somepath";
		NoPathDefinitionFound noPathDefinitionFound = new NoPathDefinitionFound(path);
		noPathDefinitionFound.setUri("uri");
		noPathDefinitionFound.setMethod("method");
		
		assertEquals("Could Not Find Path Definition for path : "+path, noPathDefinitionFound.getErrorMessage());
		assertEquals(404, noPathDefinitionFound.getStatusCode());
		assertEquals("uri", noPathDefinitionFound.getUri());
		assertEquals("method", noPathDefinitionFound.getMethod());
	}
}
