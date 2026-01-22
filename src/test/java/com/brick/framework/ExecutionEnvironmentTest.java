package com.brick.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.brick.framework.controller.Response;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.utilities.BrickRequestData;
import com.sun.tools.javac.code.Attribute.Array;

import jakarta.servlet.http.Cookie;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

public class ExecutionEnvironmentTest {
	
	@InjectMocks ExecutionEnvironment env;
	
	@Mock AnnotationProcessor annotationProcessor;
	@Mock BrickRequestData brickRequestData;
	@Mock Map<String,JsonNode> serviceResponseMap;
	
	 @BeforeEach
    void setUp() throws Exception {
    	MockitoAnnotations.openMocks(this);
    	
        // 1. Create the instance (Constructor still runs, but we will overwrite fields)
        env = new ExecutionEnvironment(annotationProcessor, brickRequestData);

        // 2. Inject the mocks manually to ensure 'this.response' is never null in the test scope
        setPrivateField(env, "serviceResponseMap", serviceResponseMap);
    }
	 
	 private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
	        Field field = target.getClass().getDeclaredField(fieldName);
	        field.setAccessible(true);
	        field.set(target, value);
	 }
	 
	 @Test
	 public void getResponse_throwsException() {
		 Response response = mock(Response.class);
		 when(response.getKey()).thenReturn("responseKey1");
		 when(response.getValue()).thenReturn("invalid");
		 List<Response> responseList = Arrays.asList(response);
		 
		 assertThrows(InvalidParams.class,()->{
			 env.getResponse(responseList);
		 });
	 }
	 
	 @Test
	 public void getResponse_success() throws InvalidParams {
		 Response response = mock(Response.class);
		 when(response.getKey()).thenReturn("abc");
		 when(response.getValue()).thenReturn("request.body.abc");
		 List<Response> responseList = Arrays.asList(response);
		 
		 ObjectNode requestBody = JsonNodeFactory.instance.objectNode();
		 requestBody.put("abc", "def");
		 
		 when(brickRequestData.getRequestBody()).thenReturn(requestBody);
		 
		 assertEquals(requestBody, env.getResponse(responseList));
	 }
	 
	 @Test
	 public void getValidatorMethod_test() throws InvalidValidatorId {
		 Method m = mock(Method.class);
		 String validatorId = "validatorId";
		 when(annotationProcessor.getValidatorMethod(validatorId)).thenReturn(m);
		 
		 assertEquals(m,env.getValidatorMethod(validatorId));
	 }
	 
	 @Test
	 public void getAutoInitializedObject_test() {
		Class<?> clazz = this.getClass();
		when(annotationProcessor.getAutoInitilizedObject(clazz)).thenReturn(this);
		
		assertEquals(this,env.getAutoInitializedObject(clazz));
	 }
	 
	 @Test
	 public void getServiceMethod_test() throws InvalidServiceId {
		 Method m = mock(Method.class);
		 String serviceId = "serviceId";
		 when(annotationProcessor.getServiceMethod(serviceId)).thenReturn(m);
		 
		 assertEquals(m,env.getServiceMethod(serviceId));
	 }
	 
	 @Test
	 public void setServiceResponse_test() {
		 Rectangle r = new Rectangle(5,10);
		 String executionId = "executionId";
		 
		 env.setServiceResponse(executionId, r);
		 
		 ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
		 objectNode.put("width", 5);
		 objectNode.put("height",10);
		 
		 verify(serviceResponseMap).put(executionId, objectNode);
	 }
	 
	 @Test
	 public void getParams_invalidParamsLength() {
		 String paramName = "invalid";
		 
		 assertThrows(InvalidParams.class, ()->{
			 env.getParams(paramName);
		 });
	 }
	 
	 @Test
	 public void getParams_invalidParamsArgs() {
		 String paramName = "invalid.abc.def";
		 
		 assertThrows(InvalidParams.class, ()->{
			 env.getParams(paramName);
		 });
	 }
	 
	 @Test
	 public void getParams_request() throws InvalidParams {
		 String paramName = "request.body.abc";
		 
		 ObjectNode requestBody = JsonNodeFactory.instance.objectNode();
		 requestBody.put("abc", "def");
		 
		 when(brickRequestData.getRequestBody()).thenReturn(requestBody);
		 
		 assertEquals("def", env.getParams(paramName).asString());
	 }
	 
	 @Test
	 public void getParams_header() throws InvalidParams {
		 String paramName = "request.header.abc";
		 
		 Map<String,String> header = new HashMap<String, String>();
		 header.put("abc", "def");
		 
		 when(brickRequestData.getHeaders()).thenReturn(header);
		 
		 ObjectNode paramNode = JsonNodeFactory.instance.objectNode();
		 paramNode.put("abc", "def");
		 
		 assertEquals(paramNode, env.getParams(paramName));
	 }
	 
	 @Test
	 public void getParams_query() throws InvalidParams {
		 String paramName = "request.query.abc";
		 
		 Map<String,String[]> query = new HashMap<String, String[]>();
		 query.put("abc", new String[] {"def"});
		 
		 when(brickRequestData.getQueryParams()).thenReturn(query);
		 
		 ObjectNode paramNode = JsonNodeFactory.instance.objectNode();
		 ArrayNode arrayNode = paramNode.putArray("abc");
		 arrayNode.add("def");
		 
		 assertEquals(paramNode, env.getParams(paramName));
	 }
	 
	 @Test
	 public void getParams_path() throws InvalidParams {
		 String paramName = "request.path.abc";
		 
		 Map<String,String> path = new HashMap<String, String>();
		 path.put("abc", "def");
		 
		 when(brickRequestData.getPathVariables()).thenReturn(path);
		 
		 ObjectNode paramNode = JsonNodeFactory.instance.objectNode();
		 paramNode.put("abc", "def");
		 
		 assertEquals(paramNode, env.getParams(paramName));
	 }
	 
	 @Test
	 public void getParams_cookie() throws InvalidParams {
		 String paramName = "request.cookie.abc";
		 
		 Cookie c = mock(Cookie.class);
		 when(c.getName()).thenReturn("abc");
		 when(c.getValue()).thenReturn("def");
		 when(brickRequestData.getCookies()).thenReturn(Arrays.asList(c));
		 
		 ObjectNode paramNode = JsonNodeFactory.instance.objectNode();
		 paramNode.put("abc", "def");
		 
		 assertEquals(paramNode, env.getParams(paramName));
	 }
	 
	 @Test
	 public void getParams_service() throws InvalidParams {
		 String paramName = "xyz.response.abc";
		 
		 ObjectNode paramNode = JsonNodeFactory.instance.objectNode();
		 paramNode.put("abc", "def");
		 
		 when(serviceResponseMap.containsKey("xyz")).thenReturn(true);
		 when(serviceResponseMap.get("xyz")).thenReturn(paramNode);
		 
		 assertEquals("def", env.getParams(paramName).asString());
	 }

}

class Rectangle{
	public int width;
	public int height;
	
	public Rectangle(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}
}
