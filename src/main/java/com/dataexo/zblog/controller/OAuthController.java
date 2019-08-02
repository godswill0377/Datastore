package com.dataexo.zblog.controller;


import com.dataexo.zblog.service.ThirdPartyService;
import com.dataexo.zblog.service.TokenService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.auth.Facebook;
import com.dataexo.zblog.vo.auth.GithubPojo;
import com.dataexo.zblog.vo.auth.GoogleAuthHelper;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * This is authentication controller.
 * This implements github , linkedin , google , facebook authentication.
 * For example , when you are going to authenticate via github ,
 * First , githubAuth() function is called.
 * Then it will redirect to github athenticate url.
 * After login the github original site , it will call callback function (github_callback).
 * github_callback is called.
 * It checks the token and authentication result in github_callback  function.
 *
 */
@Controller
public class OAuthController extends AbstractController {

    private static final Logger logger = Logger.getLogger(OAuthController.class);



    @Value("${address.domain}")
    public String baseDomain;

    @Value("${ssoauth.permit_url}")
    public String ssoPermitUrl;


    @Value("${address.appname}")
    public String appname;

    @Value("${github.clientid}")
    public String github_clientid;
    @Value("${github.client_secret}")
    public String github_client_secret;
    @Value("${github.redirect_url}")
    public String redirect_url;

    @Value("${linkedin.clientid}")
    public String linkedin_clientid;
    @Value("${linkedin.client_secret}")
    public String linkedin_client_secret;
    @Value("${linkedin.redirect_url}")
    public String linkedin_redirect_url;

    @Value("${facebook.appid}")
    public String facebook_appid;
    @Value("${facebook.app_secret}")
    public String facebook_secret;
    @Value("${facebook.redirect_url}")
    public String facebook_url;

    @Value("${google.clientid}")
    public String google_clientid;
    @Value("${google.client_secret}")
    public String google_secret;
    @Value("${google.redirect_url}")
    public String google_url;

    @Value("${token.auth.timeout}")
    public int tokenAuthTimeout;

    @Value("${token.mail.timeout}")
    public int tokenMailTimeout;

    @Value("${server.mode}")
    public String serverMode;

    @Resource
    private UserService userService;

    @Autowired
    private TokenService tokenService;


    @Autowired
    private ThirdPartyService thirdPartyService;


    private  OAuthService linkedin_service;
    private Token linkedin_requestToken;

    private static final String PROTECTED_RESOURCE_URL = "http://api.linkedin.com/v1/people/~/connections:(id,last-name)";

    /**
     * This is github signin function.
     * It will redirects to authentication url.
     * @return
     */
    @RequestMapping(value = "/user/github/auth", method = RequestMethod.GET)
    public String githubAuth() {

        logger.debug("Github login");


        String url = "https://github.com/login/oauth/authorize?client_id="+github_clientid+"&redirect_uri="+redirect_url+"&scope=user";
        return "redirect:"+url;
    }

