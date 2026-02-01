package com.brick.framework.controller;

import java.util.HashMap;
import java.util.Map;

import com.brick.framework.exception.ExecutionIdNotUnique;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class Controller {
	private Map<String,ControllerMethod> methodMap;
	
	public Controller(BrickMap brickMap) throws KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		this.methodMap = new HashMap<String, ControllerMethod>();
		
		for( Map.Entry<String, Object> entry : brickMap ) {
			this.methodMap.put(entry.getKey().toUpperCase(), new ControllerMethod( new BrickMap( entry.getValue() ) ) );
		}
	}
	
	
	/*
	 * Description: Returns ControllerMethod Based on Method
	 */
	public ControllerMethod getControllerMethod(String method) {
		return this.methodMap.get(method);
	}
}
