package com.brick.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.utilities.BrickMap;

public abstract class Service {
	public abstract List<String> getExecutionId();
	
	public abstract List<String> getParams();
	
	public abstract void executeService(ExecutionEnvironment env) throws InvalidValidatorId, ServiceValidationFailure, ParameterMismatch, InvalidParams, IllegalAccessException, InvocationTargetException, InvalidServiceId, ParallelServiceResponseMappingFound;
}
