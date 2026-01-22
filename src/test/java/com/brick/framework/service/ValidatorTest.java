package com.brick.framework.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.brick.framework.AnnotationProcessor;
import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class ValidatorTest {
	@Test
	public void keyNotFound() throws FileNotFoundException, InvalidData {
		String filePath = "/test_yaml/validator_keyNotFound.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap validatorMap = fileReader.getMap();
    	
    	assertThrows(KeyNotFound.class,()->{
    		new Validator("abc", validatorMap);
    	});
	}
	
	@Test
	public void success() throws FileNotFoundException, InvalidData, KeyNotFound, InvalidValue, InvalidValidatorId, NoSuchMethodException, SecurityException {
		String filePath = "/test_yaml/validator_success.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap validatorMap = fileReader.getMap();
    	
    	Validator validator = new Validator("abc", validatorMap);
    	assertEquals("validatorMethodOne", validator.getValidatorId());
    	assertEquals("abc", validator.getExecutionId());
    	assertEquals(ValidatorResponseType.VISIBLE, validator.getType());
    	assertEquals("Validation failed", validator.getMessage());
    	
    	AnnotationProcessor mockAnnotationProcessor = mock(AnnotationProcessor.class);
    	TestClass testClass = new TestClass();
    	Method validatorMethod = TestClass.class.getMethod("validator");
    	when(mockAnnotationProcessor.getAutoInitilizedObject(TestClass.class)).thenReturn(testClass);
    	when(mockAnnotationProcessor.getValidatorMethod("validatorMethodOne")).thenReturn(validatorMethod);
    	assertDoesNotThrow(()->{
    		validator.performValidation(new ExecutionEnvironment(mockAnnotationProcessor, null));
    	});
	}

}
