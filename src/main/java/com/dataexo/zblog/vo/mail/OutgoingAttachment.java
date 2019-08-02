package com.dataexo.zblog.vo.mail;

import java.io.File;

import com.google.gson.annotations.SerializedName;

public class OutgoingAttachment {

	@SerializedName("filename")
	public String filename;

	@SerializedName("content-type")
	public String contentType;

	@SerializedName("size")
	public Long size;

	@SerializedName("deleteAfterSent")
	private boolean deleteAfterSent = false;

	@SerializedName("attachmentId")
	private String attachmentId;

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public boolean isDeleteAfterSent() {
		return deleteAfterSent;
	}

	public void setDeleteAfterSent(boolean deleteAfterSent) {
		this.deleteAfterSent = deleteAfterSent;
	}

	public File getAttachmentFile() {
		return attachmentFile;
	}

	public void setAttachmentFile(File attachmentFile) {
		this.attachmentFile = attachmentFile;
	}

	@SerializedName("file")
	public File attachmentFile;

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getSize() {
		return this.size;
	}

	public void setSize(Long size) {
		this.size = size;
	}
}