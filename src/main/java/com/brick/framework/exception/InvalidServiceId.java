package com.brick.framework.exception;

public class InvalidServiceId extends Exception{
	public InvalidServiceId(String serviceId) {
		super("Invalid Service Id : "+ serviceId);
	}
}
