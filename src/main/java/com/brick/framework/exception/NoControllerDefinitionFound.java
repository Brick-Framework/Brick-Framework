package com.brick.framework.exception;

public class NoControllerDefinitionFound extends Exception {
	public NoControllerDefinitionFound(String path) {
		super("No Controller Defintion Found for Path : "+path);
	}
	
	public NoControllerDefinitionFound(String path,String method) {
		super("No Controller Defintion Found for Path : "+path+" with method : "+method);
	}

}
