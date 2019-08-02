package com.dataexo.zblog.util;

import com.dataexo.zblog.controller.MainController;
import org.apache.log4j.Logger;

import java.security.MessageDigest;



public class Md5Util {

	private static final Logger logger = Logger.getLogger(Md5Util.class);
	public static final String PWD_CONST = "DataExo2017";

	public static String pwdDigest(String password){
		return digest(password+PWD_CONST);
	}

	private final static String digest(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {

			logger.error( e);

			e.printStackTrace();

			return null;
		}
	}
	
}
