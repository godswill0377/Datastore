package com.dataexo.zblog.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.dataexo.zblog.controller.ServiceControlller;
import com.dataexo.zblog.exception.MailGunException;
import com.dataexo.zblog.service.Mailgun;
import com.dataexo.zblog.vo.auth.MailgunClient;
import com.dataexo.zblog.vo.auth.MailgunCredentials;
import com.dataexo.zblog.vo.mail.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

public class MailgunImpl implements Mailgun {

	private static final Logger logger = Logger.getLogger(MailgunImpl.class);

	MailgunClient client;

	public MailgunImpl(MailgunCredentials mailgunCredentials) {
		client = new MailgunClient(mailgunCredentials.getDomain(), mailgunCredentials.getApiKey());
	}

	public SentMailResponse sendEmail(SendMailRequest sendMailRequest, MailgunClient mailgunClient) throws MailGunException {
		logger.debug("Inside sendEmail with to: " + sendMailRequest.getTo() + " and from: " + sendMailRequest.getFrom());
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("from", sendMailRequest.getFrom());
		form.field("to", sendMailRequest.getTo());

		if (StringUtils.isNotEmpty(sendMailRequest.getCc())) {
			form.field("cc", sendMailRequest.getCc());
		}

		if (StringUtils.isNotEmpty(sendMailRequest.getBcc())) {
			form.field("bcc", sendMailRequest.getBcc());
		}

		form.field("subject", ObjectUtils.defaultIfNull(sendMailRequest.getSubject(), ""));

		if (StringUtils.isNotEmpty(sendMailRequest.getText())) {
			form.field("text", sendMailRequest.getText());
		} else {
			form.field("text", " ");
		}

		if (StringUtils.isNotEmpty(sendMailRequest.getHtml())) {
			form.field("html", sendMailRequest.getHtml());
		} else {
			form.field("html", " ");
		}

		if (StringUtils.isNotEmpty(sendMailRequest.getReplyTo())) {
			form.field("Reply-To", sendMailRequest.getReplyTo());
		}

		Map<String, String> headers = sendMailRequest.getHeaders();
		if (headers != null) {
			String references = null;
			if (headers.containsKey("References")) {
				references = headers.get("References");
			}
			for (String headerKey : headers.keySet()) {
				if ("Message-Id".equals(headerKey)) {
					String messageId = headers.get(headerKey);
					if (StringUtils.isNotEmpty(messageId)) {
						form.field("h:In-Reply-To", messageId);
						if (StringUtils.isBlank(references)) {
							form.field("h:References", messageId);
						} else {
							form.field("h:References", references);
						}
					}
				}
			}
		}

		List<OutgoingAttachment> attachments = sendMailRequest.getAttachment();
		if (attachments != null && attachments.size() > 0) {
			for (OutgoingAttachment attachment : attachments) {
				File uploadFile = attachment.getAttachmentFile();
				if (uploadFile.exists()) {
					form.bodyPart(new FileDataBodyPart("attachment", uploadFile, MediaType.valueOf(attachment.getContentType())));
				} else {
					logger.warn("Could not find attachment file to send for attachment file path " + attachment.getFilename());
				}

			}
		}
		ClientResponse response = mailgunClient.getClient().resource(mailgunClient.getBaseUri() + "/messages").type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, form);
		String responseString;
		try {
			responseString = IOUtils.toString(response.getEntityInputStream(), "UTF-8");
		} catch (IOException e) {
			logger.error( "Could not parse response from the mail", e);
			throw new MailGunException("Could not parse response from the mail", e);

		}
		logger.debug("the response from mailgun is " + response.getStatus() + " and response String is " + responseString);
		SentMailResponse sentMailResponse = new Gson().fromJson(responseString, SentMailResponse.class);
		cleanTempAttachment(attachments);

