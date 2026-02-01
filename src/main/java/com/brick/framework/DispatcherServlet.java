package com.brick.framework;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.brick.framework.controller.Controller;
import com.brick.framework.controller.ControllerMethod;
import com.brick.framework.exception.CyclicAutoInitilizationReferenceFound;
import com.brick.framework.exception.DuplicateOpenApiSpecificationFound;
import com.brick.framework.exception.DuplicateServiceFound;
import com.brick.framework.exception.DuplicateServiceIdFound;
import com.brick.framework.exception.DuplicateValidatorFound;
import com.brick.framework.exception.DuplicateValidatorIdFound;
import com.brick.framework.exception.ExecutionIdNotUnique;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.InvalidValidatorSigantature;
import com.brick.framework.exception.MultipleControllerDefinitionFound;
import com.brick.framework.exception.MultiplePublicConstructorFound;
import com.brick.framework.exception.NoControllerDefinitionFound;
import com.brick.framework.exception.NoPublicConstructorFound;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.exception.ResponseBodyValidationFailure;
import com.brick.framework.response.BadRequest;
import com.brick.framework.response.InternalServerError;
import com.brick.framework.response.InvalidContentType;
import com.brick.framework.response.MethodNotAllowed;
import com.brick.framework.response.NoPathDefinitionFound;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.framework.utility.BrickConstants;
import com.brick.logger.Logger;
import com.brick.openapi.elements.path.Path;
import com.brick.openapi.elements.path.http.methods.HttpMethod;
import com.brick.openapi.exception.InvalidOpenAPISpecification;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickRequestData;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class DispatcherServlet extends HttpServlet {
	 
	private final ObjectMapper mapper;
	private transient final AnnotationProcessor annotationProcessor;
	private transient final OpenApiProcessor openApiProcessor;
	private transient final ControllerProcessor controllerProcessor;
		
	public DispatcherServlet(String basePackage) throws InvalidData, InvalidOpenAPISpecification, URISyntaxException, DuplicateOpenApiSpecificationFound, IOException, ClassNotFoundException, DuplicateValidatorIdFound, DuplicateServiceIdFound, InvalidValidatorSigantature, DuplicateServiceFound, DuplicateValidatorFound, MultiplePublicConstructorFound, NoPublicConstructorFound, CyclicAutoInitilizationReferenceFound, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MultipleControllerDefinitionFound, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		this.mapper = new ObjectMapper();
		
		this.openApiProcessor = new OpenApiProcessor();
		this.controllerProcessor = new ControllerProcessor();
		this.annotationProcessor = new AnnotationProcessor(basePackage);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		
		try {
			processRequest(req, resp);
		}catch(InvalidContentType | NoPathDefinitionFound | MethodNotAllowed | BadRequest | ServiceValidationFailure exception) {
			Logger.logException(exception);
			
			exception.setUri(req.getRequestURI());
			exception.setMethod(req.getMethod());
			
			resp.setStatus(exception.getStatusCode());
			resp.getWriter().write(this.mapper.writeValueAsString(exception));
		}catch(Exception e) {
			Logger.logException(e);
			
			InternalServerError internalServerError = new InternalServerError();
			internalServerError.setMethod(req.getMethod());
			internalServerError.setUri(req.getRequestURI());
			
			resp.setStatus(internalServerError.getStatusCode());
			resp.getWriter().write(this.mapper.writeValueAsString(internalServerError));
		}
	}
	
	
	/*
	 * Description: Process the Given Request, Perform  Check etc
	 */
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws InvalidContentType, NoPathDefinitionFound, MethodNotAllowed, IOException, BadRequest, ResponseBodyValidationFailure, InvalidData, NoControllerDefinitionFound, InvalidValidatorId, ServiceValidationFailure, ParameterMismatch, InvalidParams, IllegalAccessException, InvocationTargetException, InvalidServiceId, ParallelServiceResponseMappingFound {
		String requestURI = request.getRequestURI();	
		
		// Check if Content Type is JSON
		if( null != request.getHeader(BrickConstants.CONTENT_TYPE) && !BrickConstants.getValidContentType().contains(request.getHeader(BrickConstants.CONTENT_TYPE)) ) {
			InvalidContentType invalidContentType = new InvalidContentType(request.getHeader(BrickConstants.CONTENT_TYPE));
			Logger.logException(invalidContentType);
			throw invalidContentType;
		}
		
		Path path = null;
		
		// Finding Path for Endpoint
		for( Path p: this.openApiProcessor.getPaths() ) {
			if( p.matches(requestURI) ) {
				Logger.info(requestURI+" Matched with Pattern "+ p.getUri());
				path = p;
				break;
			}
		}
		
		
		// Throwing Exception if No Path Definition Found
		if( null == path ) {
			NoPathDefinitionFound noPathDefinitionFound = new NoPathDefinitionFound(requestURI);
			Logger.logException(noPathDefinitionFound);
			throw noPathDefinitionFound;
		}
		
		// Throwing Exception if Method is Not Specified
		if( null == path.getMethod(request.getMethod()) ) {
			MethodNotAllowed methodNotAllowed = new MethodNotAllowed(request.getMethod());
			Logger.logException(methodNotAllowed);
			throw methodNotAllowed;
		}
		
		
		Logger.info("Endpoint Matched with Method : "+request.getMethod() );

		HttpMethod method = path.getMethod(request.getMethod());
		Map<String,String> pathVariables = path.getPathVariables(requestURI);
		JsonNode requestBodyJson = BrickRequestData.getRequestBody(request);
		
		Map<String,String> headers = new HashMap<String, String>();
		Enumeration<String> headerKeys = request.getHeaderNames();
		while( headerKeys.hasMoreElements() ) {
			String currentKey = headerKeys.nextElement();
			headers.put(currentKey, request.getHeader(currentKey) );
		}
		
		BrickRequestData brickRequestData = new BrickRequestData(requestBodyJson, pathVariables, headers, Arrays.asList( request.getCookies() ), request.getParameterMap());
		
		if( !method.validateRequest( brickRequestData ) ) { // Checking if Request Data is According to OpenAPI Specification
			BadRequest badRequest = new BadRequest();
			Logger.logException(badRequest);
			throw badRequest;
		}
		
		//Get ControllerMethod
		Controller controller = this.controllerProcessor.getController(path.getUri());
		if( null == controller ) {
			NoControllerDefinitionFound noControllerDefinitionFound = new NoControllerDefinitionFound(path.getUri());
			Logger.logException(noControllerDefinitionFound);
			throw noControllerDefinitionFound;
		}
		
		ControllerMethod controllerMethod = controller.getControllerMethod(request.getMethod());
		if( null == controllerMethod ) {
			NoControllerDefinitionFound noControllerDefinitionFound = new NoControllerDefinitionFound(path.getUri(),request.getMethod());
			Logger.logException(noControllerDefinitionFound);
			throw noControllerDefinitionFound;
		}
		
		//Service Execution
		ExecutionEnvironment env = new ExecutionEnvironment(this.annotationProcessor,brickRequestData);
		Logger.info("Execution Environment Created, Starting Service Execution");
		controllerMethod.executeServices(env);
		JsonNode serviceResponse = env.getResponse(controllerMethod.getResponseList());		
		
		// Set and Validate Response
		if( !method.validateResponse(HttpServletResponse.SC_OK, serviceResponse ) ) { 
			ResponseBodyValidationFailure responseBodyValidationFailure = new ResponseBodyValidationFailure(serviceResponse);
			Logger.logException(responseBodyValidationFailure);
			throw responseBodyValidationFailure;
		}
		response.getWriter().write(serviceResponse.toString());
		response.setStatus(HttpServletResponse.SC_OK);
	}
}