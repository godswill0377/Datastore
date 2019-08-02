package com.dataexo.zblog.controller.rest;

import com.dataexo.zblog.controller.ServiceControlller;
import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.*;
import com.dataexo.zblog.vo.*;

import com.stripe.exception.*;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is rest api controller implements for account management;
 * It is implemented via POST mode.
 * You can call this api in front end page,
 * exactly you can call this rest api using ajax which mode is POST.
 * Here is rest apis which implements membership purchase ,reset the profile , reset the password , ...
 * This api is integrated with js/account.js and blog/account/...
 * As you can see , most of the function's return class is ResultInfo.
 * This is status of api.
 * This means that ResultInfo.resultCode = 'success' is successfully operated.
 * The return result is json mode.
 * You can easily parse this json data using javascript in front end page.
 *
 * Create Time: 6/22
 * Created By: lang
 */


@RestController
public class AccountRestController {

    private static final Logger logger = Logger.getLogger(AccountRestController.class);

    @Autowired
    private UserService userService;


    @Autowired
    private Data_setsService data_setsService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Pay_logService pay_logService;

    @Autowired
    private Pay_orderService pay_orderService;


    @Autowired
    private StripeService stripeService;

    @Autowired
    private PlanService planService;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private Pay_SourceService pay_sourceService;

    @Autowired
    private Sub_manageService sub_manageService;

    @Autowired
    private Trans_logService trans_logService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private Coupon_manageService coupon_manageService;

    @Autowired
    private Coupon_userService coupon_userService;

    @Value("${address.email}")
    public String myemail;
    @Value("${address.domain}")
    public String domain;

    @Value("${mailgun.domain}")
    public String mailgun_domain;

    @Value("${mailgun.apikey}")
    public String mailgun_apikey;

    @Value("${strip.apikey}")
    public String strip_apikey;

    @Value("${address.email}")
    public String appEmail;

    /**
     * This is membership purchase rest api.
     * When you purchase the membership on the purchase page ,
     * this api will be called via ajax which is using POST.
     * This api implements to update the membership of current logged in user.
     * As you can see , this function update the membership field of user table.
     * @param mode This is membership type. mode=0 means free membership.
     *             mode=1 means you purchased monthly single membership.
     *             mode=2 means you purchased monthly enterprise membership.
     *             mode=3 menas you purchased yearly single membership.
     *             mode=4 means you purchased yearly enterprise membership.
     * @param session This is session variable for getting user .
     * @return
     */
    @RequestMapping(value = "/account/membership/order",method = RequestMethod.POST)
    public ResultInfo reset_membership(String mode
            , HttpSession session){

        int membership_mode = Integer.parseInt(mode);

        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");

        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("Session invalid.");
            logger.debug( " AccountRestConroller/reset_membership  has session invalid");

        }else{

            logger.debug(userinfo.getUsername() + ": request AccountRestConroller/reset_membership");

            if(membership_mode == 0){ // free membership
                String customer_id = userinfo.getCustomer_id();
                if(customer_id != null){
                    if(!customer_id.equals("")){
                        stripeService.cancelCustomer(customer_id);
                    }
                }

                userinfo.setExpire_date(UtilClass.getExpireDate(Integer.parseInt(mode)));
                userinfo.setMembership(Integer.parseInt(mode));

                if(mode.equals("0")){
                    userinfo.setBalance(0);
                }
                else{
                    Plan plan = planService.getPlanById(Integer.parseInt(mode));
                    userinfo.setBalance(plan.getVr_price());
                }
                userService.updateInfo(userinfo);
                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            }
            else { // subscription membership
                Plan plan = planService.getPlanById(membership_mode);
                Double price = plan.getReal_price();
                int vendor_id = plan.getVendor_id();

                boolean vendorFlag = false;
                List<Sub_manage> sub_manages = sub_manageService.getSubscriptionByUserId(userinfo.getId());
                if(sub_manages != null){
                    for(Sub_manage sub_manage : sub_manages) {
                        if (sub_manage.getVendor_id() == vendor_id) {
                            vendorFlag = true;
                            break;
                        }
                    }
                }
                String customer_id = userinfo.getCustomer_id();
                Pager pager = new Pager();
                pager.setUser_id(Integer.parseInt(userinfo.getId() + ""));

                List<Pay_sources> pay_sourcesList = pay_sourceService.loadPay_Source(pager);
                if(customer_id != null && vendorFlag){
                    if(!customer_id.equals("")){
                        if(pay_sourcesList.size() > 0){
                            resultInfo =  ResultInfoFactory.getErrorResultInfo("customerExist");
                            return resultInfo;

                        }
                    }
                }

                Token new_token = new Token();

                long expire = System.currentTimeMillis();
                expire += (long) 1000 * 60 * 60;

                new_token.setExpire("" + expire);
                new_token.setToken(UtilClass.generateToken());
                tokenService.insertToken(new_token);

                Pay_order pay_order = new Pay_order();
                pay_order.setAmount(price.floatValue());
                pay_order.setDataset_ids("-1");
                pay_order.setMembership_id("" + membership_mode);
                pay_order.setToken(new_token.getToken());

                pay_orderService.insertPayOrder(pay_order);

                resultInfo = ResultInfoFactory.getSuccessResultInfo();
                resultInfo.setObject(pay_order.getId());


            }
        }

