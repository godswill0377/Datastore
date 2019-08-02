package com.dataexo.zblog.exception;

import com.dataexo.zblog.vo.auth.MailGunResponse;

public class MissingEndpoint extends MailGunException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingEndpoint(String message, MailGunResponse response) {
		super(message, response);
	}
}
