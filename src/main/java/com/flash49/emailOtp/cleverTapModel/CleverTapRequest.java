package com.flash49.emailOtp.cleverTapModel;

public class CleverTapRequest {
	private float ts;
	private String meta;
	private String description;
	private String code;

	// Getter Methods

	public float getTs() {
		return ts;
	}

	public String getMeta() {
		return meta;
	}

	public String getDescription() {
		return description;
	}

	public String getCode() {
		return code;
	}

	// Setter Methods

	public void setTs(float ts) {
		this.ts = ts;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
