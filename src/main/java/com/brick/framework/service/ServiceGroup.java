package com.brick.framework.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.brick.framework.ExecutionEnvironment;
import com.brick.framework.ServiceThreadManager;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.framework.utility.ControllerConstants;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class ServiceGroup extends Service{
	private ExecutionType type;
	private List<Service> serviceList;
	
	public ServiceGroup(BrickMap brickMap) throws InvalidValue, KeyNotFound, ParallelServiceResponseMappingFound {
		this.serviceList = new ArrayList<Service>();
		this.type = ExecutionType.fromString( brickMap.getString(ControllerConstants.EXECUTION_TYPE) );
		
		List<Map<String,Object>> listOfService = brickMap.getListOfMap(ControllerConstants.SERVICE_LIST);
		for( Map<String,Object> svc : listOfService ) {
			this.serviceList.add( ServiceFactory.getService( new BrickMap(svc) ) );
		}
		
		this.checkParamsValidity();
	}

	@Override
	public List<String> getExecutionId() {
		List<String> executionIdList = new ArrayList<String>();
		for( Service s : this.serviceList ) {
			executionIdList.addAll( s.getExecutionId() );
		}
		
		return executionIdList;
	}

	private void checkParamsValidity() throws ParallelServiceResponseMappingFound {
		//Check if Execution Type is Parallel and if it is Parallel no service is using output of other service
		if( ExecutionType.PARALLEL == type ) {	
			Set<String> executionIdSet = new HashSet<String>();
			for( Service s : this.serviceList ) {
				executionIdSet.addAll(s.getExecutionId());
			}
			
			List<String> paramsList = this.getParams();
			for( String param: paramsList ) {
				String paramStart = param.split("\\.")[0];
				if( executionIdSet.contains(paramStart) ) {
					ParallelServiceResponseMappingFound parallelServiceResponseMappingFound = new ParallelServiceResponseMappingFound(paramStart, param);
					Logger.logException(parallelServiceResponseMappingFound);
					throw parallelServiceResponseMappingFound;
				}
			}
		}
	}

	@Override
	public void executeService(ExecutionEnvironment env) {
		ServiceThreadManager threadManager = ServiceThreadManager.getInstance();
		
		Logger.info("Starting Parallel Exeuction of Services");
		
		List<Future<?>> futures = new ArrayList<>();

        // 1. Submit multiple Runnable tasks
		for( Service s: this.serviceList ) {
			Runnable serviceRunnable = () ->{
				try {
					s.executeService(env);
				} catch (ServiceValidationFailure | IllegalAccessException | InvocationTargetException
						| InvalidValidatorId | ParameterMismatch | InvalidParams | InvalidServiceId
						| ParallelServiceResponseMappingFound e) {
					Logger.logException(e);
					throw new RuntimeException(e);
				}
			};
			futures.add(threadManager.submitTask(serviceRunnable));
		}

        // 2. Wait for all tasks to complete and handle exceptions

        for (Future<?> future : futures) {
            try {
                // Blocks until the task completes or throws an exception
                future.get();
            } catch (ExecutionException e) {
                // The task threw an exception (e.g., RuntimeException from Task 3)
            	Logger.logException(e);
                Logger.error("Task failed! Cause: " + e.getCause().getMessage());
            } catch (InterruptedException e) {
            	Logger.logException(e);
                Thread.currentThread().interrupt();
            }
        }
        
        Logger.info("Parallel Execution of Services Completed");
	}

	@Override
	public List<String> getParams() {
		List<String> paramsList = new ArrayList<String>();
		for( Service s : this.serviceList ) {
			paramsList.addAll(s.getParams());
		}
		
		return paramsList;
	}
}
