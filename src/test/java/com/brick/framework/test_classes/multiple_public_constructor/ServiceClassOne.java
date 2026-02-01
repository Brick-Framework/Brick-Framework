package com.brick.framework.test_classes.multiple_public_constructor;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;
import com.brick.logger.Logger;

@Service(name="Service One")
public class ServiceClassOne {
	
	public ServiceClassOne() {
		
	}
	
	public ServiceClassOne(String abc) {
		Logger.info("Inside Constructor" + abc);
	}
	
	@Identifier(id="serviceMethodOne")
	public void serviceMethodOne() {
		Logger.info("Inside serviceMethodOne");
	}
	
	@Identifier(id="serviceMethodTwo")
	public void serviceMethodTwo() {
		Logger.info("Inside serviceMethodTwo");
	}

}
