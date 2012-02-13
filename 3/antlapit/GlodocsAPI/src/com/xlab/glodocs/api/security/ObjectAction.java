package com.xlab.glodocs.api.security;

public enum ObjectAction {
	NO_ACCESS(0, "NA"), READ(1, "RE"), WRITE(2, "WR"), FULL(3, "RW");

	private int integerCode;
	private String code;

	private ObjectAction(int integerCode, String code) {
		this.integerCode = integerCode;
		this.code = code;
	}

	public int getIntegerCode() {
		return integerCode;
	}

	public String getCode() {
		return code;
	}
}
