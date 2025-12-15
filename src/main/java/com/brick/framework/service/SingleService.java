package com.brick.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.framework.utility.ControllerConstants;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class SingleService extends Service{
	private String serviceId;
	private String executionId;
	private List<Validator> validatorList;
	private List<String> params;
	
	public SingleService(BrickMap brickMap) throws KeyNotFound, InvalidValue {
		this.serviceId = brickMap.getString(ControllerConstants.SERVICE_ID);
		this.params = brickMap.getListOfString(ControllerConstants.PARAMETERS);
		this.executionId = brickMap.getString(ControllerConstants.EXECUTION_ID);
		this.validatorList = new ArrayList<Validator>();
		
		Optional<List<Map<String,Object>>> listOfValidator = brickMap.getOptionalListOfMap(ControllerConstants.VALIDATOR);
		if( listOfValidator.isPresent() ) {
			for( Map<String,Object> val : listOfValidator.get() ) {
				this.validatorList.add(new Validator(this.executionId,new BrickMap(val)));
			}
		}
	}

	@Override
	public List<String> getExecutionId() {
		List<String> executionIdList = new ArrayList<String>();
		executionIdList.add(this.executionId);
		return executionIdList;
	}

	@Override
	public void executeService(ExecutionEnvironment env) throws InvalidValidatorId, ServiceValidationFailure, ParameterMismatch, InvalidParams, IllegalAccessException, InvocationTargetException, InvalidServiceId {
		// Execute Validators
		Logger.info("Starting Validator Execution for Service with executionId" + this.executionId +" with serviceId " + this.serviceId);
		for(Validator v : validatorList ) {
			v.performValidation(env);
		}
		Logger.info("Validator Execution for Service with Service Id " + this.serviceId+ " finished");
		
		//Execute Services
		Logger.info("Starting Service Execution for executionId" + this.executionId+" with Service Id " + this.serviceId);
		Method serviceMethod = env.getServiceMethod(this.serviceId);
		Object serviceObject = env.getAutoInitializedObject( serviceMethod.getDeclaringClass() );
		
		Object[] parameters = new Object[serviceMethod.getParameterCount()];
		if( this.params.size() != parameters.length ) {
			ParameterMismatch parameterMismatch = new ParameterMismatch(parameters.length, this.params.size(), this.serviceId);
			Logger.logException(parameterMismatch);
			throw parameterMismatch;
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		for( int i = 0; i < parameters.length; i++ ) {
			JsonNode jsonParams = env.getParams(this.params.get(i));
			parameters[i] = objectMapper.convertValue(jsonParams, serviceMethod.getParameterTypes()[i] );
		}
		
		//Set Response
		Object serviceResponse = serviceMethod.invoke(serviceObject, parameters);
		env.setServiceResponse(this.executionId, serviceResponse);
		Logger.info("Service Execution for executionId " + this.executionId+" with Service Id " + this.serviceId+" finished");
	}

	@Override
	public List<String> getParams() {
		return this.params;
	}
	
	
	
	
}
