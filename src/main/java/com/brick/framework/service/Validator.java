package com.brick.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.framework.utility.ControllerConstants;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class Validator {
	private String executionId;
	private String validatorId;
	private List<String> params;
	private ValidatorResponseType type;
	private Optional<String> message;
	
	public Validator(String executionId,BrickMap brickMap) throws KeyNotFound, InvalidValue {
		this.executionId = executionId;
		this.validatorId = brickMap.getString(ControllerConstants.VALIDATOR_ID);
		this.params = brickMap.getListOfString(ControllerConstants.PARAMETERS);
		this.type = ValidatorResponseType.fromString( brickMap.getString(ControllerConstants.RESPONSE_TYPE) );
		this.message = brickMap.getOptionalString(ControllerConstants.MESSAGE);
		
		if( ValidatorResponseType.VISIBLE == this.type && !this.message.isPresent() ) {
			KeyNotFound keyNotFound = new KeyNotFound(ControllerConstants.MESSAGE);
			Logger.logException(keyNotFound);
			throw keyNotFound;
		}
	}

	public String getValidatorId() {
		return validatorId;
	}

	public String getMessage() {
		if( message.isPresent() ) {
			return message.get();
		}
		
		return "";
	}
	
	public String getExecutionId() {
		return executionId;
	}

	public ValidatorResponseType getType() {
		return type;
	}
	
	
	/*
	 * Description: Calls the ValidatorId method and performs Validation
	 */
	public void performValidation(ExecutionEnvironment env) throws InvalidValidatorId, ParameterMismatch, ServiceValidationFailure, InvalidParams, IllegalAccessException, InvocationTargetException {
		Logger.info("Performing Validation for executionId " + this.executionId +" with ValidatorId : "+this.validatorId);
		Method validationMethod = env.getValidatorMethod(this.validatorId);
		Object validatorObject = env.getAutoInitializedObject( validationMethod.getDeclaringClass() );
		
		Object parameters[] = new Object[validationMethod.getParameterCount()];
		
		if( this.params.size() != parameters.length ) {
			ParameterMismatch parameterMismatch = new ParameterMismatch(parameters.length, this.params.size(), this.validatorId);
			Logger.logException(parameterMismatch);
			throw parameterMismatch;
		}
		
		ObjectMapper objectMapper = new ObjectMapper();
		for( int i = 0; i < parameters.length; i++ ) {
			JsonNode jsonParams = env.getParams(this.params.get(i));
			parameters[i] = objectMapper.convertValue(jsonParams, validationMethod.getParameterTypes()[i]);
		}
		
		boolean isValidationSuccessful = (boolean)validationMethod.invoke(validatorObject, parameters);
		Logger.info("Validation Response for executionId " + this.executionId +" with ValidatorId : "+this.validatorId+" is "+isValidationSuccessful);
		if( !isValidationSuccessful ) {
			ServiceValidationFailure serviceValidaionFailure = new ServiceValidationFailure(this);
			Logger.logException(serviceValidaionFailure);
			throw serviceValidaionFailure;
		}	
		
	}


}
