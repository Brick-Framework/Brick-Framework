package com.brick.framework;

import com.brick.framework.exception.CyclicAutoInitilizationReferenceFound;
import com.brick.framework.exception.DuplicateOpenApiSpecificationFound;
import com.brick.framework.exception.DuplicateServiceFound;
import com.brick.framework.exception.DuplicateServiceIdFound;
import com.brick.framework.exception.DuplicateValidatorFound;
import com.brick.framework.exception.DuplicateValidatorIdFound;
import com.brick.framework.exception.ExecutionIdNotUnique;
import com.brick.framework.exception.InvalidValidatorSigantature;
import com.brick.framework.exception.MultipleControllerDefinitionFound;
import com.brick.framework.exception.MultiplePublicConstructorFound;
import com.brick.framework.exception.NoPublicConstructorFound;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.utility.BrickConstants;
import com.brick.framework.utility.ConfigKeys;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidOpenAPISpecification;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.Config;
import com.brick.utilities.exception.ConfigException;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public class BrickApplication {
		
	private Tomcat tomcat;
	private String basePackage;
	private Thread tomcatServerThread;
	
	public BrickApplication(String basePackage) {
		this.basePackage = basePackage;
	}
	
	/*
	 * Important method that allows other users to start this application
	 */
	public void startApplication() {
		try{
			System.out.println(BrickConstants.BANNER);
			System.out.println(BrickConstants.VERSION);
			
			Thread.currentThread().setName("main-thread");
						
			this.startServer();			
			System.out.println(BrickConstants.STARTUP_SUCCESS_MESSAGE);
		}catch (Exception e){
			Logger.fatal("Unexpected Error Occurred");
			Logger.logException(e);
			Logger.fatal("Shutting Down Program");
			System.exit(1);
		}
	}

	/*
	 * Description: Starts Embedded Tomcat Server
	 */
	private void startServer() throws ConfigException, LifecycleException, InvalidData, InvalidOpenAPISpecification, URISyntaxException, DuplicateOpenApiSpecificationFound, IOException, ClassNotFoundException, DuplicateValidatorIdFound, DuplicateServiceIdFound, InvalidValidatorSigantature, DuplicateServiceFound, DuplicateValidatorFound, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MultiplePublicConstructorFound, NoPublicConstructorFound, CyclicAutoInitilizationReferenceFound, MultipleControllerDefinitionFound, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		Logger.info("Starting Tomcat Server");

		int port = Config.getConfigInteger(ConfigKeys.PORT);

		this.tomcat = new Tomcat();
		this.tomcat.setBaseDir("tomcat-temp");
		this.tomcat.setPort(port);

		// Create and configure executor
		StandardThreadExecutor executor = new StandardThreadExecutor();
		executor.setNamePrefix("brick-framework-tomcat-thread-");
		executor.setMaxThreads(Config.getConfigIntegerOrDefault(ConfigKeys.TOMCAT_MAX_THREADS, ConfigKeys.TOMCAT_DEFAULT_MAX_THREADS));
		executor.setMinSpareThreads(Config.getConfigIntegerOrDefault(ConfigKeys.TOMCAT_MIN_SPARE_THREADS, ConfigKeys.TOMCAT_DEFAULT_MIN_SPARE_THREADS));
		executor.setMaxIdleTime(Config.getConfigIntegerOrDefault(ConfigKeys.TOMCAT_MAX_IDLE_TIME, ConfigKeys.TOMCAT_DEFAULT_MAX_IDLE_TIME));
		executor.setDaemon(true);

		this.tomcat.getService().addExecutor(executor);

		// Create and configure connector
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setPort(port);

		// Get the protocol handler (type-safe)
		Http11NioProtocol protocolHandler = (Http11NioProtocol) connector.getProtocolHandler();

		// Wire the executor directly
		protocolHandler.setExecutor(executor);

		// Set other properties directly on the handler
		protocolHandler.setAcceptCount(Config.getConfigIntegerOrDefault(ConfigKeys.TOMCAT_QUEUE_LENGTH, ConfigKeys.TOMCAT_DEFAULT_QUEUE_LENGTH));
		protocolHandler.setConnectionTimeout(Config.getConfigIntegerOrDefault(ConfigKeys.TOMCAT_TIMEOUT, ConfigKeys.TOMCAT_DEFAULT_TIMEOUT));
		protocolHandler.setMaxKeepAliveRequests(Config.getConfigIntegerOrDefault(ConfigKeys.TOMCAT_MAX_KEEP_ALIVE_REQUEST, ConfigKeys.TOMCAT_DEFAULT_MAX_KEEP_ALIVE_REQUEST));

		// Register connector
		this.tomcat.getService().addConnector(connector);
		this.tomcat.setConnector(connector);

		// Add servlet
		Context context = this.tomcat.addContext("", new File(".").getAbsolutePath());
		Tomcat.addServlet(context, "dispatcher", new DispatcherServlet(basePackage));
		context.addServletMappingDecoded("/*", "dispatcher");
		
		
		
		// Start server 
		this.tomcat.start();
		
		this.tomcatServerThread = new Thread(new Runnable(){
			  @Override
			    public void run() {
					// Server Waiting for Connections
					Logger.info("Tomcat started");
					tomcat.getServer().await(); 
			  }
		});
		this.tomcatServerThread.setName("tomcat-server-thread");

		
		// Starting Server Thread
		this.tomcatServerThread.start();
	}
}
