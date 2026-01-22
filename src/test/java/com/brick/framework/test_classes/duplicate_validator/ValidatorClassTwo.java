package com.brick.framework.test_classes.duplicate_validator;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Validator;

@Validator(name="Validator One")
public class ValidatorClassTwo {
	@Identifier(id="validatorMethodThree")
	public boolean validatorMethodThree() {
		return true;
	}
	
	@Identifier(id="validatorMethodFour")
	public boolean validatorMethodFour() {
		return true;
	}
}
