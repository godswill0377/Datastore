package com.dataexo.zblog.service;

import com.dataexo.zblog.exception.MailGunException;
import com.dataexo.zblog.vo.auth.MailgunClient;
import com.dataexo.zblog.vo.mail.*;

import java.io.InputStream;
import java.util.List;


/**
 * Mailgun java interface as of now only contains operations that we need
 * 
 * @author Utkarsh
 * 
 */
public interface Mailgun {

	SentMailResponse sendEmail(SendMailRequest sendMailRequest, MailgunClient mailgunClient) throws MailGunException;

	// AttachmentResponse initiateAttachments(AttachmentDownloadRequest attachmentDownloadRequest) throws MailGunException;
	// AttachmentResponse downloadAttachmentsPart(AttachmentDownloadRequest attachmentDownloadRequest) throws MailGunException;
	// AttachmentResponse abortAttachmentDownload(AttachmentDownloadRequest attachmentDownloadRequest) throws MailGunException;

	InputStream getAttachment(IncomingAttachment incomingAttachmentObject) throws MailGunException;

	void putRoute(Route route) throws MailGunException;

	List<Route> getRoutes(int limit, int skip) throws MailGunException;

	Route getRoute(Long id) throws MailGunException;

	void deleteRoute(Long id) throws MailGunException;

	void putCampaign(Campaign campaign) throws MailGunException;

	List<Campaign> getCampaigns(int limit, int skip) throws MailGunException;

	Campaign getCampaign(Long id) throws MailGunException;

	void deleteCampaign(Long id) throws MailGunException;


	List<Domain> getDomains(int limit, int skip) throws MailGunException;

	Domain getDomain(String domainid) throws MailGunException;

	void deleteDomain(String domainid) throws MailGunException;

}