    /**
     * This is github signup function.
     * It will redirects to authentication url.
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/github/signup_auth", method = RequestMethod.GET)
    public String githubSignupAuth(HttpSession session) {

        logger.debug("Github signup");

        String url = "https://github.com/login/oauth/authorize?client_id="+github_clientid+"&redirect_uri="+redirect_url+"&scope=user";
        session.setAttribute("signup_auth","1");
        return "redirect:"+url;
    }

    /**
     * This is callback function after authenticate via github.
     * In this function , it checks token and response result.
     * Then also it will save the github user information.
     *
     * @param request
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/github/callback", method = RequestMethod.GET)
    public String github_callback(HttpServletRequest request,  HttpSession session) {
        String github_url = "https://github.com/login/oauth/authorize?client_id="+github_clientid+"&redirect_uri="+redirect_url+"&scope=user";

        try {
            String code = request.getParameter("code");
            if (code == null) {

            } else {
                URL url = new URL(
                        "https://github.com/login/oauth/access_token?client_id="
                                + github_clientid + "&redirect_uri=" + redirect_url
                                + "&client_secret=" + github_client_secret + "&code=" +
                                code);

                BufferedReader reader = null;
                String outputString = "";
                if(serverMode.equals("https")) {
                    HttpsURLConnection conn = (HttpsURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(20000);

                    reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                }
                else{
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(20000);

                    reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                }
                String line;
                while ((line = reader.readLine()) != null) {
                    outputString = outputString + line;
                }
                System.out.println(outputString);
                String accessToken = null;
                if (outputString.indexOf("access_token") != -1) {
                    accessToken = outputString.substring(13,
                            outputString.indexOf("&"));
                }
                url = new URL("https://api.github.com/user");
                System.out.println(url);

                if(serverMode.equals("https")) {
                    HttpsURLConnection myURLConnection = (HttpsURLConnection) url
                            .openConnection();
                    myURLConnection.setRequestProperty("Authorization", "token "
                            + accessToken);
                    myURLConnection.setRequestProperty("User-Agent", appname);
                    myURLConnection.setRequestMethod("GET");
                    myURLConnection.setUseCaches(false);
                    myURLConnection.setDoInput(true);
                    myURLConnection.setDoOutput(true);
                    myURLConnection.setConnectTimeout(7000);

                    outputString = "";
                    reader = new BufferedReader(new InputStreamReader(
                            myURLConnection.getInputStream()));

                }
                else{
                    HttpURLConnection myURLConnection = (HttpURLConnection) url
                            .openConnection();
                    myURLConnection.setRequestProperty("Authorization", "token "
                            + accessToken);
                    myURLConnection.setRequestProperty("User-Agent", appname);
                    myURLConnection.setRequestMethod("GET");
                    myURLConnection.setUseCaches(false);
                    myURLConnection.setDoInput(true);
                    myURLConnection.setDoOutput(true);
                    myURLConnection.setConnectTimeout(7000);

                    outputString = "";
                    reader = new BufferedReader(new InputStreamReader(
                            myURLConnection.getInputStream()));
                }

                while ((line = reader.readLine()) != null) {
                    outputString = outputString + line;
                }
                reader.close();

                GithubPojo gp = (GithubPojo) new Gson().fromJson(outputString,
                        GithubPojo.class);


                User userinfo = new User();
                if(gp.getEmail() == null)
                    userinfo.setEmail("");
                else
                    userinfo.setEmail(gp.getEmail());
                if(gp.getName() == null){
                    if(gp.getLogin() == null)
                        gp.setName("");
                    else
                        gp.setName(gp.getLogin());
                }

                userinfo.setUsername(gp.getName().replaceAll(" ","") + "2018");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                userinfo.setCreateTime(dateFormat.format(date));
                String token = UUID.randomUUID().toString().toLowerCase() ;
                userinfo.setToken(token);
                userinfo.setPassword(Md5Util.pwdDigest(gp.getName().replaceAll(" ","")));
                userinfo.setApiKey(UtilClass.generateAPIKey());
                userinfo.setActivate(1);

                User usertmp = userService.checkUser(userinfo);
                if(usertmp != null){
                    userinfo = usertmp;
                }
                else {

                    session.setAttribute("resetPassword", "1");
                    userService.insertUserInfo(userinfo);
                    userinfo = userService.checkUser(userinfo);

                    thirdPartyService.addNewsToSystem(userinfo); // add to jforum project
                }
                session.removeAttribute("signup_auth");
                session.setAttribute("user", userinfo);

                token = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
                long expire = System.currentTimeMillis() + 1000 * 60 * tokenAuthTimeout;
                com.dataexo.zblog.vo.Token token_class = new com.dataexo.zblog.vo.Token();
                token_class.setExpire("" + expire);
                token_class.setToken(token);
                tokenService.insertToken(token_class);

                String redirect =  URLEncoder.encode(baseDomain, java.nio.charset.StandardCharsets.UTF_8.toString());

                String requestUrl = ssoPermitUrl + "?token=" + token + "&redirect=" + redirect + "&apiKey="+userinfo.getApiKey();
                return "redirect:"+requestUrl;

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error( e);
        }
        return "redirect:"+github_url;
    }

    /**
     * This is linkedin signin function.
     * It will redirects to authentication url.
     * @return
     */
    @RequestMapping(value = "/user/linkedin/auth", method = RequestMethod.GET)
    public String linkedinAuth() {

        logger.debug("linkedin login");

        linkedin_service = new ServiceBuilder()
                .provider(LinkedInApi.class)
                .apiKey(linkedin_clientid)
                .apiSecret(linkedin_client_secret)
                .callback(linkedin_redirect_url)
                .scope("r_basicprofile,r_emailaddress")
                .build();

        linkedin_requestToken = linkedin_service.getRequestToken();

        String AuthUrl=linkedin_service.getAuthorizationUrl(linkedin_requestToken);

        return "redirect:"+AuthUrl;
    }