		return sentMailResponse;
	}

	private void cleanTempAttachment(List<OutgoingAttachment> attachments) {

		String tempAttachmentFile = null;
		if (attachments != null) {
			for (OutgoingAttachment outgoingAttachment : attachments) {
				if (outgoingAttachment.isDeleteAfterSent()) {
					tempAttachmentFile = System.getProperty("java.io.tmpdir") + outgoingAttachment.getAttachmentId();
					FileUtils.deleteQuietly(new File(tempAttachmentFile));
				}
			}
		}
	}

	public InputStream getAttachment(IncomingAttachment incomingAttachmentObject) throws MailGunException {
		logger.info("Inside getAttachment() incomingAttachment url" + incomingAttachmentObject.getUrl());
		WebResource webResource = client.getClient().resource(incomingAttachmentObject.getUrl());
		ClientResponse response = webResource.type(new MediaType("application", incomingAttachmentObject.getContentType())).get(ClientResponse.class);

		logger.debug("response while getting attachment " + response.getStatus());
		return response.getEntityInputStream();
	}

	public void putRoute(Route route) throws MailGunException {
		logger.info("Inside putRoute() with route expression" + route);
		WebResource webResource = client.getClient().resource(client.getRouteBaseUri());
		MultivaluedMapImpl formData = new MultivaluedMapImpl();
		formData.add("priority", route.getPriority());
		formData.add("description", route.getDescription());
		formData.add("expression", route.getExpression());
		for (String action : route.getActions()) {
			formData.add("action", action);
		}
		ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		logger.debug("JSON Response: " + responseJsonObject);

	}

	public List<Route> getRoutes(int limit, int skip) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getRouteBaseUri());
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams.add("skip", skip);
		queryParams.add("limit", limit);
		ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		JSONArray items = responseJsonObject.getJSONArray("items");
		List<Route> routeList = new ArrayList<Route>();
		for (int i = 0; i < items.size(); i++) {
			Route route = getRouteFromString(items.getJSONObject(i).toString());
			routeList.add(route);
		}
		return routeList;
	}

	public Route getRoute(Long id) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getRouteBaseUri() + File.separator + id);
		ClientResponse response = webResource.get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from getRoute mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		Route route = getRouteFromString(responseJsonObject.getJSONObject("route").toString());
		return route;
	}

	public void deleteRoute(Long id) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getRouteBaseUri() + File.separator + id);
		ClientResponse response = webResource.delete(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from deleteRoute mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		logger.debug("JSON Response: " + responseJsonObject);
	}

	public void putCampaign(Campaign campaign) throws MailGunException {
		// TODO Auto-generated method stub

	}

	public List<Campaign> getCampaigns(int limit, int skip) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getCampaignBaseUri());
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams.add("skip", skip);
		queryParams.add("limit", limit);
		ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from mailgun getCampaigns is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		JSONArray items = responseJsonObject.getJSONArray("items");
		List<Campaign> campaignList = new ArrayList<Campaign>();
		for (int i = 0; i < items.size(); i++) {
			Campaign campaign = getCampaignFromString(items.getJSONObject(i).toString());
			campaignList.add(campaign);
		}
		return campaignList;
	}

	public Campaign getCampaign(Long id) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getCampaignBaseUri() + File.separator + id);
		ClientResponse response = webResource.get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from getCampaign mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		Campaign campaign = getCampaignFromString(responseJsonObject.getJSONObject("campaign").toString());
		return campaign;
	}

	public void deleteCampaign(Long id) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getCampaignBaseUri() + File.separator + id);
		ClientResponse response = webResource.delete(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from deleteCampaign mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		logger.debug("JSON Response: " + responseJsonObject);
	}



	public List<Domain> getDomains(int limit, int skip) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getDomainBaseUri());
		MultivaluedMapImpl queryParams = new MultivaluedMapImpl();
		queryParams.add("skip", skip);
		queryParams.add("limit", limit);
		ClientResponse response = webResource.queryParams(queryParams).get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from mailgun getDomains is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		JSONArray items = responseJsonObject.getJSONArray("items");
		List<Domain> domainList = new ArrayList<Domain>();
		for (int i = 0; i < items.size(); i++) {
			Domain domain = getDomainFromString(items.getJSONObject(i).toString());
			domainList.add(domain);
		}
		return domainList;
	}

	public Domain getDomain(String domainid) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getDomainBaseUri() + File.separator + domainid);
		ClientResponse response = webResource.get(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from getDomain mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		Domain domain = getDomainFromString(responseJsonObject.getJSONObject("domain").toString());
		return domain;
	}

	public void deleteDomain(String domainid) throws MailGunException {
		WebResource webResource = client.getClient().resource(client.getDomainBaseUri() + File.separator + domainid);
		ClientResponse response = webResource.delete(ClientResponse.class);
		String responseString = response.getEntity(String.class);
		logger.debug("the response from deleteCampaign mailgun is " + response.getStatus() + " and response String is " + responseString);
		JSONObject responseJsonObject = (JSONObject) JSONSerializer.toJSON(responseString);
		logger.debug("JSON Response: " + responseJsonObject);
	}

	private Domain getDomainFromString(String item) {
		Domain domain = new Gson().fromJson(item, Domain.class);
		return domain;
	}

	private Campaign getCampaignFromString(String item) {
		Campaign campaign = new Gson().fromJson(item, Campaign.class);
		return campaign;
	}

	private Route getRouteFromString(String item) {
		Route route = new Gson().fromJson(item, Route.class);
		return route;
	}

	public static void main(String[] args) {
		System.out.println(StringUtils.isNotEmpty(""));
	}
}
