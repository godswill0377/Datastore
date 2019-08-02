package com.dataexo.zblog.vo.auth;

public class MailgunCredentials {

	private String domain;
	private String apiKey;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public MailgunCredentials() {
	}

	public MailgunCredentials(String domain, String apiKey) {
		super();
		this.domain = domain;
		this.apiKey = apiKey;
	}

}
