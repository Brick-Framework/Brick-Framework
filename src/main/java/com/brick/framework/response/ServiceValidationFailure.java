package com.brick.framework.response;

import com.brick.framework.service.ValidatorResponseType;
import com.brick.framework.service.Validator;

public class ServiceValidationFailure extends FailureResponse {
	private static final String DEFAULT_MESSAGE = "Validation Failure";
	private static final int FAILURE_STATUS_CODE = 400;
	
	public ServiceValidationFailure(Validator v) {
		super("ServiceValidation Failed for Service with executionId : "+v.getExecutionId()+" with validatorId " + v.getValidatorId());
		if( ValidatorResponseType.VISIBLE == v.getType() ) {
			this.errorMessage = v.getMessage();
		}else {
			this.errorMessage = DEFAULT_MESSAGE;
		}
		this.statusCode = FAILURE_STATUS_CODE;
	}
}
