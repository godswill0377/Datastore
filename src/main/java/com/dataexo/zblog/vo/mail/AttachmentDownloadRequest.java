package com.dataexo.zblog.vo.mail;

import java.util.List;

public class AttachmentDownloadRequest {

	private List<IncomingAttachment> attachments;
	private Integer index;

	public List<IncomingAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<IncomingAttachment> attachments) {
		this.attachments = attachments;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

}
