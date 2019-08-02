package com.dataexo.zblog.service.impl;


import com.dataexo.zblog.service.ThirdPartyService;
import com.dataexo.zblog.service.TokenService;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.UUID;

@Service
@Transactional
public class ThirdPartyServiceImpl implements ThirdPartyService {

    private static final Logger logger = Logger.getLogger(ThirdPartyServiceImpl.class);

    @Value("${address.appname}")
    public String appName;


    @Value("${aws.domain}")
    public String awsDomain;

    @Value("${aws.user}")
    public String awsUser;

    @Value("${aws.pass}")
    public String awsPass;

    @Value("${aws.port}")
    public Integer awsPort;


    @Value("${ssoauth.jforum_url}")
    public String jforum_url;


    @Value("${server.mode}")
    public String serverMode;

    @Override
    public boolean sendAwsSes(String from, String to, String subject, String body) {

        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", awsPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties.
        Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information.
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from, appName));

            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(body,"text/html");

            Transport transport = session.getTransport();

            transport.connect(awsDomain,awsPort, awsUser, awsPass);

            transport.sendMessage(msg, msg.getAllRecipients());

        } catch (Exception e) {
            logger.error( e);
            e.printStackTrace();
            return false;
        }


        return true;
    }


    @Override
    public  boolean addNewsToSystem(User user){


        String key_data = UUID.randomUUID().toString().toLowerCase();
        String enc_data = Md5Util.pwdDigest(key_data);

        try {

            String jforum_insert_url = jforum_url + "/jforum.page?module=ajax&action=ssoinsert_login";

            key_data = UUID.randomUUID().toString().toLowerCase();
            enc_data = Md5Util.pwdDigest(key_data);

            String  param = "&username="+user.getUsername() + "&email="+user.getEmail() + "&enc_data="+enc_data
                    + "&key_data="+key_data + "&password="+Md5Util.pwdDigest(user.getUsername() + "dataexo") + "&apiKey="+user.getApiKey();

            UtilClass.sendGet(jforum_insert_url+param, serverMode);
        } catch (Exception e) {
            logger.error( e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
