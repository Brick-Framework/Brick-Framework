package com.brick.framework.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.brick.framework.utility.ControllerConstants;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class ControllerMethod {
	private List<Service> serviceList;
	private List<Response> responseList; 
	
	public ControllerMethod(BrickMap brickMap) throws KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		this.serviceList = new ArrayList<Service>();
		
		List<Map<String,Object>> listOfService = brickMap.getListOfMap(ControllerConstants.SERVICES);
		for( Map<String,Object> svc : listOfService ) {
			this.serviceList.add( ServiceFactory.getService( new BrickMap(svc) ) );
		}
		
		String executionIdUnique = areExecutionIdUnique();
		if( null != executionIdUnique ) {
			ExecutionIdNotUnique executionIdNotUnique = new ExecutionIdNotUnique(executionIdUnique);
			Logger.logException(executionIdNotUnique);
			throw executionIdNotUnique;
		}
		
		this.responseList = new ArrayList();
		List<Object> listOfResponseObject = brickMap.getListOfObject(ControllerConstants.RESPONSE);
		for( Object o : listOfResponseObject ) {
			if( o instanceof Map ) {
				this.responseList.add( new Response( new BrickMap(o) ) );
			}else if( o instanceof String ) {
				this.responseList.add( new Response( (String)o ) );
			}
		}
	}
	
	
	/*
	 * Description: Returns name of first duplicate executionId else returns null
	 */
	private String areExecutionIdUnique() {
		Set<String> executionIdSet = new HashSet<String>();
		for( Service s : this.serviceList ) {
			for( String exeId: s.getExecutionId() ) {
				if( executionIdSet.contains(exeId) ) {
					return exeId;
				}
				executionIdSet.add(exeId);
			}
			
		}
		
		return null;
	}
	
	
	
	public List<Response> getResponseList() {
		return responseList;
	}


	/*
	 * Description: Executes all Services One by One
	 */
	public void executeServices(ExecutionEnvironment env) throws InvalidValidatorId, ServiceValidationFailure, ParameterMismatch, InvalidParams, IllegalAccessException, InvocationTargetException, InvalidServiceId, ParallelServiceResponseMappingFound {
		for( Service s: serviceList ) {
			s.executeService(env);
		}
	}
}
