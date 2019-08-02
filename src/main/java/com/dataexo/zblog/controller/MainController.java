package com.dataexo.zblog.controller;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.util.ResultInfoFactory;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.util.VerifyCaptcha;
import com.dataexo.zblog.vo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class MainController extends AbstractController {

    private static final Logger logger = Logger.getLogger(MainController.class);

    @Value("${captcha.secretkey}")
    public String captcha_secretkey;

    @Value("${ssoauth.auth_url}")
    public String ssoAuthUrl;

    @Value("${ssoauth.logout}")
    public String logout;

    @Value("${ssoauth.jforum_url}")
    public String jforum_url;

    @Value("${ssoauth.login_url}")
    public String login_url;

    @Value("${address.domain}")
    public String domain;

    @Value("${address.domain}")
    public String baseDomain;


    @Value("${address.email}")
    public String support_email;

    @Value("${token.auth.timeout}")
    public int tokenAuthTimeout;


    @Value("${ssoauth.permit_url}")
    public String ssoPermitUrl;

    @Value("${paypal.mode}")
    public String paypalMode;

    @Value("${paypal.client.app}")
    public String paypalClientId;


    @Value("${server.mode}")
    public String serverMode;


    @Resource
    private Data_categoryService data_categoryService;

    @Resource
    private ThirdPartyService thirdPartyService;

    @Resource
    private Price_modelService price_modelService;  //price_model

    @Resource
    private Asset_classService asset_classService;  //asset_class

    @Resource
    private Data_typeService data_typeService;  //data_type

    @Resource
    private RegionService regionService;  //region

    @Resource
    private PublisherService publisherService;  //publisher

    @Resource
    private BucketService bucketService;  //publisher

    @Resource
    private UserService userService;

    @Resource
    private TokenService tokenService;

    @Resource
    private Pay_orderService pay_orderService;

    @Resource
    private Data_setsService data_setsService;

    @Resource
    private VendorService vendorService;

    @Resource
    private Pay_SourceService pay_sourceService;


    /**
     * This is landing page for dataexo.
     *
     * @param model
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/")
    public String index(Model model
            , HttpServletRequest request
            , HttpServletResponse response
            , HttpSession session){



        /////// Check SSO Authentication//////////
        String token = request.getParameter("token");
        String apikey = request.getParameter("apikey");
        String redirect_from  = request.getParameter("redirect");
        User userinfo = (User) session.getAttribute("user");
        if(userinfo == null) {
            if (token == null) {
                try {
                    String new_token = UUID.randomUUID().toString().toLowerCase();
                    String redirect = URLEncoder.encode(domain + request.getServletPath(), "UTF-8");

                    return "redirect:" + ssoAuthUrl + "?redirect=" + redirect + "&token=" + new_token;

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.error( "encoding isssue:" , e);
                    return "error/500.html";
                }
            }

            if (apikey != null) {

                User user = userService.loadUserByApiKey(apikey);
                if(user != null){
                    session.setAttribute("user", user);
                }

                if(redirect_from != null) {
                    if (!redirect_from.equals("")) {

                        return "redirect:" + redirect_from;
                    }
                }
            }

        }

        /////////////////////
        baseRequest(session, model);

        List<Data_category> list = data_categoryService.findAll();
        List<Data_category> showList = new ArrayList<Data_category>();

        int len = 4;
        if(list.size() < len){
            len = list.size();
        }

        for(int i = 0 ; i < len ; i ++){
            showList.add(list.get(i));
        }

        model.addAttribute("subdata_categoryList" , showList);

        Pager pager = new Pager();
        pager.setSearch_str("");
        pager.setStart(0);
        pager.setLimit(6);
        List<Data_sets> data_setsList =  data_setsService.loadData_sets(pager , null);

        if(data_setsList.size() < 6 && data_setsList.size() != 0){
            len = 6 - data_setsList.size();
            for(int i = 0 ; i < len ; i ++ ){
                data_setsList.add(data_setsList.get(i));
            }
        }
        model.addAttribute("dataSetFeatureList" , data_setsList);

       pager.setLimit(4);
       data_setsList =  data_setsService.loadData_sets(pager , null);

        if(data_setsList.size() < 4 && data_setsList.size() != 0){
             len = 4 - data_setsList.size();
            for(int i = 0 ; i < len ; i ++ ){
                data_setsList.add(data_setsList.get(i));
            }
        }
        model.addAttribute("dataSetTopSellList" , data_setsList);
        return "index";
    }


    /**
     * This is about us page.
     * In this page , we can show about company information
     * @param model
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/aboutus")
    public String aboutus(Model model
            , HttpServletRequest request
            , HttpServletResponse response
            , HttpSession session){

        logger.debug("aboutus");
        baseRequest(session,model);
        return "aboutus";
    }


    @RequestMapping("/privacy")
    public String privacy(Model model
            , HttpServletRequest request
            , HttpServletResponse response
            , HttpSession session){

        baseRequest(session,model);
        return "privacy_policy";
    }


    /**
     * This is waht we do page.
     * @param model
     * @param request
     * @param response
     * @param session
     * @return
     */
    @RequestMapping("/whatwedo")
    public String whatwedo(Model model
            , HttpServletRequest request
            , HttpServletResponse response
            , HttpSession session){

        logger.debug("whatwedo");
        baseRequest(session,model);
        return "what_we_do";
    }

    //todo
    @RequestMapping("/cateid/{cateid}")
    public String catelist(@PathVariable String cateid, Model model, HttpServletRequest request , HttpServletResponse response, HttpSession session){

        baseRequest(session,model);

        logger.debug("data browser page by cateid");

        List<Price_model> price_modelsList = (List<Price_model>) session.getAttribute("price_modelsList");
        if(price_modelsList == null){
            price_modelsList = price_modelService.findAll();
            session.setAttribute("price_modelsList", price_modelsList);
        }

        List<Asset_class> assetClassList =  (List<Asset_class>) session.getAttribute("assetClassList");
        if(assetClassList == null){
            assetClassList = asset_classService.findAll();
            session.setAttribute("assetClassList", assetClassList);
        }

        List<Data_type> dataTypeList =  (List<Data_type>) session.getAttribute("dataTypeList");
        if(dataTypeList == null){
            dataTypeList = data_typeService.findAll();
            session.setAttribute("dataTypeList", dataTypeList);
        }

        List<Region> regionList =  (List<Region>) session.getAttribute("regionList");
        if(regionList == null){
            regionList = regionService.findAll();
            session.setAttribute("regionList", regionList);
        }

        List<Publisher> publisherList =  (List<Publisher>) session.getAttribute("publisherList");
        if(publisherList == null){
            publisherList = publisherService.findAll();
            session.setAttribute("publisherList", publisherList);
        }

        List<Vendors> vendorsList =  (List<Vendors>) session.getAttribute("vendorsList");
        if(vendorsList == null){
            vendorsList = vendorService.getAvailableVendors();

            session.setAttribute("vendorList", vendorsList);
        }

        model.addAttribute("cateid",cateid);
        model.addAttribute("price_modelsList",price_modelsList);
        model.addAttribute("asset_classList",assetClassList);
        model.addAttribute("data_typeList",dataTypeList);
        model.addAttribute("regionList",regionList);
        model.addAttribute("publisherList",publisherList);
        model.addAttribute("vendorList",vendorsList);


        return "dataset/data_browser";
    }


    @RequestMapping("/sso_login/redirect")
    public String sso_login(HttpServletRequest request
            , Model model
            , HttpSession session){
        String redirect =  "";
        String new_token = UUID.randomUUID().toString().toLowerCase();

        try {

            Token token_model = new Token();
            token_model.setToken(new_token);
            token_model.setExpire((System.currentTimeMillis() + 1000 * 60 * 10) + "");
            tokenService.insertToken(token_model);
             redirect = URLEncoder.encode(domain,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            logger.error( "encoding isssue:" , e);

            return "error/500";
        }

        String session_id = session.getId();
        return "redirect:" + login_url + "?redirect=" + redirect + "&token=" + new_token + "&session_id=" + session_id;
    }

    @RequestMapping("/login/sso")
    public String login_sso(Model model
            , HttpServletRequest request
            , HttpServletResponse response
            , HttpSession session) throws UnsupportedEncodingException {

        String apiKey = request.getParameter("apikey");
        String token = request.getParameter("token");
        String redirect = request.getParameter("redirect");

        if(token == null || apiKey == null){
            return "error/permission";
        }

        Token current_token = tokenService.getByToken(token);

        if (current_token == null) {
            return "error/permission";
        }

        long current_time = System.currentTimeMillis();
        long diff = current_time - Long.parseLong(current_token.getExpire());

        if (diff > 0) {
            return "error/permission";
        }

        if(redirect == null){
            return "error/permission";
        }


        User user = userService.loadUserByApiKey(apiKey);

        Bucket bucket = new Bucket();
        bucket.setSession_id(session.getId());
        bucket.setUserid(Integer.parseInt(""+user.getId()));
        bucketService.updateBucketInfo(bucket);
        bucketService.updateBucketInfoById(bucket);

        session.setAttribute("user", user);

        return "redirect:" + URLDecoder.decode(redirect, "UTF-8");
    }

    @RequestMapping("/cateid")
    public String home1(Model model){
        return "redirect:/cateid/1";
    }


    /**
     * 跳转到登录页面
     * @return
     */
    @RequestMapping("/login")
    public String loginPage(HttpSession session,Model model, HttpServletRequest request){

        User adminuser = (User) session.getAttribute("adminuser");
        if(adminuser != null){
             return "redirect:/admin/user/list";
        }

        ////////

        // admin
        // F3270B464E2742776A723472B71D9A0E: abc123

        // dex-admin
        // B06EBCB2AB58D8A05A36261DC0EDF927: dex-p8d$

        model.addAttribute("sitekey",captcha_sitekey);
        return "login";
    }

    @RequestMapping("/adminlogin")
    public String adminloginPage(@RequestParam("g-recaptcha-response") String captcha,

                                @RequestParam("username") String username,
                                @RequestParam("password") String password ,
                                HttpServletRequest request,
                                HttpSession session, Model model){

        model.addAttribute("sitekey",captcha_sitekey);

        Boolean captcha_result = false;

        String userAgent = request.getHeader("User-Agent");
        try {
            captcha_result = VerifyCaptcha.verify(captcha, userAgent,captcha_secretkey);
        } catch (IOException e) {
            e.printStackTrace();

            logger.error( "Captcha error:" , e);

            captcha_result =false;
        }

        if(!captcha_result){
            return "login";
        }
        else {
            User adminuser = userService.loadUserByUsername(username);

            if(adminuser == null){
                return "login";
            }
            String pass = Md5Util.pwdDigest(password);
            if(!adminuser.getPassword().equals(pass)){
                return "login";
            }
            if(adminuser.getId() != 1){
                return "login";
            }
            session.setAttribute("adminuser",adminuser);

        }
        return "redirect:/admin/user/list";
    }

    @RequestMapping("/admin")
    public String adminPage(){
        return "redirect:/login";
    }


    /**
     * Reset password page
     * @param userid  : the user id when user sent forgotten password request
     * @param token  : the user token when user sent forgotten password request
     * @param model
     * @param session
     * @return String
     */

    @RequestMapping(value = "/ForgotPassword/newPassword/{userid}/{token}", method = RequestMethod.GET)
    public String setNewPassword(@PathVariable String userid
            , @PathVariable String token,Model model, HttpSession session){


        logger.debug("Forgot password  for user");

        Token tokenModel = tokenService.getByToken(token);
        if(tokenModel == null){
            return "error/permission";
        }
        long cur = System.currentTimeMillis();
        if(cur > Long.parseLong(tokenModel.getExpire())){
            return "error/token_expire";
        }

        baseRequest(session,model);
        User userinfo = userService.loadUserById(Long.parseLong(userid));
        if(userinfo == null){
            return "error/permission";
        }

        List<Price_model> price_modelsList = price_modelService.findAll();
        List<Asset_class> assetClassList= asset_classService.findAll();
        List<Data_type> dataTypeList= data_typeService.findAll();
        List<Region> regionList= regionService.findAll();
        List<Publisher> publisherList= publisherService.findAll();


        String ssotoken = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
        long expire = System.currentTimeMillis() + 1000 * 60 * tokenAuthTimeout;
        com.dataexo.zblog.vo.Token token_class = new com.dataexo.zblog.vo.Token();
        token_class.setExpire("" + expire);
        token_class.setToken(ssotoken);
        tokenService.insertToken(token_class);

        String redirect = "";
        try {
            redirect = URLEncoder.encode(baseDomain, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String requestUrl = ssoPermitUrl + "?token=" + token + "&redirect=" + redirect + "&apiKey="+userinfo.getApiKey();
       // return "redirect:"+requestUrl;

        model.addAttribute("forgetSSOPermit",requestUrl);

        model.addAttribute("cateid",1);

        model.addAttribute("price_modelsList",price_modelsList);
        model.addAttribute("asset_classList",assetClassList);
        model.addAttribute("data_typeList",dataTypeList);
        model.addAttribute("regionList",regionList);
        model.addAttribute("publisherList",publisherList);
        model.addAttribute("resetPassword","1");

        model.addAttribute("user_email",userinfo.getEmail());
        model.addAttribute("token",token);

        return "index";

    }


    @RequestMapping("/error_download")
    public String error_download(){
        return "error/download";
    }

    @RequestMapping("/contactus")
    public String contactus(HttpSession session, Model model){
        baseRequest(session,model);
        return "contactus";
    }

    @RequestMapping("/documentation/main")
    public String DocHelp(HttpSession session, Model model){
        baseRequest(session,model);

        model.addAttribute("jforum_url" , jforum_url);
        model.addAttribute("support_email" , support_email);

        return "documentation/main";
    }

    /**
     * This function is for user logout.
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request , HttpSession session) {

        logger.debug("logout  for user");

        boolean flag = true;
        try {
            String link = jforum_url + "/jforum.page?module=ajax&action=ssologout";

            UtilClass.sendGet(link, serverMode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error( "jforum connection error:" , e);
        }

        String redirect = "";
        try {
             redirect = URLEncoder.encode(domain , "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error( e);
        }
        session.removeAttribute("user");

        return "redirect:" + logout + "?redirect=" + redirect;

    }


    @RequestMapping("/user/insert/api")
   public String addNewUser(HttpServletRequest request
            , HttpServletResponse response
            , HttpSession session
            , Model model){

        String username = request.getParameter("username");
        String upassword = request.getParameter("password");
        upassword = upassword.replaceAll("%10","#");
        String email = request.getParameter("email");
        String token = request.getParameter("token");

        Token token_model = tokenService.getByToken(token);
        if(token_model == null){
            return "error/permission";
        }

        User userInfo = userService.loadUserByUsername(username);
        if (userInfo == null) {
            userInfo = userService.loadUserByEmail(email);
            if (userInfo == null) {
                String password = Md5Util.pwdDigest(upassword);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);

                user.setCreateTime(dateFormat.format(date));
                user.setPassword(password);

                token = UUID.randomUUID().toString().toLowerCase();
                user.setToken(token);

                user.setApiKey(UtilClass.generateAPIKey());
                userService.insertUserInfo(user);

                String url = jforum_url + "/jforum.page?module=ajax&action=ssoinsert";

                String key_data = UUID.randomUUID().toString().toLowerCase();
                String enc_data  = null;
                try {

                    enc_data = Md5Util.pwdDigest(key_data);
                    String param = "&username="+user.getUsername() + "&email="+user.getEmail();
                    param += "&enc_data="+enc_data + "&key_data="+key_data + "&password=" + user.getPassword() ;

                    UtilClass.sendGet(url + param, serverMode);

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error( e);
                }

              //  session.setAttribute("user",user);
            } else {
                return "error/permission";
            }
        } else {
            return "error/permission";
        }

        return "redirect:/";
    }

    @RequestMapping("/payment/{order_id}")
    public String payment(@PathVariable Integer order_id
            , HttpServletResponse response
            , HttpSession session
            , Model model){


        logger.debug("payment page");

        if(!baseRequest(session,model)){
            return "redirect:/";
        }

        if(order_id == 0){
            return "redirect:/";
        }

        ////////// get pay sources ///////////
        User user = (User) session.getAttribute("user");
        Pager pager = new Pager();
        pager.setUser_id(Integer.parseInt("" + user.getId()));
        List<Pay_sources> sourceList = pay_sourceService.loadPay_Source(pager);
        model.addAttribute("sourceList" , sourceList);
        ///////////////////

        Pay_order pay_order = pay_orderService.getPayOrder(order_id);
        Token tokenModel = tokenService.getByToken(pay_order.getToken());

        long current_time = System.currentTimeMillis();
        long diff = current_time - Long.parseLong(tokenModel.getExpire());

        if (diff > 0) {
            return "error/token_expire";
        }

        if(pay_order.getDataset_ids().equals("-1")){
            model.addAttribute("mode" , "membership");
        }
        Double discount = 0.0;
        Double finalAmount = (double)pay_order.getAmount();
        Double amount = finalAmount;

        if(pay_order.getMembership_id().equals("-1")){
            amount = 0.0;
            model.addAttribute("mode" , "checkout");
            String [] datasets_id = pay_order.getDataset_ids().split(",");
            if(datasets_id != null) {
                for (String dataset_id : datasets_id) {
                    amount += data_setsService.getData_setsById(Integer.parseInt(dataset_id)).getOnetime_price();
                }
            }
            discount = amount - finalAmount;
        }
        DecimalFormat formatter = new DecimalFormat("#0.00");
        model.addAttribute("discount_amount", formatter.format(discount));
        model.addAttribute("amount" , formatter.format(amount));
        model.addAttribute("final_amount", formatter.format(finalAmount));
        model.addAttribute("order_id" , pay_order.getId());
        model.addAttribute("paypalMode", paypalMode);
        model.addAttribute("paypalClientId", paypalClientId);

        return "payment";
    }

    @RequestMapping("/sendmail")
    public String sendmail(HttpServletResponse response
            , HttpSession session1
            , Model model) throws Exception{

        // Create a Properties object to contain connection configuration information.
        String body = "<h1>Amazon SES SMTP Email Test</h1><p>This email was sent with Amazon SES using the <a href='https://github.com/javaee/javamail'>Javamail Package</a>for <a href='https://www.java.com'>Java</a>.";

        thirdPartyService.sendAwsSes("wangyi_1986@hotmail.com", "service@dataexo.com" , "Testing" , body);
        return "ok";
    }

    @RequestMapping("/documentation/dataexo")
    public String doc_dataexo(HttpSession session, Model model){
        baseRequest(session,model);
        return "documentation/dataexo";
    }

    @RequestMapping("/documentation/analyze")
    public String doc_analyze(HttpSession session, Model model){
        baseRequest(session,model);
        return "documentation/zeppelin";
    }

}
