package com.brick.framework.test_classes.duplicate_service;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;

@Service(name="Service One")
public class ServiceClassOne {
	
	@Identifier(id="serviceMethodOne")
	public void serviceMethodOne() {
	}
	
	@Identifier(id="serviceMethodTwo")
	public void serviceMethodTwo() {
	}

}
