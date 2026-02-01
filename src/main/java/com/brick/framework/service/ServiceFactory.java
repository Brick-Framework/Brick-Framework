package com.brick.framework.service;

import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.utility.ControllerConstants;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.KeyNotFound;

public class ServiceFactory {
	
	private ServiceFactory() {
		super();
	}
	
	public static Service getService(BrickMap brickMap) throws InvalidValue, KeyNotFound, ParallelServiceResponseMappingFound {
		
		if( brickMap.contains(ControllerConstants.SERVICE_GROUP) ) {
			return new ServiceGroup( brickMap.getBrickMap(ControllerConstants.SERVICE_GROUP) );
		}else {
			return new SingleService(brickMap);
		}
	}
}
