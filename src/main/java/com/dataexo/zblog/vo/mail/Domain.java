package com.dataexo.zblog.vo.mail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Domain {

	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("created_at")
	@Expose
	private String createdAt;

	@SerializedName("smtp_login")
	@Expose
	private String smtpLogin;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("smtp_password")
	@Expose
	private String smtpPassword;

	@SerializedName("wildcard")
	@Expose
	private boolean wildcard;

	@SerializedName("spam_action")
	@Expose
	private String spamAction;

	@SerializedName("state")
	@Expose
	private String state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getSmtpLogin() {
		return smtpLogin;
	}

	public void setSmtpLogin(String smtpLogin) {
		this.smtpLogin = smtpLogin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public boolean isWildcard() {
		return wildcard;
	}

	public void setWildcard(boolean wildcard) {
		this.wildcard = wildcard;
	}

	public String getSpamAction() {
		return spamAction;
	}

	public void setSpamAction(String spamAction) {
		this.spamAction = spamAction;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
