package com.brick.framework.test_classes.duplicate_service;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;

@Service(name="Service One")
public class ServiceClassTwo {
	
	@Identifier(id="serviceMethodThree")
	public void serviceMethodThree() {
	}
	
	@Identifier(id="serviceMethodFour")
	public void serviceMethodFour() {
	}

}
