package com.flash49.emailOtp.cleverTapModel;

import java.util.ArrayList;

public class CleverTapRootRequest {
	private String event;
	ArrayList<Object> data = new ArrayList<Object>();

	// Getter Methods

	public String getEvent() {
		return event;
	}

	// Setter Methods

	public void setEvent(String event) {
		this.event = event;
	}
}