    /**
     * This is linkedin signup function.
     * It will redirects to authentication url.
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/linkedin/signup_auth", method = RequestMethod.GET)
    public String linkedinSignupAuth(HttpSession session) {

        logger.debug("linkedin signup");

        linkedin_service = new ServiceBuilder()
                .provider(LinkedInApi.class)
                .apiKey(linkedin_clientid)
                .apiSecret(linkedin_client_secret)
                .callback(linkedin_redirect_url)
                .scope("r_basicprofile,r_emailaddress")
                .build();

        linkedin_requestToken = linkedin_service.getRequestToken();

        String AuthUrl=linkedin_service.getAuthorizationUrl(linkedin_requestToken);
        session.setAttribute("signup_auth","1");
        return "redirect:"+AuthUrl;
    }

    /**
     * This is callback function after authenticate via linkedin.
     * In this function , it checks token and response result.
     * Then also it will save the linkedin user information.
     * @param httprequest
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/linkedin/callback", method = RequestMethod.GET)
    public String linkedin_callback(HttpServletRequest httprequest, HttpSession session) {

        String verifier=httprequest.getParameter("oauth_verifier");// linkedin girişi yaptıktan sonra linkedin bize bir gerçekleyici kodu vermekte
        try{
            if(verifier != null) {
                Verifier linkedVer = new Verifier(verifier);

                Token AccessToken = linkedin_service.getAccessToken(linkedin_requestToken, linkedVer); // istemci jetonu ve gerçekleyici kodumuzu kullanarak bir erişim jetonu alıyoruz :)
                OAuthRequest request = new OAuthRequest(Verb.GET,
                        "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,industry,email-address,site-standard-profile-request,picture-url)?format=json");

                linkedin_service.signRequest(AccessToken, request); // iletişim jetonunu ve istemlerimizi bildiriyoruz denilebilir.
                Response resp = request.send();// yanıtımızı alıyoruz.
                String json = resp.getBody();

                JSONObject jsonObj = new JSONObject(json);


                User userinfo = new User();
                userinfo.setEmail(jsonObj.getString("emailAddress") );

                String username = jsonObj.getString("firstName") + jsonObj.getString("lastName");
                username = username.replaceAll(" ","");
                userinfo.setUsername(username + "2018");

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                userinfo.setCreateTime(dateFormat.format(date));
                String token = UUID.randomUUID().toString().toLowerCase() ;
                userinfo.setToken(token);
                userinfo.setPassword(Md5Util.pwdDigest(userinfo.getUsername()));

                userinfo.setApiKey(UtilClass.generateAPIKey());
                userinfo.setActivate(1);

                User usertmp = userService.checkUser(userinfo);

                if(usertmp != null){
                    userinfo = usertmp;
                }
                else {

                    session.setAttribute("resetPassword", "1");
                    userService.insertUserInfo(userinfo);
                    userinfo = userService.checkUser(userinfo);
                    thirdPartyService.addNewsToSystem(userinfo); // add to jforum project
                }
                session.removeAttribute("signup_auth");
                session.setAttribute("user", userinfo);

                token = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
                long expire = System.currentTimeMillis() + 1000 * 60 * tokenAuthTimeout;
                com.dataexo.zblog.vo.Token token_class = new com.dataexo.zblog.vo.Token();
                token_class.setExpire("" + expire);
                token_class.setToken(token);
                tokenService.insertToken(token_class);

                String redirect =  URLEncoder.encode(baseDomain , java.nio.charset.StandardCharsets.UTF_8.toString());

                String requestUrl = ssoPermitUrl + "?token=" + token + "&redirect=" + redirect + "&apiKey="+userinfo.getApiKey();
                return "redirect:"+requestUrl;

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error( e);
        }
        String AuthUrl=linkedin_service.getAuthorizationUrl(linkedin_requestToken);
        return "redirect:"+AuthUrl;
    }

    /**
     *  This is google signin function.
     * It will redirects to authentication url.
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/google/auth", method = RequestMethod.GET)
    public String googleAuth(HttpSession session) {

        logger.debug("google login");

        GoogleAuthHelper helper = new GoogleAuthHelper(google_clientid,google_secret,google_url);
        session.setAttribute("state", helper.getStateToken());
        String AuthUrl = helper.buildLoginUrl();
        return "redirect:"+AuthUrl;
    }

    /**
     * This is google signup function.
     * It will redirects to authentication url.
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/google/signup_auth", method = RequestMethod.GET)
    public String googleSignupAuth(HttpSession session) {

        logger.debug("google signup");

        GoogleAuthHelper helper = new GoogleAuthHelper(google_clientid,google_secret,google_url);

        session.setAttribute("state", helper.getStateToken());
        String AuthUrl = helper.buildLoginUrl();
        session.setAttribute("signup_auth","1");
        return "redirect:"+AuthUrl;

    }

    /**
     *  This is callback function after authenticate via google.
     * In this function , it checks token and response result.
     * Then also it will save the google user information.
     * @param httprequest
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/google/callback", method = RequestMethod.GET)
    public String google_callback(HttpServletRequest httprequest,  HttpSession session) {
        GoogleAuthHelper helper = new GoogleAuthHelper(google_clientid,google_secret,google_url);

        try {
            String code = httprequest.getParameter("code");
            String state = httprequest.getParameter("state");
         //   String new_state = (String) session.getAttribute("state");
            if (code != null && state != null ) {
                session.removeAttribute("state");

                String json= helper.getUserInfoJson(httprequest.getParameter("code"));

                JSONObject jsonObj = new JSONObject(json);


                    User userinfo = new User();
                    userinfo.setId(-1);
                    userinfo.setEmail(jsonObj.getString("email"));

                    String username = jsonObj.getString("name");
                    username = username.replaceAll(" ","");
                    userinfo.setUsername(username + "2018");

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    userinfo.setCreateTime(dateFormat.format(date));
                    String token = UUID.randomUUID().toString().toLowerCase() ;
                    userinfo.setToken(token);
                    userinfo.setPassword(Md5Util.pwdDigest(userinfo.getUsername()));

                    userinfo.setApiKey(UtilClass.generateAPIKey());
                    userinfo.setActivate(1);

                    User usertmp = userService.checkUser(userinfo);

                    if(usertmp != null){
                        userinfo = usertmp;
                    }
                    else {

                        session.setAttribute("resetPassword", "1");
                        userService.insertUserInfo(userinfo);
                        userinfo = userService.checkUser(userinfo);
                        thirdPartyService.addNewsToSystem(userinfo); // add to jforum project
                    }
                    session.removeAttribute("signup_auth");
                    session.setAttribute("user", userinfo);

                    token = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
                    long expire = System.currentTimeMillis() + 1000 * 60 * tokenAuthTimeout;
                    com.dataexo.zblog.vo.Token token_class = new com.dataexo.zblog.vo.Token();
                    token_class.setExpire("" + expire);
                    token_class.setToken(token);
                    tokenService.insertToken(token_class);

                    String redirect =  URLEncoder.encode(baseDomain , java.nio.charset.StandardCharsets.UTF_8.toString());

                    String requestUrl = ssoPermitUrl + "?token=" + token + "&redirect=" + redirect + "&apiKey="+userinfo.getApiKey();
                    return "redirect:"+requestUrl;

             }
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error( e);
        }
        String AuthUrl = helper.buildLoginUrl();
        return "redirect:"+AuthUrl;
    }

    /**
     *  This is callback function after authenticate via facebook.
     * In this function , it checks token and response result.
     * Then also it will save the google user information.
     * @return
     */
    @RequestMapping(value = "/user/facebook/auth", method = RequestMethod.GET)
    public String facebookAuth() {

        logger.debug("facebookAuth login");

        Facebook helper = new Facebook(facebook_appid,facebook_secret,facebook_url);

        String AuthUrl = helper.getLoginRedirectURL();

        return "redirect:"+AuthUrl;
    }

