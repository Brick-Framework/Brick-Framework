package com.brick.framework.test_classes.invalid_validator_signature;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Validator;

@Validator(name="Validator One")
public class ValidatorClassOne {
	
	@Identifier(id="validatorMethodOne")
	public void validatorMethodOne() {
	}
	
	@Identifier(id="validatorMethodOne")
	public boolean validatorMethodTwo() {
		return true;
	}

}
