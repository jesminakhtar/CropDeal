package com.cropdeal.orderservice.exception;

public class InvalidOrderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidOrderException(String msg) {
		super(msg);
	}
}
