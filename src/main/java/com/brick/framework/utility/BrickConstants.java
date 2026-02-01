package com.brick.framework.utility;

import java.util.Arrays;
import java.util.List;

public class BrickConstants {
	
	private BrickConstants() {
		super();
	}
	
	public static final String BANNER = "__________        .__        __    \r\n"
			+ "\\______   \\_______|__| ____ |  | __\r\n"
			+ " |    |  _/\\_  __ \\  |/ ___\\|  |/ /\r\n"
			+ " |    |   \\ |  | \\/  \\  \\___|    < \r\n"
			+ " |______  / |__|  |__|\\___  >__|_ \\\r\n"
			+ "        \\/                \\/     \\/";
	
	public static final String VERSION = "v1.0.0";
	public static final String STARTUP_SUCCESS_MESSAGE = "Application Running.........................";
	
	public static final String OPENAPI_ROOT_PATH = "/openapi";
	public static final String CONTROLLER_ROOT_PATH = "/controller";
	
	public static final List<String> VALID_OPENAPI_EXTENSION = Arrays.asList("yaml");
	public static final String VALID_CONTROLLER_EXTENSION = "yaml";
	
	public static final String CONTENT_TYPE = "Content-Type"; //Header Key
	public static final String JSON_DATA = "application/json";
	public static final List<String> VALID_CONTENT_TYPE = Arrays.asList(BrickConstants.JSON_DATA);
}
