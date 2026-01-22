package com.brick.framework.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import com.brick.framework.exception.ExecutionIdNotUnique;
import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class ControllerTest {
	@Test
	public void success() throws FileNotFoundException, InvalidData, KeyNotFound, InvalidValue, ExecutionIdNotUnique, ParallelServiceResponseMappingFound {
		String filePath = "/controller/valid_controller.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap controllerMap = fileReader.getMap();
    	
    	Controller controller = new Controller(controllerMap.getBrickMap("/process"));
    	assertNotNull(controller.getControllerMethod("POST"));
    	assertNull(controller.getControllerMethod("invalid"));
	}

}
