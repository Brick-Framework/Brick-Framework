package com.brick.framework.test_classes.duplicate_serviceId;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;

@Service(name="Service One")
public class ServiceClassOne {
	
	@Identifier(id="serviceMethodOne")
	public void serviceMethodOne() {
	}
	
	@Identifier(id="serviceMethodOne")
	public void serviceMethodTwo() {
	}

}
