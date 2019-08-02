package com.dataexo.zblog.vo.mail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReceiveMailRequest {

	@Expose
	private String Received;

	@SerializedName("stripped-signature")
	@Expose
	private String strippedSignature;

	@Expose
	private String From;

	@SerializedName("X-Envelope-From")
	@Expose
	private String XEnvelopeFrom;

	@Expose
	private String recipient;

	@SerializedName("X-Google-Dkim-Signature")
	@Expose
	private String XGoogleDkimSignature;

	@Expose
	private String To;

	@Expose
	private String Cc;

	@Expose
	private String Bcc;

	@SerializedName("message-headers")
	@Expose
	private List<List<String>> messageHeaders;

	@SerializedName("stripped-text")
	@Expose
	private String strippedText;

	@SerializedName("content-id-map")
	@Expose
	private ContentIdMap contentIdMap;

	@Expose
	private String subject;

	@SerializedName("stripped-html")
	@Expose
	private String strippedHtml;

	@Expose
	private String from;

	@Expose
	private String sender;

	@SerializedName("X-Mailgun-Incoming")
	@Expose
	private String XMailgunIncoming;

	@SerializedName("X-Received")
	@Expose
	private String XReceived;

	@SerializedName("X-Gm-Message-State")
	@Expose
	private String XGmMessageState;

	@Expose
	private List<IncomingAttachment> attachments = new ArrayList<IncomingAttachment>();

	@SerializedName("body-html")
	@Expose
	private String bodyHtml;

	@Expose
	private String References;

	@SerializedName("Mime-Version")
	@Expose
	private String MimeVersion;

	@SerializedName("In-Reply-To")
	@Expose
	private String InReplyTo;

	@Expose
	private String Date;

	@SerializedName("Message-Id")
	@Expose
	private String MessageId;

	@SerializedName("Content-Type")
	@Expose
	private String ContentType;

	@SerializedName("body-plain")
	@Expose
	private String bodyPlain;

	@Expose
	private String Subject;

	/**
	 * 
	 * @return
	 *         The Received
	 */
	public String getReceived() {
		return Received;
	}

	/**
	 * 
	 * @param Received
	 *            The Received
	 */
	public void setReceived(String Received) {
		this.Received = Received;
	}

	public ReceiveMailRequest withReceived(String Received) {
		this.Received = Received;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The strippedSignature
	 */
	public String getStrippedSignature() {
		return strippedSignature;
	}

	/**
	 * 
	 * @param strippedSignature
	 *            The stripped-signature
	 */
	public void setStrippedSignature(String strippedSignature) {
		this.strippedSignature = strippedSignature;
	}

	public ReceiveMailRequest withStrippedSignature(String strippedSignature) {
		this.strippedSignature = strippedSignature;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The From
	 */

	/**
	 * 
	 * @return
	 *         The XEnvelopeFrom
	 */
	public String getXEnvelopeFrom() {
		return XEnvelopeFrom;
	}

	/**
	 * 
	 * @param XEnvelopeFrom
	 *            The X-Envelope-From
	 */
	public void setXEnvelopeFrom(String XEnvelopeFrom) {
		this.XEnvelopeFrom = XEnvelopeFrom;
	}

	public ReceiveMailRequest withXEnvelopeFrom(String XEnvelopeFrom) {
		this.XEnvelopeFrom = XEnvelopeFrom;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The recipients
	 */
	public String getRecipients() {
		return recipient;
	}

	/**
	 * 
	 * @param recipients
	 *            The recipients
	 */
	public void setRecipients(String recipients) {
		this.recipient = recipients;
	}

	public ReceiveMailRequest withRecipients(String recipients) {
		this.recipient = recipients;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The XGoogleDkimSignature
	 */
	public String getXGoogleDkimSignature() {
		return XGoogleDkimSignature;
	}

	/**
	 * 
	 * @param XGoogleDkimSignature
	 *            The X-Google-Dkim-Signature
	 */
	public void setXGoogleDkimSignature(String XGoogleDkimSignature) {
		this.XGoogleDkimSignature = XGoogleDkimSignature;
	}

	public ReceiveMailRequest withXGoogleDkimSignature(String XGoogleDkimSignature) {
		this.XGoogleDkimSignature = XGoogleDkimSignature;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The To
	 */
	public String getTo() {
		return To;
	}

	/**
	 * 
	 * @param To
	 *            The To
	 */
	public void setTo(String To) {
		this.To = To;
	}

	public ReceiveMailRequest withTo(String To) {
		this.To = To;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The messageHeaders
	 */
	public List<List<String>> getMessageHeaders() {
		return messageHeaders;
	}

	/**
	 * 
	 * @param messageHeaders
	 *            The message-headers
	 */
	public void setMessageHeaders(List<List<String>> messageHeaders) {
		this.messageHeaders = messageHeaders;
	}

	public ReceiveMailRequest withMessageHeaders(List<List<String>> messageHeaders) {
		this.messageHeaders = messageHeaders;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The strippedText
	 */
	public String getStrippedText() {
		return strippedText;
	}

	/**
	 * 
	 * @param strippedText
	 *            The stripped-text
	 */
	public void setStrippedText(String strippedText) {
		this.strippedText = strippedText;
	}

	public ReceiveMailRequest withStrippedText(String strippedText) {
		this.strippedText = strippedText;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The contentIdMap
	 */
	public ContentIdMap getContentIdMap() {
		return contentIdMap;
	}

	/**
	 * 
	 * @param contentIdMap
	 *            The content-id-map
	 */
	public void setContentIdMap(ContentIdMap contentIdMap) {
		this.contentIdMap = contentIdMap;
	}

	public ReceiveMailRequest withContentIdMap(ContentIdMap contentIdMap) {
		this.contentIdMap = contentIdMap;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * 
	 * @param subject
	 *            The subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ReceiveMailRequest withSubject(String subject) {
		this.subject = subject;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The strippedHtml
	 */
	public String getStrippedHtml() {
		return strippedHtml;
	}

	/**
	 * 
	 * @param strippedHtml
	 *            The stripped-html
	 */
	public void setStrippedHtml(String strippedHtml) {
		this.strippedHtml = strippedHtml;
	}

	public ReceiveMailRequest withStrippedHtml(String strippedHtml) {
		this.strippedHtml = strippedHtml;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * 
	 * @param from
	 *            The from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	public ReceiveMailRequest withFrom(String from) {
		this.from = from;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * 
	 * @param sender
	 *            The sender
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	public ReceiveMailRequest withSender(String sender) {
		this.sender = sender;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The XMailgunIncoming
	 */
	public String getXMailgunIncoming() {
		return XMailgunIncoming;
	}

	/**
	 * 
	 * @param XMailgunIncoming
	 *            The X-Mailgun-Incoming
	 */
	public void setXMailgunIncoming(String XMailgunIncoming) {
		this.XMailgunIncoming = XMailgunIncoming;
	}

	public ReceiveMailRequest withXMailgunIncoming(String XMailgunIncoming) {
		this.XMailgunIncoming = XMailgunIncoming;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The XReceived
	 */
	public String getXReceived() {
		return XReceived;
	}

	/**
	 * 
	 * @param XReceived
	 *            The X-Received
	 */
	public void setXReceived(String XReceived) {
		this.XReceived = XReceived;
	}

	public ReceiveMailRequest withXReceived(String XReceived) {
		this.XReceived = XReceived;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The XGmMessageState
	 */
	public String getXGmMessageState() {
		return XGmMessageState;
	}

	/**
	 * 
	 * @param XGmMessageState
	 *            The X-Gm-Message-State
	 */
	public void setXGmMessageState(String XGmMessageState) {
		this.XGmMessageState = XGmMessageState;
	}

	public ReceiveMailRequest withXGmMessageState(String XGmMessageState) {
		this.XGmMessageState = XGmMessageState;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The attachments
	 */
	public List<IncomingAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * 
	 * @param attachments
	 *            The attachments
	 */
	public void setAttachments(List<IncomingAttachment> attachments) {
		this.attachments = attachments;
	}

	public ReceiveMailRequest withAttachments(List<IncomingAttachment> attachments) {
		this.attachments = attachments;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The bodyHtml
	 */
	public String getBodyHtml() {
		return bodyHtml;
	}

	/**
	 * 
	 * @param bodyHtml
	 *            The body-html
	 */
	public void setBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
	}

	public ReceiveMailRequest withBodyHtml(String bodyHtml) {
		this.bodyHtml = bodyHtml;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The References
	 */
	public String getReferences() {
		return References;
	}

	/**
	 * 
	 * @param References
	 *            The References
	 */
	public void setReferences(String References) {
		this.References = References;
	}

	public ReceiveMailRequest withReferences(String References) {
		this.References = References;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The MimeVersion
	 */
	public String getMimeVersion() {
		return MimeVersion;
	}

	/**
	 * 
	 * @param MimeVersion
	 *            The Mime-Version
	 */
	public void setMimeVersion(String MimeVersion) {
		this.MimeVersion = MimeVersion;
	}

	public ReceiveMailRequest withMimeVersion(String MimeVersion) {
		this.MimeVersion = MimeVersion;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The InReplyTo
	 */
	public String getInReplyTo() {
		return InReplyTo;
	}

	/**
	 * 
	 * @param InReplyTo
	 *            The In-Reply-To
	 */
	public void setInReplyTo(String InReplyTo) {
		this.InReplyTo = InReplyTo;
	}

	public ReceiveMailRequest withInReplyTo(String InReplyTo) {
		this.InReplyTo = InReplyTo;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The Date
	 */
	public String getDate() {
		return Date;
	}

	/**
	 * 
	 * @param Date
	 *            The Date
	 */
	public void setDate(String Date) {
		this.Date = Date;
	}

	public ReceiveMailRequest withDate(String Date) {
		this.Date = Date;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The MessageId
	 */
	public String getMessageId() {
		return MessageId;
	}

	/**
	 * 
	 * @param MessageId
	 *            The Message-Id
	 */
	public void setMessageId(String MessageId) {
		this.MessageId = MessageId;
	}

	public ReceiveMailRequest withMessageId(String MessageId) {
		this.MessageId = MessageId;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The ContentType
	 */
	public String getContentType() {
		return ContentType;
	}

	/**
	 * 
	 * @param ContentType
	 *            The Content-Type
	 */
	public void setContentType(String ContentType) {
		this.ContentType = ContentType;
	}

	public ReceiveMailRequest withContentType(String ContentType) {
		this.ContentType = ContentType;
		return this;
	}

	/**
	 * 
	 * @return
	 *         The bodyPlain
	 */
	public String getBodyPlain() {
		return bodyPlain;
	}

	/**
	 * 
	 * @param bodyPlain
	 *            The body-plain
	 */
	public void setBodyPlain(String bodyPlain) {
		this.bodyPlain = bodyPlain;
	}

	public ReceiveMailRequest withBodyPlain(String bodyPlain) {
		this.bodyPlain = bodyPlain;
		return this;
	}

	public String getCc() {
		return Cc;
	}

	public void setCc(String cc) {
		Cc = cc;
	}

	public String getBcc() {
		return Bcc;
	}

	public void setBcc(String bcc) {
		Bcc = bcc;
	}

	@Override
	public String toString() {
		JSONObject obj = new JSONObject();
		obj.put("strippedSignature", strippedSignature);
		obj.put("From", From);
		obj.put("XEnvelopeFrom", XEnvelopeFrom);
		obj.put("recipient", recipient);
		obj.put("XGoogleDkimSignature", XGoogleDkimSignature);
		obj.put("To", To);
		obj.put("Cc", Cc);
		obj.put("Bcc", Bcc);
		obj.put("strippedText", strippedText);
		obj.put("contentIdMap", contentIdMap);
		obj.put("subject", subject);
		obj.put("strippedHtml", strippedHtml);

		obj.put("from", from);
		obj.put("sender", sender);
		obj.put("XMailgunIncoming", XMailgunIncoming);
		obj.put("XReceived", XReceived);
		obj.put("XGmMessageState", XGmMessageState);
		obj.put("bodyHtml", bodyHtml);
		obj.put("References", References);

		obj.put("MimeVersion", MimeVersion);
		obj.put("InReplyTo", InReplyTo);
		obj.put("Date", Date);
		obj.put("MessageId", MessageId);
		obj.put("ContentType", ContentType);
		obj.put("bodyPlain", bodyPlain);
		obj.put("Subject", Subject);
		return obj.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(Received).append(strippedSignature).append(From).append(XEnvelopeFrom).append(recipient).append(XGoogleDkimSignature).append(To).append(messageHeaders)
				.append(strippedText).append(contentIdMap).append(subject).append(strippedHtml).append(from).append(sender).append(XMailgunIncoming).append(XReceived).append(XGmMessageState)
				.append(attachments).append(bodyHtml).append(References).append(MimeVersion).append(InReplyTo).append(Date).append(MessageId).append(ContentType).append(bodyPlain).append(Subject)
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ReceiveMailRequest) == false) {
			return false;
		}
		ReceiveMailRequest rhs = ((ReceiveMailRequest) other);
		return new EqualsBuilder().append(Received, rhs.Received).append(strippedSignature, rhs.strippedSignature).append(From, rhs.From).append(XEnvelopeFrom, rhs.XEnvelopeFrom)
				.append(recipient, rhs.recipient).append(XGoogleDkimSignature, rhs.XGoogleDkimSignature).append(To, rhs.To).append(messageHeaders, rhs.messageHeaders)
				.append(strippedText, rhs.strippedText).append(contentIdMap, rhs.contentIdMap).append(subject, rhs.subject).append(strippedHtml, rhs.strippedHtml).append(from, rhs.from)
				.append(sender, rhs.sender).append(XMailgunIncoming, rhs.XMailgunIncoming).append(XReceived, rhs.XReceived).append(XGmMessageState, rhs.XGmMessageState)
				.append(attachments, rhs.attachments).append(bodyHtml, rhs.bodyHtml).append(References, rhs.References).append(MimeVersion, rhs.MimeVersion).append(InReplyTo, rhs.InReplyTo)
				.append(Date, rhs.Date).append(MessageId, rhs.MessageId).append(ContentType, rhs.ContentType).append(bodyPlain, rhs.bodyPlain).append(Subject, rhs.Subject).isEquals();
	}

}
