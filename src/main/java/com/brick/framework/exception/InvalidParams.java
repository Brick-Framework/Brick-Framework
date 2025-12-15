package com.brick.framework.exception;

public class InvalidParams extends Exception {
	public InvalidParams(String paramName) {
		super("Invalid Params : "+paramName);
	}

}
