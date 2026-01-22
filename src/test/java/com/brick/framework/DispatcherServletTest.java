package com.brick.framework;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.brick.framework.controller.Controller;
import com.brick.framework.controller.ControllerMethod;
import com.brick.framework.exception.InvalidParams;
import com.brick.framework.exception.InvalidServiceId;
import com.brick.framework.exception.InvalidValidatorId;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.exception.ParameterMismatch;
import com.brick.framework.response.ServiceValidationFailure;
import com.brick.framework.utility.BrickConstants;
import com.brick.openapi.elements.path.Path;
import com.brick.openapi.elements.path.http.methods.HttpMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatcherServletTest {

    private DispatcherServlet dispatcherServlet;

    // These will be initialized by the MockitoExtension
    @Mock private OpenApiProcessor mockOpenApiProcessor;
    @Mock private ControllerProcessor mockControllerProcessor;
    @Mock private AnnotationProcessor mockAnnotationProcessor;
    
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
    	MockitoAnnotations.openMocks(this);
    	
        // 1. Create the instance (Constructor still runs, but we will overwrite fields)
        dispatcherServlet = new DispatcherServlet("com.test.app");

        // 2. Inject the mocks manually to ensure 'this.response' is never null in the test scope
        setPrivateField(dispatcherServlet, "openApiProcessor", mockOpenApiProcessor);
        setPrivateField(dispatcherServlet, "controllerProcessor", mockControllerProcessor);
        setPrivateField(dispatcherServlet, "annotationProcessor", mockAnnotationProcessor);

        // 3. Prepare the writer
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        
        // IMPORTANT: Ensure the mock response returns our printWriter
        lenient().when(response.getWriter()).thenReturn(printWriter);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
    
    @Test
    void invalidContentType() throws ServletException, IOException {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
         
        // For XML
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/xml");
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(415);
    }
    
    @Test
    void invalidPath() throws ServletException, IOException {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json"); 
        
        // Execute
        dispatcherServlet.service(request, response);
        
        when(mockOpenApiProcessor.getPaths()).thenReturn(new ArrayList<Path>());

        // Verify
        verify(response).setStatus(404);
    }
    
    @Test
    void invalidMethod() throws ServletException, IOException {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json"); 
        
        Path mockPath = mock(Path.class);
        when(mockPath.matches(uri)).thenReturn(true);
        when(mockPath.getMethod("GET")).thenReturn(null);
        when(mockOpenApiProcessor.getPaths()).thenReturn(Arrays.asList(mockPath));
        
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(405);
    }
    
    @Test
    void badRequest() throws ServletException, IOException {
    	
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json");
        BufferedReader bufferedReader = mock(BufferedReader.class);
    	when( bufferedReader.readLine()).thenReturn(null);
        when( request.getReader() ).thenReturn(bufferedReader);
        when( request.getHeaderNames()).thenReturn(new Enumeration<String>() {
			
			@Override
			public String nextElement() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}
		});
        when( request.getCookies()).thenReturn(new Cookie[0]);
        
        HttpMethod method = mock(HttpMethod.class);
        when( method.validateRequest( any() ) ).thenReturn(false);
        
        Path mockPath = mock(Path.class);
        when(mockPath.matches(uri)).thenReturn(true);
        when(mockPath.getMethod("GET")).thenReturn(method);
        when(mockOpenApiProcessor.getPaths()).thenReturn(Arrays.asList(mockPath));
        
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(400);
    }
    
    @Test
    void controllerNotFound_controllerDefintionNotPresent() throws ServletException, IOException {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json");
        BufferedReader bufferedReader = mock(BufferedReader.class);
    	when( bufferedReader.readLine()).thenReturn(null);
        when( request.getReader() ).thenReturn(bufferedReader);
        when( request.getHeaderNames()).thenReturn(new Enumeration<String>() {
			
			@Override
			public String nextElement() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}
		});
        when( request.getCookies()).thenReturn(new Cookie[0]);
        
        HttpMethod method = mock(HttpMethod.class);
        when( method.validateRequest( any() ) ).thenReturn(true);
        
        Path mockPath = mock(Path.class);
        when(mockPath.matches(uri)).thenReturn(true);
        when(mockPath.getMethod("GET")).thenReturn(method);
        when(mockOpenApiProcessor.getPaths()).thenReturn(Arrays.asList(mockPath));
        
        when( mockControllerProcessor.getController(any())).thenReturn(null);
        
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(500);
    }
    
    @Test
    void controllerNotFound_methodDefintionNotPresent() throws ServletException, IOException {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json");
        BufferedReader bufferedReader = mock(BufferedReader.class);
    	when( bufferedReader.readLine()).thenReturn(null);
        when( request.getReader() ).thenReturn(bufferedReader);
        when( request.getHeaderNames()).thenReturn(new Enumeration<String>() {
			
			@Override
			public String nextElement() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}
		});
        when( request.getCookies()).thenReturn(new Cookie[0]);
        
        HttpMethod method = mock(HttpMethod.class);
        when( method.validateRequest( any() ) ).thenReturn(true);
        
        Path mockPath = mock(Path.class);
        when(mockPath.matches(uri)).thenReturn(true);
        when(mockPath.getMethod("GET")).thenReturn(method);
        when(mockOpenApiProcessor.getPaths()).thenReturn(Arrays.asList(mockPath));
        
        
        Controller controller = mock(Controller.class);
        when( controller.getControllerMethod("GET") ).thenReturn(null);
        
        when( mockControllerProcessor.getController(any())).thenReturn(controller);
        
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(500);
    }
    
    @Test
    void invalidResponse() throws ServletException, IOException, ServiceValidationFailure, IllegalAccessException, InvocationTargetException, InvalidValidatorId, ParameterMismatch, InvalidParams, InvalidServiceId, ParallelServiceResponseMappingFound {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json");
        BufferedReader bufferedReader = mock(BufferedReader.class);
    	when( bufferedReader.readLine()).thenReturn(null);
        when( request.getReader() ).thenReturn(bufferedReader);
        when( request.getHeaderNames()).thenReturn(new Enumeration<String>() {
			
			@Override
			public String nextElement() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean hasMoreElements() {
				// TODO Auto-generated method stub
				return false;
			}
		});
        when( request.getCookies()).thenReturn(new Cookie[0]);
        
        HttpMethod method = mock(HttpMethod.class);
        when( method.validateRequest( any() ) ).thenReturn(true);
        when( method.validateResponse(anyInt(), any() ) ).thenReturn(false);
        
        Path mockPath = mock(Path.class);
        when(mockPath.matches(uri)).thenReturn(true);
        when(mockPath.getMethod("GET")).thenReturn(method);
        when(mockOpenApiProcessor.getPaths()).thenReturn(Arrays.asList(mockPath));
        
        ControllerMethod controllerMethod = mock(ControllerMethod.class);
        
        Controller controller = mock(Controller.class);
        when( controller.getControllerMethod("GET") ).thenReturn(controllerMethod);
        
        when( mockControllerProcessor.getController(any())).thenReturn(controller);
        
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(500);
        verify(controllerMethod).executeServices(any());
    }
    
    @Test
    void validResponse() throws ServletException, IOException, ServiceValidationFailure, IllegalAccessException, InvocationTargetException, InvalidValidatorId, ParameterMismatch, InvalidParams, InvalidServiceId, ParallelServiceResponseMappingFound {
    	 // Setup Request
        String uri = "/api/test";
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader(BrickConstants.CONTENT_TYPE)).thenReturn("application/json");
        Enumeration<String> enumeration = Collections.enumeration(Arrays.asList("headerKey1","headerKey2"));
        when(request.getHeaderNames()).thenReturn(enumeration);
        when(request.getHeader("headerKey1")).thenReturn("headerValue1");
        when(request.getHeader("headerKey2")).thenReturn("headerValue2");
        BufferedReader bufferedReader = mock(BufferedReader.class);		
    	when( bufferedReader.readLine()).thenReturn(null);
        when( request.getReader() ).thenReturn(bufferedReader);
        when( request.getCookies()).thenReturn(new Cookie[0]);
        
        HttpMethod method = mock(HttpMethod.class);
        when( method.validateRequest( any() ) ).thenReturn(true);
        when( method.validateResponse(anyInt(), any()) ).thenReturn(true);
        
        Path mockPath = mock(Path.class);
        when(mockPath.matches(uri)).thenReturn(true);
        when(mockPath.getMethod("GET")).thenReturn(method);
        when(mockOpenApiProcessor.getPaths()).thenReturn(Arrays.asList(mockPath));
        
        ControllerMethod controllerMethod = mock(ControllerMethod.class);
        
        Controller controller = mock(Controller.class);
        when( controller.getControllerMethod("GET") ).thenReturn(controllerMethod);
        
        when( mockControllerProcessor.getController(any())).thenReturn(controller);
        
        // Execute
        dispatcherServlet.service(request, response);

        // Verify
        verify(response).setStatus(200);
        verify(controllerMethod).executeServices(any());
    }
}