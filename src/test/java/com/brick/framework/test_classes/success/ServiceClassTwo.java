package com.brick.framework.test_classes.success;

import com.brick.framework.annotations.Identifier;
import com.brick.framework.annotations.Service;
import com.brick.logger.Logger;

@Service(name="Service Two")
public class ServiceClassTwo {
	
	@Identifier(id="serviceMethodThree")
	public void serviceMethodThree() {
		Logger.info("Inside serviceMethodThree");
	}
	
	@Identifier(id="serviceMethodFour")
	public void serviceMethodFour() {
		Logger.info("Inside serviceMethodFour");
	}

}
