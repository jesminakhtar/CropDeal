package com.cropdeal.inventoryservice.exception;

public class InsufficientQuantityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InsufficientQuantityException(String msg) {
		super(msg);
	}
}
