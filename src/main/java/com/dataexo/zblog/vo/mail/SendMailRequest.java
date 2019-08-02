package com.dataexo.zblog.vo.mail;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendMailRequest {

	// from Email address for From header
	// to Email address of the recipient(s). Example: "Bob <bob@host.com>". You can use commas to separate multiple recipients.
	// cc Same as To but for Cc
	// bcc Same as To but for Bcc
	// subject Message subject
	// text Body of the message. (text version)
	// html Body of the message. (HTML version)
	// attachment File attachment. You can post multiple attachment values. Important: You must use multipart/form-data encoding when sending attachments.
	// inline Attachment with inline disposition. Can be used to send inline images (see example). You can post multiple inline values.
	// o:tag Tag string. See Tagging for more information.
	// o:campaign Id of the campaign the message belongs to. See Campaign Analytics for details.
	// o:dkim Enables/disables DKIM signatures on per-message basis. Pass yes or no
	// o:deliverytime Desired time of delivery. See Date Format. Note: Messages can be scheduled for a maximum of 3 days in the future.
	// o:testmode Enables sending in test mode. Pass yes if needed. See Sending in Test Mode
	// o:tracking Toggles tracking on a per-message basis, see Tracking Messages for details. Pass yes or no.
	// o:tracking-clicks Toggles clicks tracking on a per-message basis. Has higher priority than domain-level setting. Pass yes, no or htmlonly.
	// o:tracking-opens Toggles opens tracking on a per-message basis. Has higher priority than domain-level setting. Pass yes or no.
	// h:X-My-Header h: prefix followed by an arbitrary value allows to append a custom MIME header to the message (X-My-Header in this case). For example, h:Reply-To to specify Reply-To address.
	// v:my-var v: prefix followed by an arbitrary name allows to attach a custom JSON data to the message. See Attaching Data to Messages for more information.

	@SerializedName("from")
	@Expose
	private String from;
	
	@SerializedName("to")
	@Expose
	private String to;
	
	@SerializedName("cc")
	@Expose
	private String cc;
	
	@SerializedName("bcc")
	@Expose
	private String bcc;
	
	@SerializedName("subject")
	@Expose
	private String subject;
	
	@SerializedName("text")
	@Expose
	private String text;
	
	@SerializedName("html")
	@Expose
	private String html;

	@SerializedName("attachment")
	@Expose
	private List<OutgoingAttachment> attachment;

	@SerializedName("In-Reply-To")
	@Expose
	private String replyTo;

	private Map<String, String> headers;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public List<OutgoingAttachment> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<OutgoingAttachment> attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	// @Override
	// public int hashCode() {
	// return new
	// HashCodeBuilder().append(Received).append(strippedSignature).append(From).append(XEnvelopeFrom).append(recipients).append(XGoogleDkimSignature).append(To).append(messageHeaders).append(strippedText).append(contentIdMap).append(subject).append(strippedHtml).append(from).append(sender).append(XMailgunIncoming).append(XReceived).append(XGmMessageState).append(attachments).append(bodyHtml).append(References).append(MimeVersion).append(InReplyTo).append(Date).append(MessageId).append(ContentType).append(bodyPlain).append(Subject).toHashCode();
	// }

	// @Override
	// public boolean equals(Object other) {
	// if (other == this) {
	// return true;
	// }
	// if ((other instanceof SendMailRequest) == false) {
	// return false;
	// }
	// SendMailRequest rhs = ((SendMailRequest) other);
	// return new EqualsBuilder().append(Received, rhs.Received).append(strippedSignature, rhs.strippedSignature).append(From, rhs.From).append(XEnvelopeFrom, rhs.XEnvelopeFrom).append(recipients,
	// rhs.recipients).append(XGoogleDkimSignature, rhs.XGoogleDkimSignature).append(To, rhs.To).append(messageHeaders, rhs.messageHeaders).append(strippedText, rhs.strippedText).append(contentIdMap,
	// rhs.contentIdMap).append(subject, rhs.subject).append(strippedHtml, rhs.strippedHtml).append(from, rhs.from).append(sender, rhs.sender).append(XMailgunIncoming,
	// rhs.XMailgunIncoming).append(XReceived, rhs.XReceived).append(XGmMessageState, rhs.XGmMessageState).append(attachments, rhs.attachments).append(bodyHtml, rhs.bodyHtml).append(References,
	// rhs.References).append(MimeVersion, rhs.MimeVersion).append(InReplyTo, rhs.InReplyTo).append(Date, rhs.Date).append(MessageId, rhs.MessageId).append(ContentType,
	// rhs.ContentType).append(bodyPlain, rhs.bodyPlain).append(Subject, rhs.Subject).isEquals();
	// }

}
