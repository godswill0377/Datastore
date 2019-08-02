package com.dataexo.zblog.vo.resources.instances;

import javax.ws.rs.core.MediaType;


import com.dataexo.zblog.exception.MailGunException;
import com.dataexo.zblog.util.Endpoints;
import com.dataexo.zblog.vo.auth.Email;
import com.dataexo.zblog.vo.auth.MailGunResponse;
import com.dataexo.zblog.vo.auth.MailgunClient;
import com.dataexo.zblog.vo.resources.InstanceResource;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class MessageInstance extends InstanceResource {

	MailGunResponse response;
	Email email;

	public MessageInstance(MailgunClient client, Email email) {
		super(client);
		this.response = new MailGunResponse();
		this.email = email;
	}

	public void sendSimple() throws MailGunException {
		MultivaluedMapImpl formData = new MultivaluedMapImpl();
		formData.add("from", this.email.getFrom());
		formData.add("to", this.email.getTo());
		formData.add("subject", this.email.getSubject());

		formData.add("html", this.email.getBody());
		ClientResponse response = this.getClient().getService().path(this.getResourceLocation()).type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
		this.parseResponse(response);
		this.checkStatusCode(this.response);
	}

	public MailGunResponse sendMIME(MailgunClient client, Email email) {
		return this.response;
	}

	@Override
	protected String getResourceLocation() {
		return Endpoints.MESSAGES; // To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected void parseResponse(ClientResponse response) {
		Gson gson = new Gson();
		this.response = gson.fromJson(response.getEntity(String.class), MailGunResponse.class);
		this.response.setStatusCode(response.getStatus());
	}

	public MailGunResponse getResponse() {
		return response;
	}

	public void setResponse(MailGunResponse mgr) {
		this.response = mgr;
	}
}
