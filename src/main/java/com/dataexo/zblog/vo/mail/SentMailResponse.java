package com.dataexo.zblog.vo.mail;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SentMailResponse {

	// {
	// "message": "Queued. Thank you.",
	// "id": "<20111114174239.25659.5817@samples.mailgun.org>"
	// }

	@SerializedName("message")
	@Expose
	private String message;

	@SerializedName("id")
	@Expose
	private String id;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
