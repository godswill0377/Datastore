package com.dataexo.zblog.vo.auth;

public class Facebook {
    private  String secret;
    private  String client_id ;
    private String redirect_uri;
    public Facebook(String id,String secret, String url){
        this.client_id = id;
        this.secret = secret;
        this.redirect_uri = url;
    }
    public  String getSecret() {
        return secret;
    }

    public  String getLoginRedirectURL() {
        return "https://graph.facebook.com/oauth/authorize?client_id=" +
                client_id + "&display=page&redirect_uri=" +
                redirect_uri+"&scope=public_profile,email";
    }

    public  String getAuthURL(String authCode) {
        return "https://graph.facebook.com/oauth/access_token?client_id=" +
                client_id+"&redirect_uri=" +
                redirect_uri+"&client_secret="+secret+"&code="+authCode;
    }
}