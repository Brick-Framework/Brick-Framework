package com.brick.framework.utility;

public class ConfigKeys {
	public static final String PORT = "server.port";
	public static final int DEFAULT_PORT = 8080;
	
	public static final String TOMCAT_MAX_THREADS = "server.tomcat.maxThreads";
	public static final int TOMCAT_DEFAULT_MAX_THREADS = 200;
	
	public static final String TOMCAT_MIN_SPARE_THREADS = "server.tomcat.minSpareThreads";
	public static final int TOMCAT_DEFAULT_MIN_SPARE_THREADS = 20;
	
	public static final String TOMCAT_MAX_IDLE_TIME = "server.tomcat.maxIdleTime";
	public static final int TOMCAT_DEFAULT_MAX_IDLE_TIME = 60000;
	
	public static final String TOMCAT_QUEUE_LENGTH = "server.tomcat.queueLength";
	public static final int TOMCAT_DEFAULT_QUEUE_LENGTH = 100;
	
	public static final String TOMCAT_TIMEOUT = "server.tomcat.timeout";
	public static final int TOMCAT_DEFAULT_TIMEOUT = 20000;
	
	public static final String TOMCAT_MAX_KEEP_ALIVE_REQUEST = "server.tomcat.maxKeepAliveRequest";
	public static final int TOMCAT_DEFAULT_MAX_KEEP_ALIVE_REQUEST = 100;
	
	public static final String SERVICE_MIN_THREADS = "server.service.minThreads";
	public static final int SERVICE_DEFAULT_MIN_THREADS = 3;
	
	public static final String SERVICE_MAX_THREADS = "server.service.maxThreads";
	public static final int SERVICE_DEFAULT_MAX_THREADS = 5;
	
	public static final String SERVICE_TIMEOUT = "server.service.timeout";
	public static final int SERVICE_DEFAULT_TIMEOUT = 60;
	
	public static final String SERVICE_QUEUE_LENGTH = "server.service.queueLength";
	public static final int SERVICE_DEFAULT_QUEUE_LENGTH = 10;
}
