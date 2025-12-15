package com.brick.framework.exception;

public class MultiplePublicConstructorFound extends Exception{
	public MultiplePublicConstructorFound(String componentName) {
		super("Multiple Public constructor found for component : "+componentName);
	}

}
