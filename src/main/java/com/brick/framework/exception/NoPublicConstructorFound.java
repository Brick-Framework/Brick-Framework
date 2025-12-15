package com.brick.framework.exception;

public class NoPublicConstructorFound extends Exception{
	public NoPublicConstructorFound(String componentName) {
		super("Could not find any public constructor for component : "+componentName);
	}
}
