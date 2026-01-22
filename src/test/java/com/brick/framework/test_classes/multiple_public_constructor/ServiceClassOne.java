package com.brick.framework.test_classes.multiple_public_constructor;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;

@Service(name="Service One")
public class ServiceClassOne {
	
	public ServiceClassOne() {
		
	}
	
	public ServiceClassOne(String abc) {
	}
	
	@Identifier(id="serviceMethodOne")
	public void serviceMethodOne() {
	}
	
	@Identifier(id="serviceMethodTwo")
	public void serviceMethodTwo() {
	}

}
