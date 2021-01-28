package com.abrarandlabiba.cloud.data;

public class SMSRequest {
	private String phoneNumber;
	private String message;
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String number) {
		this.phoneNumber = number;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
