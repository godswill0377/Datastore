package com.dataexo.zblog.exception;


import com.dataexo.zblog.vo.auth.MailGunResponse;

public class MissingRequiredParameters extends MailGunException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MissingRequiredParameters(String message, MailGunResponse response) {
		super(message, response);
	}
}
