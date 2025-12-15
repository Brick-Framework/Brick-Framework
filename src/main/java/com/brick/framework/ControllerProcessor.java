package com.brick.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.brick.framework.controller.Controller;
import com.brick.framework.exception.DuplicateOpenApiSpecificationFound;
import com.brick.framework.exception.ExecutionIdNotUnique;
import com.brick.framework.exception.MultipleControllerDefinitionFound;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.framework.utility.BrickConstants;
import com.brick.logger.Logger;
import com.brick.openapi.exception.InvalidOpenAPISpecification;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class ControllerProcessor {
	
	private static final String PATH_PREFIX = "/controller/";
	
	private Map<String,Controller> pathToControllerMap;
	
	public ControllerProcessor() throws FileNotFoundException, URISyntaxException, InvalidData, InvalidOpenAPISpecification, DuplicateOpenApiSpecificationFound, MultipleControllerDefinitionFound, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		this.pathToControllerMap = new HashMap<String, Controller>();
		
		this.parseControllerFiles();
	}
	
	/*
	 * Description: Starts Parsing Controller Files in root path specified After performing Checks
	 */
	private void parseControllerFiles() throws FileNotFoundException, URISyntaxException, InvalidData, InvalidOpenAPISpecification, DuplicateOpenApiSpecificationFound, MultipleControllerDefinitionFound, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		Logger.info("Starting to Parse Controller Files");
		
		File rootDirectory = new File(getClass().getResource(BrickConstants.CONTROLLER_ROOT_PATH).toURI());
		
		if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            FileNotFoundException exception = new FileNotFoundException("Directory Not Found : "+BrickConstants.OPENAPI_ROOT_PATH);
            Logger.logException(exception);
            throw exception;
        }
		
		processControllerFiles(rootDirectory);
		
		Logger.info("Controller Files Parsing Completed");
	}
	
	/*
	 * Description: Scans the Directory Recursively and Adds Controller Object From OpenApi to endpointPathMap
	 */
	private void processControllerFiles(File directory) throws InvalidData, FileNotFoundException, InvalidOpenAPISpecification, DuplicateOpenApiSpecificationFound, MultipleControllerDefinitionFound, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		File[] files = directory.listFiles();
		if( null == files ) {
			return;
		}
		
		
		for( File file : files ) {
			if( file.isDirectory() ) {
				processControllerFiles(file);
			}else {
				if( BrickConstants.VALID_CONTROLLER_EXTENSION.equals( FileReader.getFileExtension(file.getName()) ) ) {
					FileReader fileReader = new YamlFileReader(PATH_PREFIX+file.getName());
					BrickMap map = fileReader.getMap();
					
					for( Map.Entry<String, Object> entry : map ) {
						if( this.pathToControllerMap.containsKey(entry.getKey()) ) {
							MultipleControllerDefinitionFound multipleControllerDefinitionFound = new MultipleControllerDefinitionFound(entry.getKey());
							Logger.logException(multipleControllerDefinitionFound);
							throw multipleControllerDefinitionFound;
						}
						
						Logger.info("Trying to Create Controller Object From : "+file.getName()+" with path : "+entry.getKey());
						this.pathToControllerMap.put(entry.getKey(), new Controller( new BrickMap(entry.getValue()) ) );
					}
				}
			}
		}
	}
	
	/*
	 * Description : Get Controller Based on path
	 */
	public Controller getController(String path) {
		return this.pathToControllerMap.get(path);
	}

}
