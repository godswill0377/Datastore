package com.dataexo.zblog.exception;

import com.dataexo.zblog.vo.auth.MailGunResponse;

public class MailGunException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MailGunException(String message, MailGunResponse response) {
		super(String.format("%s - %s", message, response.getMessage()));
	}

	public MailGunException(String message, Exception e) {
		super(message, e);
	}
}
