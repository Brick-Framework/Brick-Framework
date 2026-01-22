package com.brick.framework;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import com.brick.framework.exception.DuplicateOpenApiSpecificationFound;
import com.brick.openapi.exception.InvalidOpenAPISpecification;
import com.brick.utilities.exception.InvalidData;

public class OpenApiProcessorTest {
	@Test
	public void success() throws FileNotFoundException, InvalidData, InvalidOpenAPISpecification, URISyntaxException, DuplicateOpenApiSpecificationFound {
		assertDoesNotThrow(()->{
			new OpenApiProcessor();
		});
		OpenApiProcessor openApiProcessor = new OpenApiProcessor();
		assertFalse( 0 == openApiProcessor.getPaths().size());
	}
}
