package com.brick.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.brick.framework.exception.DuplicateOpenApiSpecificationFound;
import com.brick.framework.utility.BrickConstants;
import com.brick.logger.Logger;
import com.brick.openapi.OpenAPI;
import com.brick.openapi.elements.path.Path;
import com.brick.openapi.exception.InvalidOpenAPISpecification;
import com.brick.openapi.reader.OpenAPIFileReader;
import com.brick.openapi.reader.OpenApiFileReaderFactory;
import com.brick.utilities.exception.InvalidData;
import com.brick.utilities.file.FileReader;

public class OpenApiProcessor {
	
	private List<Path> paths;
	
	public OpenApiProcessor() throws FileNotFoundException, InvalidData, InvalidOpenAPISpecification, URISyntaxException, DuplicateOpenApiSpecificationFound {
		this.paths = new ArrayList<Path>();
		
		this.parseOpenApi();
	}
	
	/*
	 * Description: Starts Parsing OpenAPI Files in root path specified After performing Checks
	 */
	private void parseOpenApi() throws FileNotFoundException, InvalidData, InvalidOpenAPISpecification, URISyntaxException, DuplicateOpenApiSpecificationFound {
		Logger.info("Starting to Parse Open Api Files");
		
		File rootDirectory = new File(getClass().getResource(BrickConstants.OPENAPI_ROOT_PATH).toURI());
		
		if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            FileNotFoundException exception = new FileNotFoundException("Directory Not Found : "+BrickConstants.OPENAPI_ROOT_PATH);
            Logger.logException(exception);
            throw exception;
        }
		
		processOpenApiFiles(rootDirectory);
		
		Logger.info("Open Api Files Parsing Completed");
	}
	
	/*
	 * Description: Scans the Directory Recursively and Adds Path Object From OpenApi to endpointPathMap
	 */
	private void processOpenApiFiles(File directory) throws InvalidData, FileNotFoundException, InvalidOpenAPISpecification, DuplicateOpenApiSpecificationFound {
		File[] files = directory.listFiles();
		if( null == files ) {
			return;
		}
		
		Set<String> seenUri = new HashSet<String>();
		
		for( File file : files ) {
			if( file.isDirectory() ) {
				processOpenApiFiles(file);
			}else {
				if( BrickConstants.VALID_OPENAPI_EXTENSION.contains( FileReader.getFileExtension(file.getName())) ) {
					OpenAPIFileReader fileReader = OpenApiFileReaderFactory.getReader(file);
					OpenAPI openApi = fileReader.getOpenAPI();
					
					List<Path> paths = openApi.getPaths();
					for( Path path: paths ) {
						
						if( seenUri.contains(path.getUri()) ) {
							throw new DuplicateOpenApiSpecificationFound(path.getUri());
						}
						this.paths.add(path);
						seenUri.add(path.getUri());
					}
				}
			}
		}
	}

	public List<Path> getPaths() {
		return paths;
	}
	
	
}
