package com.brick.framework.service;

import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;

public enum ExecutionType {
	SERIAL("serial"),
	PARALLEL("parallel");
	
	private final String executionType;


	ExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public static ExecutionType fromString(String value) throws InvalidValue {
        
        for (ExecutionType pt : values()) {
            if (pt.executionType.equalsIgnoreCase(value)) {
                return pt;
            }
        }

        InvalidValue invalidValue = new InvalidValue(value);
        Logger.logException(invalidValue);
        throw invalidValue;
    }
}