    /**
     *  This is facebook signup function.
     * It will redirects to authentication url.
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/facebook/signup_auth", method = RequestMethod.GET)
    public String facebookSignupAuth(HttpSession session) {

        logger.debug("facebookAuth signup");

        Facebook helper = new Facebook(facebook_appid,facebook_secret,facebook_url);
        String AuthUrl = helper.getLoginRedirectURL();
        session.setAttribute("signup_auth","1");
        return "redirect:"+AuthUrl;
    }


    /**
     * This is callback function after authenticate via facebook.
     * In this function , it checks token and response result.
     * Then also it will save the facebook user information.
     * @param httprequest
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/facebook/callback", method = RequestMethod.GET)
    public String facebook_callback(HttpServletRequest httprequest,  HttpSession session) {
        String code = httprequest.getParameter("code");
        Facebook helper = new Facebook(facebook_appid,facebook_secret,facebook_url);

        if (code != null) {
            String authURL = helper.getAuthURL(code);
            URL url = null;
            try {
                url = new URL(authURL);
                String result = UtilClass.readURL(url);
                JSONObject json = new JSONObject(result);
                String accessToken = json.getString("access_token");
                Integer expires = json.getInt("expires_in");

                if (accessToken != null && expires != null) {
                    JSONObject resp = new JSONObject(
                            UtilClass.readURL(new URL("https://graph.facebook.com/v2.5/me?fields=id,name,email&access_token=" + accessToken)));


                    String name = resp.getString("name");
                    String email = resp.getString("email");

                        User userinfo = new User();
                        userinfo.setId(-1);
                        userinfo.setEmail(email);

                        name = name.replaceAll(" ","");

                        userinfo.setUsername(name + "2018");

                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        userinfo.setCreateTime(dateFormat.format(date));
                        String token = UUID.randomUUID().toString().toLowerCase() ;
                        userinfo.setToken(token);
                        userinfo.setPassword(Md5Util.pwdDigest(userinfo.getUsername()));

                        userinfo.setApiKey(UtilClass.generateAPIKey());
                        userinfo.setActivate(1);

                    User usertmp = userService.checkUser(userinfo);

                        if(usertmp != null){
                            userinfo = usertmp;
                        }
                        else {

                            session.setAttribute("resetPassword", "1");
                            userService.insertUserInfo(userinfo);
                            userinfo = userService.checkUser(userinfo);
                            thirdPartyService.addNewsToSystem(userinfo); // add to jforum project
                        }
                        session.removeAttribute("signup_auth");
                        session.setAttribute("user", userinfo);

                        token = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
                        long expire = System.currentTimeMillis() + 1000 * 60 * tokenAuthTimeout;
                        com.dataexo.zblog.vo.Token token_class = new com.dataexo.zblog.vo.Token();
                        token_class.setExpire("" + expire);
                        token_class.setToken(token);
                        tokenService.insertToken(token_class);

                        String redirect =  URLEncoder.encode(baseDomain , java.nio.charset.StandardCharsets.UTF_8.toString());

                        String requestUrl = ssoPermitUrl + "?token=" + token + "&redirect=" + redirect + "&apiKey="+userinfo.getApiKey();
                        return "redirect:"+requestUrl;

                } else {
                    throw new RuntimeException("Access token and expires not found");
                }
            } catch (IOException e) {
                logger.error( e);
                throw new RuntimeException(e);

            }
        }
        String AuthUrl = helper.getLoginRedirectURL();
        return "redirect:"+AuthUrl;
    }
}
