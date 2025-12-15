package com.brick.framework.exception;

public class MultipleControllerDefinitionFound extends Exception {
	public MultipleControllerDefinitionFound(String path) {
		super("Multiple Controller Defintion Found for Path : "+path);
	}

}
