package com.brick.framework.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.brick.framework.AnnotationProcessor;
import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class SingleServiceTest {
	@Test
	public void success_creation() throws FileNotFoundException, InvalidData, InvalidValue, KeyNotFound, ParallelServiceResponseMappingFound {
		String filePath = "/test_yaml/valid_singleService.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap serviceMap = fileReader.getMap();
    	
    	SingleService singleService = new SingleService(serviceMap);
    	
    	assertEquals(0, singleService.getParams().size());
    	assertEquals(Arrays.asList("abc"), singleService.getExecutionId());
	}
	
	@Test
	public void executeServices() throws InvalidValue, KeyNotFound, ParallelServiceResponseMappingFound, FileNotFoundException, InvalidData, ServiceValidationFailure, IllegalAccessException, InvocationTargetException, InvalidValidatorId, ParameterMismatch, InvalidParams, InvalidServiceId, NoSuchMethodException, SecurityException {
		String filePath = "/test_yaml/valid_singleService.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap serviceMap = fileReader.getMap();
 
    	AnnotationProcessor mockAnnotationProcessor = mock(AnnotationProcessor.class);
    	TestClass testClass = new TestClass();
    	Method validatorMethod = TestClass.class.getMethod("validator");
    	Method serviceMethod = TestClass.class.getMethod("service");
    	when(mockAnnotationProcessor.getAutoInitilizedObject(TestClass.class)).thenReturn(testClass);
    	when(mockAnnotationProcessor.getValidatorMethod("validatorMethodOne")).thenReturn(validatorMethod);
    	when(mockAnnotationProcessor.getServiceMethod("serviceMethodOne")).thenReturn(serviceMethod);
    	
		SingleService singleService = new SingleService(serviceMap);
    	assertDoesNotThrow(()->{
    		singleService.executeService(new ExecutionEnvironment(mockAnnotationProcessor, null));
    	});
	}
}

class TestClass {
	public boolean validator() {
		return true;
	}
	
	public void service() {
		Logger.info("Inside Service");
	}
}