        return resultInfo;
    }


    /**
     * Membership change rest api.
     * @param mode
     * @param updown
     * @param session
     * @return
     */
    @RequestMapping(value = "/account/membership/change",method = RequestMethod.POST)
    public ResultInfo changeMembership(String mode, boolean updown, HttpSession session){

        int membership_mode = Integer.parseInt(mode);

        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        List<Sub_manage> sub_manages = sub_manageService.getSubscriptionByUserId(userinfo.getId());
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("Session invalid.");

            logger.debug( "Session invalid");

        }else{

            logger.debug(userinfo.getUsername() + ": request AccountRestConroller/changeMembership");
            String subscription_id ="";
            Sub_manage sub_manage = new Sub_manage();
            Plan plan = planService.getPlanById(membership_mode);
            if(plan == null) plan = new Plan();
            for(Sub_manage sm : sub_manages){
                if(sm.getVendor_id() == plan.getVendor_id()){
                    subscription_id = sm.getSubscription_id();
                    sub_manage = sub_manageService.getSubscriptionById(sm.getId());
                }
            }
            String subscriber_id = stripeService.upgradeSubscription(subscription_id, plan.getPlan_id(), updown, membership_mode == 0);
            if(subscriber_id.indexOf("Error:") >= 0){
                resultInfo =  ResultInfoFactory.getErrorResultInfo(subscriber_id);
                return resultInfo;
            }
            if (updown) {
                sub_manage.setStatus(1);
                sub_manage.setPlan_id(plan.getId());
                sub_manage.setSubscription_id(subscriber_id);
                sub_manage.setBalance(plan.getVr_price());
                sub_manage.setExpiry_date(UtilClass.getExpiryDate(membership_mode));
                sub_manageService.updateSubscription(sub_manage);
                Trans_log trans_log = new Trans_log();
                trans_log.setStatus(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date();
                trans_log.setDate(dateFormat.format(date));
                if(plan.getVendor_id() == -1){
                    trans_log.setTrans_type(1);
                    trans_log.setDescription("Subscription By "+ userinfo.getUsername() +" from vendor with id " + plan.getVendor_id());
                    trans_log.setAmount(plan.getReal_price());
                    trans_log.setVendor_id(plan.getVendor_id());
                    trans_logService.saveTrans(trans_log);
                    userService.updateBalance(plan.getVendor_id(), plan.getReal_price());
                }else { //for vendor other than admin cut fees
                    int fee_percent = feeService.getFeeById(2).getFee_percent();
                    double [] amount = {plan.getReal_price() * (100-fee_percent)/100 , plan.getReal_price() * fee_percent/100};
                    String [] description = {"Subscription By "+ userinfo.getUsername() +" from vendor with id " + plan.getVendor_id(),"Service Charge For DataExo Platform"};
                    int[] vendor_id = {plan.getVendor_id(), -1};
                    int[] trans_type = {1, -1};
                    for (int i = 0; i < amount.length; i++) {
                        trans_log.setDescription(description[i]);
                        trans_log.setAmount(amount[i]);
                        trans_log.setVendor_id(vendor_id[i]);
                        trans_log.setTrans_type(trans_type[i]);
                        trans_logService.saveTrans(trans_log);
                        userService.updateBalance(vendor_id[i], amount[i]);
                    }
                }
            } else {
                sub_manage.setStatus(0);
                sub_manageService.cancelSubscription(sub_manage);
            }

            resultInfo = ResultInfoFactory.getSuccessResultInfo();

        }

        return resultInfo;
    }

    /**
     * Membership continue rest api.
     * @param session
     * @return
     */
    @RequestMapping(value = "/account/membership/continue",method = RequestMethod.POST)
    public ResultInfo continueMembership(String id, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("Session invalid.");

            logger.debug("Session invalid.");


        }else{

            logger.debug(userinfo.getUsername() + ": request AccountRestConroller/continueMembership");


            int plan_id = Integer.parseInt(id);
            Sub_manage sub_manage = sub_manageService.getSubscriptionByUserIdAndPlanId(plan_id, userinfo.getId());
            String subscriptionId = stripeService.continueSubscription(sub_manage.getSubscription_id());
            if(subscriptionId.indexOf("Error:") >= 0){
                resultInfo =  ResultInfoFactory.getErrorResultInfo(subscriptionId);
                return resultInfo;
            }

            sub_manage.setStatus(1);
            sub_manageService.updateSubscription(sub_manage);
            // String customer_id = userinfo.getCustomer_id();
//
//            customer_id = stripeService.continueMembership(customer_id);
//            if(customer_id.equals("error")){
//                resultInfo =  ResultInfoFactory.getErrorResultInfo("error");
//                return resultInfo;
//            }
//            userinfo.setCustomer_id(customer_id);
//            userinfo.setUp_membership(-1);
//
//            userService.updateInfo(userinfo);
            resultInfo = ResultInfoFactory.getSuccessResultInfo();

        }

        return resultInfo;
    }



    /**
     * This is reset profile rest api.
     * When you update the profile on the page ,
     * this api will be called via ajax which is using POST mode.
     * As you can see , you should update username and email in the user table in this function.
     * The ajax is called from js/account.js. You can find ajax function which update the profile.
     * @param reset_user This variable involve username and email address.
     *                   This will be used to update the data of table.
     * @param session
     * @return
     */
    @RequestMapping(value = "/account/profile/api",method = RequestMethod.POST)
    public ResultInfo reset_profile(User reset_user, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("You can't chnage. You are none of this site user.!");

            logger.debug( "AccountRestConroller/reset_profile: You can't chnage. You are none of this site user.!");
        }else{
            User exist = userService.loadUserByUsername(reset_user.getUsername());
            if(exist != null) {
                if (userinfo.getId() != exist.getId()) {
                    resultInfo =  ResultInfoFactory.getErrorResultInfo("Username is already taken. !");

                    logger.debug( "AccountRestConroller/reset_profile: Username is already taken. !");

                    return resultInfo;
                }
            }
            exist = userService.loadUserByEmail(reset_user.getEmail());
            if(exist != null) {
                if (userinfo.getId() != exist.getId()) {
                    resultInfo =  ResultInfoFactory.getErrorResultInfo("Email is already taken. !");
                    logger.debug( "AccountRestConroller/reset_profile: Email is already taken. !");

                    return resultInfo;
                }
            }

            userinfo.setEmail(reset_user.getEmail());
            userinfo.setUsername(reset_user.getUsername());
            userService.updateInfo(userinfo);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Success");

        }
        return resultInfo;
    }

    /**
     * This is password reset rest api.
     * When you update the password on the page ,
     * this api will be called via ajax which is using POST mode.
     * As you can see , you should update password in the user table in this function.
     * The ajax is called from js/account.js. You can find ajax function which update the password.
     *
     * @param password This variable is reset password parameter.
     * @param session
     * @return
     */
    @RequestMapping(value = "/account/password/reset/api",method = RequestMethod.POST)
    public ResultInfo reset_password(String password, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("You already registed user!");



        }else{

            if(session.getAttribute("resetPassword") != null){
                session.removeAttribute("resetPassword");
            }

            User newuser = new User();
            newuser.setUsername(userinfo.getUsername());
            newuser.setId(userinfo.getId());
            newuser.setPassword(Md5Util.pwdDigest(password));
            userService.resetPassword(newuser);
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Success");
        }
        return resultInfo;
    }

    /**
     * This is rest api which check login.
     * When you update the password on the page ,
     * this api will be called via ajax which is using POST mode.
     * Before you update the password , first you should check the current password which you have input on the page.
     * In other words , before update the password , it checks the logged in user with current password.
     * The ajax is called from js/account.js. You can find ajax function which update the password.
     * @param password This is current password to check login.
     * @param session
     * @return
     */
    @RequestMapping(value = "/account/check_login/api",method = RequestMethod.POST)
    public ResultInfo check_login(String password, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("You already registered user!");
        }else{
            if (userinfo.getPassword().equals(Md5Util.pwdDigest(password))){
                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            }else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("Password is incorrect!");
            }
        }
        return resultInfo;
    }

    /**
     * This is rest api for purchase the data set by download mode.
     * When you click download button  on purchase page , this function will be called.
     * When you purchase the data set , it checked current user's balance .
     * Then the data set price must be samller than current balance.
     * After puchase the data sets , it send the email with data set download link.
     * One is the important thing is the download link has expire date.
     * So when the date is expired , the user cann't download.
     * long expire = (long) (1000*3600 * 24 * data_sets.getDownload_expires());
     * this code calc the expire date.
     * expire length will be determine in the admin panel.
     * @param id This is purchase id
     * @param session
     * @return
     */
    @RequestMapping(value = "/download_purchase/data_sets/api",method = RequestMethod.POST)
    public ResultInfo DownPurchaseDatasets(String id, String sId, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("redirect");

            logger.debug( "AccountRestConroller/DownPurchaseDatasets: session user is expired");

        }else{
            Sub_manage sub_manage = sub_manageService.getSubscriptionById(Integer.valueOf(sId));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            Date expired = new Date();
            try {
                expired = dateFormat.parse(sub_manage.getExpiry_date());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date.compareTo(expired) > 1){
                resultInfo =  ResultInfoFactory.getErrorResultInfo("expire");
            }
//
//                long expire = Long.parseLong(userinfo.getExpire_date());
//            long cur = System.currentTimeMillis();
//            if(cur > expire){
//            }
            else{
                Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(id));
                double balance = sub_manage.getBalance() - data_sets.getDownload_price();
                if(balance >= 0){
                    sub_manage.setBalance(balance);
                    sub_manageService.updateSubscription(sub_manage);
//                    userinfo.setBalance(balance);
//                    userService.updateInfo(userinfo);
                }
                session.setAttribute("user",userinfo);
                resultInfo = ResultInfoFactory.getSuccessResultInfo();

                Purchase purchase = new Purchase();
                purchase.setUserid((int) userinfo.getId());
                purchase.setDataset_id(Integer.parseInt(id));
                String token = UUID.randomUUID().toString().toLowerCase();
                purchase.setToken(token);
                purchase.setOrder_date(""+System.currentTimeMillis());
                purchaseService.insertPurchase(purchase);

                purchase = purchaseService.selectPurchase(purchase);

                Token tokenmodel = new Token();
                tokenmodel.setToken(token);
                long current = System.currentTimeMillis();

                long expire = (long)1000*3600 * 24 * data_sets.getDownload_expires() ;
                current += expire;
                tokenmodel.setExpire("" +current);
                tokenService.insertToken(tokenmodel);

                String subject = "You can download data set from this link.";
                String url = domain + "/api/v3/dataset_download/"+purchase.getId() + "/" + token;

                String html = "<html>" +
                        "<head>" +
                        "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                        "<meta name='format-detection' content='telephone=no' /> " +
                        "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                        "<style type='text/css'>" +
                        "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                        "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                        "table{border-collapse:collapse;}" +
                        "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                        "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                        "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                        "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                        "" +
                        ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                        ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                        "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                        "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                        "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                        "" +
                        "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                        "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                        "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                        "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                        ".flexibleImage{height:auto;}" +
                        ".linkRemoveBorder{border-bottom:0 !important;}" +
                        "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                        "" +
                        "body, #bodyTable{background-color:#E1E1E1;}" +
                        "#emailHeader{background-color:#E1E1E1;}" +
                        "#emailBody{background-color:#FFFFFF;}" +
                        "#emailFooter{background-color:#E1E1E1;}" +
                        ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                        ".emailButton{background-color:#205478; border-collapse:separate;}" +
                        ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                        ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                        ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                        ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                        ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                        ".imageContentText {margin-top: 10px;line-height:0;}" +
                        ".imageContentText a {line-height:0;}" +
                        "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                        "" +
                        "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                        "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                        "" +
                        ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                        ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                        "" +
                        "" +
                        "@media only screen and (max-width: 480px){" +
                        "body{width:100% !important; min-width:100% !important;} " +
                        "" +
                        "table[id='emailHeader']," +
                        "table[id='emailBody']," +
                        "table[id='emailFooter']," +
                        "table[class='flexibleContainer']," +
                        "td[class='flexibleContainerCell'] {width:100% !important;}" +
                        "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                        "" +
                        "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                        "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                        "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                        "" +
                        "" +
                        "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                        "" +
                        "table[class='emailButton']{width:100% !important;}" +
                        "td[class='buttonContent']{padding:0 !important;}" +
                        "td[class='buttonContent'] a{padding:15px !important;}" +
                        "" +
                        "}" +
                        "" +
                        "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                        "}" +
                        "" +
                        "@media only screen and (-webkit-device-pixel-ratio:1){" +
                        "}" +
                        "" +
                        "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                        "}" +
                        "" +
                        "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                        "" +
                        "}" +
                        "</style>" +
                        "" +
                        "</head>" +
                        "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                        "" +
                        "<center style='background-color:#E1E1E1;'>" +
                        "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                        "<tr>" +
                        "<td align='center' valign='top' id='bodyCell'>" +
                        "" +
                        "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                        "<tr style='height:50px;'>" +
                        "<tr>" +
                        "" +
                        "</table>" +
                        "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                        "" +
                        "<tr>" +
                        "<td align='center' valign='top'>" +
                        "" +
                        "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                        "<tr>" +
                        "<td align='center' valign='top'>" +
                        "" +
                        "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                        "<tr>" +
                        "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                        "" +
                        "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                        "<tr>" +
                        "<td align='center' valign='top' class='textContent'>" +
                        "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Download Information</h1>" +
                        "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>This is your download information.</h2>" +
                        "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>You can download data sets anytime which you have bought.</div>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "" +
                        "<tr>" +
                        "<td align='center' valign='top' style=' padding: 2px;'>" +
                        "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                        "<tr style='padding-top:0;'>" +
                        "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                        data_sets.getName() +
                        "</td>" +
                        "<td style='vertical-align: middle;    padding: 0px 10px;'>"+data_sets.getDownload_price()+"$</td>" +
                        "<td style='vertical-align: middle;' align='center' valign='top'>" +
                        "<a href='"+url+"'>Download</a>" +
                        "</td>" +
                        "</tr>" +
                        "" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "" +
                        "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                        "" +
                        "<tr>" +
                        "<td align='center' valign='top'>" +
                        "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                        "<tr>" +
                        "<td align='center' valign='top'>" +
                        "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                        "<tr>" +
                        "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                        "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                        "<tr>" +
                        "<td valign='top' bgcolor='#E1E1E1'>" +
                        "" +
                        "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                        "<div>Copyright &#169; 2017. All&nbsp;rights&nbsp;reserved.</div>" +
                        "" +
                        "</div>" +
                        "" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "" +
                        "</table>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</center>" +
                        "</body>" +
                        "</html>";

                // this is for mailgun email service
                // UtilClass.sendMail(myemail, userinfo.getEmail() , html , subject, mailgun_domain, mailgun_apikey);
                //thirdPartyService.sendAwsSes(appEmail, userinfo.getEmail() , subject , html);
            }
        }
        return resultInfo;
    }


    /**
     * This is rest api for implement page navigation.
     * @param pager
     * @param session
     * @return
     */
    @RequestMapping("/pager/purhcase/load")
    public Pager loadData_sets_seriesPager(Pager<Purchase> pager, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        if(userinfo != null){
            pager.setCateid(userinfo.getId() + "");
            if(pager.getSearch_str() == null) {
                pager.setSearch_str("");
            }
            purchaseService.initPage(pager);
        }

        return pager;
    }


    /**
     * This is rest api for purchase the data set by one time mode.
     * You have to pay every time to purchase this mode.
     * The download link will be sent via email after payment.
     * @param id This is purchase id.
     * @param session
     * @return
     */
    //todo coupon cart
    @RequestMapping(value = "/add_cart/data_sets/api",method = RequestMethod.POST)
    public ResultInfo AddCartDatasets(String id, String coupon, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(id));
            Bucket bucket= new Bucket();
            bucket.setUserid(0);
            bucket.setDataset_id(Integer.parseInt(id));
            bucket.setOrder_date(""+System.currentTimeMillis());
            bucket.setSession_id(session.getId());
            bucketService.insertBucket(bucket);

            Pager pager = new Pager();
            pager.setSearch_str(session.getId());
            pager.setCateid("-1");
            bucketService.initPage(pager);
            resultInfo = ResultInfoFactory.getSuccessResultInfo(""+pager.getTotalCount());

        }else{
            Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(id));

            Bucket bucket= new Bucket();
            bucket.setUserid((int) userinfo.getId());
            bucket.setDataset_id(Integer.parseInt(id));
            bucket.setSession_id(session.getId());
            bucket.setOrder_date(""+System.currentTimeMillis());
            bucketService.insertBucket(bucket);

            Pager pager = new Pager();
            pager.setSearch_str("");
            pager.setCateid(userinfo.getId() + "");
            bucketService.initPage(pager);

            resultInfo = ResultInfoFactory.getSuccessResultInfo(""+pager.getTotalCount());

            // for coupon apply to cart
            if(!coupon.equalsIgnoreCase("coupon")){
                Coupon_manage coupon_manage = coupon_manageService.getByCoupon(coupon);

                pager.setUser_id((int)userinfo.getId());
                pager.setCoupon_id(coupon_manage.getId());
                List<Coupon_user> coupon_users = coupon_userService.getByCouponAndUserId(pager);
                boolean usedFlag = false;
                if(coupon_users != null){
                    for(Coupon_user coupon_user : coupon_users){
                        if(coupon_user.getOrder_id() < 0 && coupon_user.getUsed_flag() < 0){
                            resultInfo.setErrorInfo("cart");
                            usedFlag = true;
                            break;
                        }
                    }
                }
                if(!usedFlag) {
                    Coupon_user coupon_user = new Coupon_user();
                    coupon_user.setUsed_flag(-1);
                    coupon_user.setOrder_id(-1 * (long) bucket.getId());
                    coupon_user.setUser_id(userinfo.getId());
                    coupon_user.setCoupon_id(coupon_manage.getId());
                    coupon_userService.saveCoupon_user(coupon_user);
                }
            }
        }
        return resultInfo;
    }

    /**
     * This is rest api to checkout selected items.
     * In this function , the items in bucket table move to purchase table.
     * @param tokenid
     * @param price
     * @param session
     * @return
     */
    @RequestMapping(value = "/add_cart/data_sets/checkout/api",method = RequestMethod.POST)
    public ResultInfo CheckoutDatasets(String tokenid, Integer price, HttpSession session){
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo==null || userinfo.getId() == 0 || userinfo.getId() == -1){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("redirect");
        }else{


            Pager pager = new Pager();
            pager.setSearch_str("");
            pager.setCateid(userinfo.getId() + "");
            bucketService.initPage(pager);
            pager.setStart(0);
            pager.setLimit(pager.getTotalCount());

            List<Bucket> bucketList = null;

            String subject =  "You can download data set from this link.";

            //   String html = "<html><body><span style='color:red;'>"+url+"</span></body></html>";

            String html_items = "";
            long sum = 0;
            if(pager.getTotalCount() > 0) {
                bucketList = bucketService.loadBucket(pager);

                for (int i = 0; i < bucketList.size(); i++) {
                    Bucket bucket = bucketList.get(i);
                    Purchase purchase = new Purchase();
                    purchase.setUserid(bucket.getUserid());
                    purchase.setDataset_id(bucket.getDataset_id());

                    String token = UUID.randomUUID().toString().toLowerCase();
                    purchase.setToken(token);
                    purchase.setOrder_date("" + System.currentTimeMillis());

                    purchaseService.insertPurchase(purchase);

                    purchase = purchaseService.selectPurchase(purchase);

                    Data_sets data_sets = data_setsService.getData_setsById(bucket.getDataset_id());
                    Token tokenmodel = new Token();
                    tokenmodel.setToken(token);

                    long current = System.currentTimeMillis();

                    long expire = (long)1000*3600 * 24 * data_sets.getOnetime_expires() ;
                    current += expire;
                    tokenmodel.setExpire("" +current);

                    tokenService.insertToken(tokenmodel);

                    bucketService.deleteBucket(bucket);



                    sum += data_sets.getOnetime_price();
                    String url = domain + "/api/v3/dataset_download/"+purchase.getId() + "/" + token;

                    html_items += "<tr>" +
                            "<td align='center' valign='top' style='    border-bottom: 3px solid #c1bfbf;padding: 2px;'>" +
                            "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                            "<tr style='padding-top:0;'>" +
                            "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                            data_sets.getName() +
                            "</td>" +
                            "<td style='vertical-align: middle;    padding: 0px 10px;'>"+data_sets.getOnetime_price()+"$</td>" +
                            "<td style='vertical-align: middle;' align='center' valign='top'>" +
                            "<a href='"+url+"'>Download</a>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</td>" +
                            "</tr>";

                }
            }

            Pay_log pay_log = new Pay_log();
            pay_log.setAmount(sum);
            pay_log.setTime( "" + System.currentTimeMillis());
            pay_log.setDescription("This is checkout payment history. You have paid $"+sum);

            pay_log.setUserid(userinfo.getId() + "");
            pay_log.setDataset_name("This is payment for dataset");
            pay_log.setDataset_id(1);
            if(price != 0){
                pay_logService.savePay_log(pay_log);
            }

            html_items += "<tr>" +
                    "<td align='center' valign='top' style=' padding: 2px;'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                    "<tr style='padding-top:0;'>" +
                    "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                    "Total Price :" +
                    "</td>" +
                    "<td style='vertical-align: middle;    padding: 0px 10px;'>"+sum+"$</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>";

            String html = "<html>" +
                    "<head>" +
                    "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<meta http-equiv='X-UA-Compatible' content='IE=edge,chrome=1'>" +
                    "<meta name='format-detection' content='telephone=no' /> " +
                    "<title>Respmail is a response HTML email designed to work on all major email platforms and smartphones</title>" +
                    "<style type='text/css'>" +
                    "html { background-color:#E1E1E1; margin:0; padding:0; }" +
                    "body, #bodyTable, #bodyCell, #bodyCell{height:100% !important; margin:0; padding:0; width:100% !important;font-family:Helvetica, Arial, 'Lucida Grande', sans-serif;}" +
                    "table{border-collapse:collapse;}" +
                    "table[id=bodyTable] {width:100%!important;margin:auto;max-width:500px!important;color:#7A7A7A;font-weight:normal;}" +
                    "img, a img{border:0; outline:none; text-decoration:none;height:auto; line-height:100%;}" +
                    "a {text-decoration:none !important;border-bottom: 1px solid;}" +
                    "h1, h2, h3, h4, h5, h6{color:#5F5F5F; font-weight:normal; font-family:Helvetica; font-size:20px; line-height:125%; text-align:Left; letter-spacing:normal;margin-top:0;margin-right:0;margin-bottom:10px;margin-left:0;padding-top:0;padding-bottom:0;padding-left:0;padding-right:0;}" +
                    "" +
                    ".ReadMsgBody{width:100%;} .ExternalClass{width:100%;}" +
                    ".ExternalClass, .ExternalClass p, .ExternalClass span, .ExternalClass font, .ExternalClass td, .ExternalClass div{line-height:100%;}" +
                    "table, td{mso-table-lspace:0pt; mso-table-rspace:0pt;} " +
                    "img{-ms-interpolation-mode: bicubic;display:block;outline:none; text-decoration:none;} " +
                    "body, table, td, p, a, li, blockquote{-ms-text-size-adjust:100%; -webkit-text-size-adjust:100%; font-weight:normal!important;}" +
                    "" +
                    "h1{display:block;font-size:26px;font-style:normal;font-weight:normal;line-height:100%;}" +
                    "h2{display:block;font-size:20px;font-style:normal;font-weight:normal;line-height:120%;}" +
                    "h3{display:block;font-size:17px;font-style:normal;font-weight:normal;line-height:110%;}" +
                    "h4{display:block;font-size:18px;font-style:italic;font-weight:normal;line-height:100%;}" +
                    ".flexibleImage{height:auto;}" +
                    ".linkRemoveBorder{border-bottom:0 !important;}" +
                    "table[class=flexibleContainerCellDivider] {padding-bottom:0 !important;padding-top:0 !important;}" +
                    "" +
                    "body, #bodyTable{background-color:#E1E1E1;}" +
                    "#emailHeader{background-color:#E1E1E1;}" +
                    "#emailBody{background-color:#FFFFFF;}" +
                    "#emailFooter{background-color:#E1E1E1;}" +
                    ".nestedContainer{background-color:#F8F8F8; border:1px solid #CCCCCC;}" +
                    ".emailButton{background-color:#205478; border-collapse:separate;}" +
                    ".buttonContent{color:#FFFFFF; font-family:Helvetica; font-size:18px; font-weight:bold; line-height:100%; padding:15px; text-align:center;}" +
                    ".buttonContent a{color:#FFFFFF; display:block; text-decoration:none!important; border:0!important;}" +
                    ".emailCalendar{background-color:#FFFFFF; border:1px solid #CCCCCC;}" +
                    ".emailCalendarMonth{background-color:#205478; color:#FFFFFF; font-family:Helvetica, Arial, sans-serif; font-size:16px; font-weight:bold; padding-top:10px; padding-bottom:10px; text-align:center;}" +
                    ".emailCalendarDay{color:#205478; font-family:Helvetica, Arial, sans-serif; font-size:60px; font-weight:bold; line-height:100%; padding-top:20px; padding-bottom:20px; text-align:center;}" +
                    ".imageContentText {margin-top: 10px;line-height:0;}" +
                    ".imageContentText a {line-height:0;}" +
                    "#invisibleIntroduction {display:none !important;} /* Removing the introduction text from the view */" +
                    "" +
                    "span[class=ios-color-hack2] a {color:#205478!important;text-decoration:none!important;}" +
                    "span[class=ios-color-hack3] a {color:#8B8B8B!important;text-decoration:none!important;}" +
                    "" +
                    ".a[href^='tel'], a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:none!important;cursor:default!important;}" +
                    ".mobile_link a[href^='tel'], .mobile_link a[href^='sms'] {text-decoration:none!important;color:#606060!important;pointer-events:auto!important;cursor:default!important;}" +
                    "" +
                    "" +
                    "@media only screen and (max-width: 480px){" +
                    "body{width:100% !important; min-width:100% !important;} " +
                    "" +
                    "table[id='emailHeader']," +
                    "table[id='emailBody']," +
                    "table[id='emailFooter']," +
                    "table[class='flexibleContainer']," +
                    "td[class='flexibleContainerCell'] {width:100% !important;}" +
                    "td[class='flexibleContainerBox'], td[class='flexibleContainerBox'] table {display: block;width: 100%;text-align: left;}" +
                    "" +
                    "td[class='imageContent'] img {height:auto !important; width:100% !important; max-width:100% !important; }" +
                    "img[class='flexibleImage']{height:auto !important; width:100% !important;max-width:100% !important;}" +
                    "img[class='flexibleImageSmall']{height:auto !important; width:auto !important;}" +
                    "" +
                    "" +
                    "table[class='flexibleContainerBoxNext']{padding-top: 10px !important;}" +
                    "" +
                    "table[class='emailButton']{width:100% !important;}" +
                    "td[class='buttonContent']{padding:0 !important;}" +
                    "td[class='buttonContent'] a{padding:15px !important;}" +
                    "" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:.75){" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:1){" +
                    "}" +
                    "" +
                    "@media only screen and (-webkit-device-pixel-ratio:1.5){" +
                    "}" +
                    "" +
                    "@media only screen and (min-device-width : 320px) and (max-device-width:568px) {" +
                    "}" +
                    "</style>" +
                    "" +
                    "</head>" +
                    "<body bgcolor='#E1E1E1' leftmargin='0' marginwidth='0' topmargin='0' marginheight='0' offset='0'>" +
                    "<center style='background-color:#E1E1E1;'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' height='100%' width='100%' id='bodyTable' style='table-layout: fixed;max-width:100% !important;width: 100% !important;min-width: 100% !important;'>" +
                    "<tr>" +
                    "<td align='center' valign='top' id='bodyCell'>" +
                    "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailHeader'>" +
                    "<tr style='height:10px;'>" +
                    "<tr>" +
                    "</table>" +
                    "<table bgcolor='#FFFFFF'  border='0' cellpadding='0' cellspacing='0' width='500' id='emailBody'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%' style='color:#FFFFFF;' bgcolor='#3498db'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                    "<tr>" +
                    "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                    "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td align='center' valign='top' class='textContent'>" +
                    "<h1 style='color:#FFFFFF;line-height:100%;font-family:Helvetica,Arial,sans-serif;font-size:35px;font-weight:normal;margin-bottom:5px;text-align:center;'>DataExo Bill Information</h1>" +
                    "<h2 style='text-align:center;font-weight:normal;font-family:Helvetica,Arial,sans-serif;font-size:23px;margin-bottom:10px;color:#205478;line-height:135%;'>This is your shopping bill information.</h2>" +
                    "<div style='text-align:center;font-family:Helvetica,Arial,sans-serif;font-size:15px;margin-bottom:0;color:#FFFFFF;line-height:135%;'>You can download data sets anytime which you have bought.</div>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>";

            html += html_items;
            html += "</table>" +
                    "" +
                    "<table bgcolor='#E1E1E1' border='0' cellpadding='0' cellspacing='0' width='500' id='emailFooter'>" +
                    "" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td align='center' valign='top'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='500' class='flexibleContainer'>" +
                    "<tr>" +
                    "<td align='center' valign='top' width='500' class='flexibleContainerCell'>" +
                    "<table border='0' cellpadding='30' cellspacing='0' width='100%'>" +
                    "<tr>" +
                    "<td valign='top' bgcolor='#E1E1E1'>" +
                    "" +
                    "<div style='font-family:Helvetica,Arial,sans-serif;font-size:13px;color:#828282;text-align:center;line-height:120%;'>" +
                    "<div>Copyright &#169; 2017. All&nbsp;rights&nbsp;reserved.</div>" +
                    "" +
                    "</div>" +
                    "" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</center>" +
                    "</body>" +
                    "</html>";

            // mailgun email service
            // UtilClass.sendMail(myemail, userinfo.getEmail() , html , subject, mailgun_domain, mailgun_apikey);

            thirdPartyService.sendAwsSes(appEmail, userinfo.getEmail() , subject , html);
            resultInfo = ResultInfoFactory.getSuccessResultInfo();

        }

        return resultInfo;
    }

    /**
     * This is api implements to delete item from bucket table.
     * @param id
     * @return
     */
    //todo set used flag to 0 if cart item is deleted
    @RequestMapping(value = "/add_cart/data_sets/delete/api",method = RequestMethod.POST)
    public ResultInfo DeleteBucket(String id){
        ResultInfo resultInfo = null;
        Bucket bucket = new Bucket();
        bucket.setId(Integer.parseInt(id));
        bucketService.deleteBucket(bucket);
        Coupon_user coupon_user = coupon_userService.getByOrderId(-1* (long)Integer.parseInt(id));
        if(coupon_user != null){
            if(coupon_user.getUsed_flag()<0) {
                coupon_user.setUsed_flag(0);
                coupon_userService.updateCoupon_user(coupon_user);
            }
        }
        resultInfo = ResultInfoFactory.getSuccessResultInfo();

        return resultInfo;
    }


    /**
     * This is  api to get all counts of payment logs.
     * This api should be called in profile paga.
     *
     * @param session
     * @return
     */
    @RequestMapping("/pager/payment/load")
    public Pager loadPaymentPager( Pager pager,HttpSession session) {

        User userinfo = (User) session.getAttribute("user");
        if(userinfo != null) {
            pager.setCateid(userinfo.getId() + "");
            pager.setSearch_str("");
            pay_logService.initPage(pager);
        }
        return pager;
    }


    @RequestMapping(value = "/account/datasets/available",method = RequestMethod.POST)
    public ResultInfo datasetsAvailable(HttpSession session
            , HttpServletRequest request
            , HttpServletResponse response){
        ResultInfo resultInfo = null;

        String apiKey = request.getParameter("apikey");
        String schema = request.getParameter("schema");
        if(apiKey == null || schema == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("paramerror");
            resultInfo.setObject(10);
            return resultInfo;
        }

        if(apiKey.equals("") || schema.equals("") || schema.indexOf(".") < 0){
            resultInfo = ResultInfoFactory.getErrorResultInfo("paramerror");
            resultInfo.setObject(10);
            return resultInfo;
        }

        String[] schemaAr = schema.split("\\.");
        Data_sets data_sets = new Data_sets();

        if(schemaAr.length <2 ){
            resultInfo = ResultInfoFactory.getErrorResultInfo("paramerror");
            resultInfo.setObject(10);
            return resultInfo;
        }

        data_sets.setSchema_name(schemaAr[0]);
        data_sets.setTable_name(schemaAr[1]);

        //logger.debug(data_sets.getSchema_name() + ",,,," + data_sets.getTable_name());

        data_sets = data_setsService.getData_setsBySchema(data_sets);

        User user = userService.loadUserByApiKey(apiKey);

        if(user == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("noexist");

            logger.debug("noexist");

            resultInfo.setObject(data_sets.getLimitation());
            return resultInfo;
        }


        if(data_sets == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("nodatasets");
            resultInfo.setObject(10);
            return resultInfo;
        }

        // vendor will be success in this case
        if(user.getVendor_id() > 0){
            if(data_sets.getVendor_id() == user.getVendor_id()){
                resultInfo = ResultInfoFactory.getSuccessResultInfo();

                resultInfo.setObject("-10"); // this mean it should show all data
                return resultInfo;
            }
        }

        //////////////////
        if(data_sets.getPrice_model_id() == 1){
            resultInfo = ResultInfoFactory.getErrorResultInfo("free");
            resultInfo.setObject("-10"); // this mean it should show all data
            return resultInfo;
        }
        Purchase purchase = new Purchase();
        purchase.setId(0);
        purchase.setToken("");
        purchase.setDataset_id(data_sets.getId());
        purchase.setUserid(Integer.parseInt("" + user.getId()));

        purchase = purchaseService.selectPurchase(purchase);
        if(purchase == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("nopurchase");
            resultInfo.setObject(data_sets.getLimitation());
            return resultInfo;
        }


        resultInfo = ResultInfoFactory.getSuccessResultInfo();

        resultInfo.setObject("-10"); // this mean it should show all data
        return resultInfo;
    }


}
