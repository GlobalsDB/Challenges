package com.xlab.glodocs.api.utils;

/**
 * General exception implementation
 * 
 * @author Lapitskiy Anton
 * 
 */
@SuppressWarnings("serial")
public class GlodocsException extends Exception {
	private String message;

	public GlodocsException(String string) {
		super();
		message = string;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
