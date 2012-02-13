package com.xlab.glodocs.api.security;

public enum Role {
	CREATE_DOCUMENT(0, "CR_D"), DROP_DOCUMENT(1, "DR_D"), EDIT_DOCUMENT(2,
			"ED_D"), BROWSE_DOCUMENT(3, "BR_D"), CREATE_COLLECTION(4, "CR_C"), DROP_COLLECTION(
			5, "DR_C"), EDIT_COLLECTION(6, "ED_C"), BROWSE_COLLECTION(7, "BR_C"), BROWSE_JOURNAL(
			8, "BR_J"), ROLLBACK_JOURNAL(9, "RB_J"), MANAGE_ROLES(10, "MG_R"), MANAGE_USERS(
			11, "MG_U"), MANAGE_ACTIONS(12, "MG_A");

	private int integerCode;
	private String code;

	Role(int integerCode, String code) {
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
