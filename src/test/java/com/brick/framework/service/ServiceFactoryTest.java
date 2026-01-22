package com.brick.framework.service;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import com.brick.framework.exception.ParallelServiceResponseMappingFound;
import com.brick.openapi.exception.InvalidValue;
import com.brick.utilities.BrickMap;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.exception.KeyNotFound;
import com.brick.utilities.file.FileReader;
import com.brick.utilities.file.YamlFileReader;

public class ServiceFactoryTest {
	@Test
	public void success() throws FileNotFoundException, InvalidData, KeyNotFound, InvalidValue, ParallelServiceResponseMappingFound {
		String filePath = "/controller/valid_controller.yaml";
    	FileReader fileReader = new YamlFileReader(filePath);
    	BrickMap controllerMap = fileReader.getMap();
    	
    	BrickMap singleServiceMap = new BrickMap(controllerMap.getBrickMap("/process").getBrickMap("post").getListOfMap("services").get(0));
    	BrickMap serviceGroupMap = new BrickMap(controllerMap.getBrickMap("/process").getBrickMap("post").getListOfMap("services").get(1));
    	
    	assertInstanceOf(SingleService.class, ServiceFactory.getService(singleServiceMap));
    	assertInstanceOf(ServiceGroup.class, ServiceFactory.getService(serviceGroupMap));
	}

}
