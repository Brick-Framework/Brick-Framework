package com.brick.framework;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brick.framework.controller.Response;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.logger.Logger;
import com.brick.utilities.BrickRequestData;

import jakarta.servlet.http.Cookie;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

public class ExecutionEnvironment {
	
	private static final String REQUEST = "request";
	
	private static final String BODY = "body";
	private static final String HEADER = "header";
	private static final String QUERY = "query";
	private static final String PATH = "path";
	private static final String COOKIE = "cookie";
	private static final String RESPONSE = "response";
	
	private AnnotationProcessor annotationProcessor;
	private BrickRequestData brickRequestData;
	private Map<String,JsonNode> serviceResponseMap; // Responses from services are stored here with executionId as key
	private ObjectMapper objectMapper;
	
	public ExecutionEnvironment(AnnotationProcessor annotationProcessor,BrickRequestData brickRequestData) {
		this.annotationProcessor = annotationProcessor;
		this.brickRequestData = brickRequestData;
		this.serviceResponseMap = new HashMap<String, JsonNode>();
		this.objectMapper = new ObjectMapper();
	}
	
	/*
	 * Description: Returns Response based on response params
	 */
	public JsonNode getResponse(List<Response> responseList) throws InvalidParams {
		ObjectNode node = this.objectMapper.createObjectNode();
		
		for(Response response: responseList) {
			node.set(response.getKey(), this.getParams(response.getValue()) );
		}
		
		return node;
	}
	
	
	/*
	 * Description: Get Params for Their Names
	 */
	public JsonNode getParams(String paramName) throws InvalidParams {
		String[] paramNameParts = paramName.split("\\.");
		
		if( paramNameParts.length < 2 ) {
			InvalidParams invalidParams = new InvalidParams(paramName);
			Logger.logException(invalidParams);
			throw invalidParams;
		}
		
		if( REQUEST.equals(paramNameParts[0]) ) {
			
			
			// Request Body
			if( BODY.equals(paramNameParts[1]) ) {
				JsonNode currentNode = brickRequestData.getRequestBody();
				
				int i = 2;
				while( i < paramNameParts.length && null != currentNode && !currentNode.isMissingNode() ) {
					 currentNode = currentNode.get(paramNameParts[i]);
					i += 1;
				}
				
				return currentNode;
				
			}else if( HEADER.equals(paramNameParts[1]) ) {
				ObjectNode node = this.objectMapper.createObjectNode();
				node.put(paramNameParts[2], brickRequestData.getHeaders().get(paramNameParts[2]));
				return node;
			}else if( QUERY.equals(paramNameParts[1]) ) {
				ObjectNode node = this.objectMapper.createObjectNode();
				ArrayNode arrayNode = node.putArray(paramNameParts[2]);
				
				for( String queryVal : brickRequestData.getQueryParams().get(paramNameParts[2]) ) {
					arrayNode.add(queryVal);
				}
				return node;
			}else if( PATH.equals(paramNameParts[1]) ) {
				ObjectNode node = this.objectMapper.createObjectNode();
				node.put(paramNameParts[2], brickRequestData.getPathVariables().get(paramNameParts[2]) );
				return node;
			}else if( COOKIE.equals(paramNameParts[1]) ) {
				ObjectNode node = new ObjectMapper().createObjectNode();
				
				for( Cookie c: brickRequestData.getCookies() ) {
					if( c.getName().equals(paramNameParts[2]) ) {
						node.put(paramNameParts[2], c.getValue());
						break;
					}
				}
				return node;
			}
			
		}else if( serviceResponseMap.containsKey(paramNameParts[0]) ) {
			
			if( RESPONSE.equals(paramNameParts[1]) ) {
				JsonNode currentNode = serviceResponseMap.get(paramNameParts[0]);
				
				int i = 2;
				while( i < paramNameParts.length && null != currentNode && !currentNode.isMissingNode() ) {
					 currentNode = currentNode.get(paramNameParts[i]);
					i += 1;
				}
				
				return currentNode;
			}
		}
		
		InvalidParams invalidParams = new InvalidParams(paramName);
		Logger.logException(invalidParams);
		throw invalidParams;
	}
	
	/*
	 * Description: Returns Validator Method Based on ValidatorId;
	 */
	public Method getValidatorMethod(String validatorId) throws InvalidValidatorId {
		return this.annotationProcessor.getValidatorMethod(validatorId);
	}
	
	/*
	 * Description: Returns Auto Initialized Object Based on  Class;
	 */
	public Object getAutoInitializedObject(Class<?> clazz) {
		return this.annotationProcessor.getAutoInitilizedObject(clazz);
	}
	
	/*
	 * Description: Returns Service Method based on ServiceId
	 */
	public Method getServiceMethod(String serviceId) throws InvalidServiceId {
		return this.annotationProcessor.getServiceMethod(serviceId);
	}
	
	public void setServiceResponse(String executionId,Object response) {
		this.serviceResponseMap.put(executionId, this.objectMapper.valueToTree(response));
	}
}
