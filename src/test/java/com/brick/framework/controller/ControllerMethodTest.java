package com.brick.framework.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.ExecutionIdNotUnique;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.framework.service.Service;
import com.brick.framework.service.ServiceFactory;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class ControllerMethodTest {
	@Test
	public void duplicateExecutionId() throws FileNotFoundException, InvalidData, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		String filePath = "/test_yaml/duplicate_executionId.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap controllerMap = fileReader.getMap();
    	
    	assertThrows(ExecutionIdNotUnique.class,()->{
    		new ControllerMethod(controllerMap.getBrickMap("/process").getBrickMap("post"));
    	});
	}
	
	@Test
	public void getResponseList() throws FileNotFoundException, InvalidData, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		String filePath = "/controller/valid_controller.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap controllerMap = fileReader.getMap();
    	
    	ControllerMethod method = new ControllerMethod(controllerMap.getBrickMap("/process").getBrickMap("post"));
    	
    	List<Response> responseList = method.getResponseList();
    	
    	assertEquals("message",responseList.getFirst().getKey());
    	assertEquals("abc.response",responseList.getFirst().getValue());
    	assertEquals("tm2",responseList.getLast().getKey());
    	assertEquals("tm2.response",responseList.getLast().getValue());
	}
	
	@Test
	public void executeServices() throws FileNotFoundException, InvalidData, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound, ServiceValidationFailure, IllegalAccessException, InvocationTargetException, InvalidValidatorId, ParameterMismatch, InvalidParams, InvalidServiceId {
		String filePath = "/controller/valid_controller.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap controllerMap = fileReader.getMap();
    	
    	try(MockedStatic<ServiceFactory> mockFactory = Mockito.mockStatic(ServiceFactory.class)) {
    		Service service_abc = mock(Service.class);
    		Service parallel_service = mock(Service.class);

    		
    		mockFactory.when(()->ServiceFactory.getService(any()))
    		.thenReturn(service_abc)
    		.thenReturn(parallel_service);
    		
    		ControllerMethod method = new ControllerMethod(controllerMap.getBrickMap("/process").getBrickMap("post"));
    		ExecutionEnvironment env = new ExecutionEnvironment(null, null);
    		method.executeServices(env);
    		
    		verify(service_abc,times(1)).executeService(any(ExecutionEnvironment.class));
    		verify(parallel_service,times(1)).executeService(any(ExecutionEnvironment.class));
    	}
	}
}
