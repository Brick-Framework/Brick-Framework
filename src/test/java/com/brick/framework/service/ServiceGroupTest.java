package com.brick.framework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class ServiceGroupTest {
	@Test
	public void parallelServiceResposneMapping() throws FileNotFoundException, InvalidData {
		String filePath = "/test_yaml/parallel_service_response_mapping.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap serviceGroupMap = fileReader.getMap();
    	assertThrows(ParallelServiceResponseMappingFound.class, ()->{
    		new ServiceGroup(serviceGroupMap);
    	});
	}
	
	@Test
	public void success_creation() throws FileNotFoundException, InvalidData, InvalidValue, KeyNotFound, ParallelServiceResponseMappingFound {
		String filePath = "/test_yaml/valid_serviceGroup.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap serviceGroupMap = fileReader.getMap();
    	
    	ServiceGroup serviceGroup = new ServiceGroup(serviceGroupMap);
    	
    	assertEquals(0, serviceGroup.getParams().size());
    	assertEquals(Arrays.asList("tm2","tm3"), serviceGroup.getExecutionId());
	}
	
	@Test
	public void executeServices() throws InvalidValue, KeyNotFound, ParallelServiceResponseMappingFound, FileNotFoundException, InvalidData, ServiceValidationFailure, IllegalAccessException, InvocationTargetException, InvalidValidatorId, ParameterMismatch, InvalidParams, InvalidServiceId {
		String filePath = "/test_yaml/valid_serviceGroup.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap serviceGroupMap = fileReader.getMap();
    	
    	
		try(MockedStatic<ServiceFactory> mockFactory = Mockito.mockStatic(ServiceFactory.class)) {
    		Service serviceOne = mock(Service.class);
    		Service serviceTwo = mock(Service.class);
    		
    		mockFactory.when(()->ServiceFactory.getService(any()))
    		.thenReturn(serviceOne)
    		.thenReturn(serviceTwo);
    		ServiceGroup serviceGroup = new ServiceGroup(serviceGroupMap);
    		serviceGroup.executeService(new ExecutionEnvironment(null, null));
    		
    		verify(serviceOne,times(1)).executeService(any(ExecutionEnvironment.class));
    		verify(serviceTwo,times(1)).executeService(any(ExecutionEnvironment.class));
		}
	}

}
