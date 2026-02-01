package com.brick.framework.test_classes.success;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;
import com.brick.logger.Logger;

@Service(name="Service One")
public class ServiceClassOne {
	
	@Identifier(id="serviceMethodOne")
	public void serviceMethodOne() {
		Logger.info("Inside serviceMethodOne");
	}
	
	@Identifier(id="serviceMethodTwo")
	public void serviceMethodTwo() {
		Logger.info("Inside serviceMethodTwo");
	}

}
