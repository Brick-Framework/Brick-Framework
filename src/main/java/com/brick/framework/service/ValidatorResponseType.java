package com.brick.framework.service;

import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidValue;

public enum ValidatorResponseType {
	VISIBLE("visible"),
	HIDDEN("hidden");
	
	private final String responseType;


	ValidatorResponseType(String responseType) {
        this.responseType = responseType;
    }

    public static ValidatorResponseType fromString(String value) throws InvalidValue {
        
        for (ValidatorResponseType pt : values()) {
            if (pt.responseType.equalsIgnoreCase(value)) {
                return pt;
            }
        }

        InvalidValue invalidValue = new InvalidValue(value);
        Logger.logException(invalidValue);
        throw invalidValue;
    }
}
