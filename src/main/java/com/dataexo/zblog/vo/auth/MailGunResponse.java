package com.dataexo.zblog.vo.auth;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class MailGunResponse {

	@SerializedName("message")
	String message;

	@SerializedName("id")
	String id;

	int statusCode;

	public MailGunResponse() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "MailGunResponse{" + "message='" + message + '\'' + ", id='" + id + '\'' + '}';
	}

	public static MailGunResponse getSuccessResponse() {
		MailGunResponse mailGunResponse = new MailGunResponse();
		mailGunResponse.setId(UUID.randomUUID().toString());
		mailGunResponse.setMessage("success");
		mailGunResponse.setStatusCode(200);
		return mailGunResponse;
	}

	public static MailGunResponse getErrorResponse(String message) {
		MailGunResponse mailGunResponse = new MailGunResponse();
		mailGunResponse.setId(UUID.randomUUID().toString());
		mailGunResponse.setMessage(message);
		mailGunResponse.setStatusCode(500);
		return mailGunResponse;
	}
}
