package com.brick.framework.test_classes.duplicate_validatorId;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Validator;

@Validator(name="Validator One")
public class ValidatorClassOne {
	
	@Identifier(id="validatorMethodOne")
	public boolean validatorMethodOne() {
		return true;
	}
	
	@Identifier(id="validatorMethodOne")
	public boolean validatorMethodTwo() {
		return true;
	}

}
