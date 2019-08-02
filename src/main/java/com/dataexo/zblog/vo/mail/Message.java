package com.dataexo.zblog.vo.mail;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Message {

	@SerializedName("headers")
	public Headers headers;

	@SerializedName("attachments")
	public List<IncomingAttachment> attachments;

	@SerializedName("recipients")
	public List<String> recipients;

	@SerializedName("size")
	public Integer size;

	public Headers getHeaders() {
		return this.headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public List<IncomingAttachment> getAttachments() {
		return this.attachments;
	}

	public void setAttachments(List<IncomingAttachment> attachments) {
		this.attachments = attachments;
	}

	public List<String> getRecipients() {
		return this.recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public Integer getSize() {
		return this.size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}