package com.dataexo.zblog.controller.rest;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.*;
import com.dataexo.zblog.vo.*;
import com.dataexo.zblog.vo.Plan;
import com.dataexo.zblog.vo.Token;
import com.dataexo.zblog.vo.resources.instances.MessageInstance;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This controller is for the user login functions.
 * Create time: 6/22
 * Created By:
 */
@RestController
public class UserRestController {

    private static final Logger logger = Logger.getLogger(UserRestController.class);

    @Value("${captcha.sitekey}")
    public String captcha_sitekey;


    @Value("${captcha.secretkey}")
    public String captcha_secretkey;


    @Value("${address.email}")
    public String contact_email;

    @Value("${address.admin_email}")
    public String admin_email;


    @Value("${address.domain}")
    public String domain;

    @Value("${mailgun.domain}")
    public String mailgun_domain;

    @Value("${mailgun.apikey}")
    public String mailgun_apikey;

    @Value("${strip.apikey}")
    public String stripApiKey;

    @Value("${token.auth.timeout}")
    public int tokenAuthTimeout;

    @Value("${token.mail.timeout}")
    public int tokenMailTimeout;

    @Value("${address.email}")
    public String appEmail;


    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private Data_setsService data_setsService;

    @Autowired
    private Pay_orderService pay_orderService;

    @Autowired
    private Pay_logService pay_logService;

    @Autowired
    private PurchaseService purchaseService;


    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private Contact_usService contact_usService;


    @Autowired
    private PlanService planService;


    @Autowired
    private StripeService stripeService;

    @Autowired
    private Pay_SourceService pay_sourceService;

    @Autowired
    private Trans_logService trans_logService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private Sub_manageService sub_manageService;

    @Autowired
    private Coupon_manageService coupon_manageService;

    @Autowired
    private Coupon_userService coupon_userService;

    @Autowired
    private InboxNotifyService inboxNotifyService;

    @Autowired
    private VendorService vendorService;


    /**
     * The user login authurization rest api.
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/sso/login/auth", method = RequestMethod.POST)
    public ResultInfo loginAuth(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("token") String token
    ) {
        ResultInfo resultInfo = null;
        /*Token tokenModel = tokenService.getByToken(token);
        if(tokenModel == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("Your token doesn't exist!");
            return resultInfo;
        }
        long current_time = System.currentTimeMillis();
        long diff = current_time - Long.parseLong(tokenModel.getExpire());

        if (diff > 0) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Your token is expired!");
        }
        else {*/

        User userInfo = userService.loadUserByUsername(username);
        if (userInfo == null) {

            userInfo = userService.loadUserByEmail(username);
            if (userInfo == null) {

                resultInfo = ResultInfoFactory.getErrorResultInfo("Username or email doesn't exist.");
            } else {
                if (userInfo.getActivate() == 0) {

                    resultInfo = ResultInfoFactory.getErrorResultInfo("Your account doesn't activate yet.");
                } else {
                    if (userInfo.getPassword().equals(Md5Util.pwdDigest(password))) {

                        resultInfo = ResultInfoFactory.getSuccessResultInfo();

                        resultInfo.setObject(userInfo);
                    } else {

                        resultInfo = ResultInfoFactory.getErrorResultInfo("password is incorrect");
                    }
                }
            }
        } else {
            if (userInfo.getActivate() == 0) {

                resultInfo = ResultInfoFactory.getErrorResultInfo("Your account doesn't activate yet.");
            } else {
                if (userInfo.getPassword().equals(Md5Util.pwdDigest(password))) {

                    resultInfo = ResultInfoFactory.getSuccessResultInfo();
                    resultInfo.setObject(userInfo);
                } else {

                    resultInfo = ResultInfoFactory.getErrorResultInfo("Password is incorrect");
                }
            }
        }
        // }

        return resultInfo;
    }

    @RequestMapping(value = "/user/activate", method = RequestMethod.POST)
    public ResultInfo activateAccount(@RequestParam("userid") String userid) {
        ResultInfo resultInfo = null;

//        User user = userService.loadUserById(Long.parseLong(id));
        userService.activeAccount(Integer.parseInt(userid));
        resultInfo = ResultInfoFactory.getSuccessResultInfo();

        return resultInfo;
    }

    /**
     * The user sign up function
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/sso/signup/auth", method = RequestMethod.POST)
    public ResultInfo signupAuth(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String upassword,
            @RequestParam("token") String token) {

        ResultInfo resultInfo = null;

        Token tokenModel = tokenService.getByToken(token);

        long current_time = System.currentTimeMillis();
        long diff = current_time - Long.parseLong(tokenModel.getExpire());

        if (diff > 0) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Your token is expired!");
        } else {

            User userInfo = userService.loadUserByUsername(username);
            if (userInfo == null) {

                userInfo = userService.loadUserByEmail(email);
                if (userInfo == null) {

                    //////////// Create Stripe customer ////////////////////
                    Customer customer = stripeService.createCustomer(email);
                    if (customer == null) {

                        resultInfo = ResultInfoFactory.getErrorResultInfo("stripe gateway has some problem");
                        return resultInfo;
                    }

                    String password = Md5Util.pwdDigest(upassword);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    User user = new User();
                    user.setCustomer_id(customer.getId());

                    user.setUsername(username);
                    user.setEmail(email);

                    user.setCreateTime(dateFormat.format(date));
                    user.setPassword(password);

                    String tokenVal = UUID.randomUUID().toString().toLowerCase();
                    user.setToken(tokenVal);
                    user.setApiKey(UtilClass.generateAPIKey());
                    userService.insertUserInfo(user);

                    userInfo = userService.loadUserByEmail(user.getEmail());

                    thirdPartyService.addNewsToSystem(userInfo); // add to jforum project

                    String subject = "You have to activate your account.";

                    Token new_token = new Token();
                    tokenVal = UUID.randomUUID().toString().toLowerCase();

                    new_token.setToken(tokenVal);
                    long expire = System.currentTimeMillis();
                    expire += (long) 1000 * 60 * 10;

                    new_token.setExpire("" + expire);
                    tokenService.insertToken(new_token);

                    String html = UtilClass.activateHtmlTempl(domain, userInfo, tokenVal);

                    // mailgun email service
                    // MessageInstance res = UtilClass.sendMail(myemail, userInfo.getEmail(), html, subject, mailgun_domain, mailgun_apikey);
                    thirdPartyService.sendAwsSes(appEmail, userInfo.getEmail(), subject, html);

                    resultInfo = ResultInfoFactory.getSuccessResultInfo();
                    resultInfo.setObject(userInfo);

                } else {
                    resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
                }
            } else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
            }
        }
        return resultInfo;
    }

    /**
     * The user's account reset function
     *
     * @param
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/send_email/api", method = RequestMethod.POST)
    public ResultInfo resetPassword(@RequestParam("email") String email,
                                    @RequestParam("token") String token,
                                    HttpSession session) {

        ResultInfo resultInfo = null;

        Token tokenModel = tokenService.getByToken(token);

        long current_time = System.currentTimeMillis();
        long diff = current_time - Long.parseLong(tokenModel.getExpire());

        if (diff > 0) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Your token is expired!");
        } else {
            User userInfo = userService.loadUserByEmail(email);
            if (userInfo != null) {
                token = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
                long expire = System.currentTimeMillis() + 1000 * 60 * 15;
                Token token_class = new Token();
                token_class.setExpire("" + expire);
                token_class.setToken(token);
                tokenService.insertToken(token_class);

                String subject = "Please visit this site to reset your password";
                String url = domain + "/ForgotPassword/newPassword/" + userInfo.getId() + "/" + token;
                String html = UtilClass.getResetPasswordHTML(url);

                // mailgun email service
                // MessageInstance res = UtilClass.sendMail(myemail, email, html, subject, mailgun_domain, mailgun_apikey);

                thirdPartyService.sendAwsSes(appEmail, userInfo.getEmail(), subject, html);

                resultInfo = ResultInfoFactory.getSuccessResultInfo();

            } else {

                resultInfo = ResultInfoFactory.getErrorResultInfo("Email doesn't exists!");
            }
        }

        return resultInfo;
    }


    /**
     * Reset the Password Rest Api
     *
     * @param user    : it involves email , token parameters
     * @param session
     * @return resultInfo
     */

    @RequestMapping(value = "/user/resetPassword/api", method = RequestMethod.POST)
    public ResultInfo resetPasswordAuth(User user, HttpSession session) {

        ResultInfo resultInfo = null;

        Token token_class = tokenService.getByToken(user.getToken());
        long nowtime = System.currentTimeMillis();
        if (token_class == null) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Your reset password token has expired!");
        } else {
            long expire = Long.parseLong(token_class.getExpire());
            if (nowtime > expire) {
                resultInfo = ResultInfoFactory.getErrorResultInfo("Your reset password token has expired!");
            } else {
                User newuser = userService.loadUserByEmail(user.getEmail());

                String password = Md5Util.pwdDigest(user.getPassword());
                newuser.setPassword(password);
                userService.resetPassword(newuser);

                session.setAttribute("user",newuser);
                resultInfo = ResultInfoFactory.getSuccessResultInfo("Reset password success!!!");
            }
        }
        return resultInfo;
    }

    /**
     * set the user's favourite
     *
     * @param series  : favourite data-sets-series
     * @param session
     * @return resultInfo
     */

    @RequestMapping("/user/favourite/api")
    public ResultInfo setFavourite(Data_sets_series series, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        ResultInfo resultInfo = null;
        if (userinfo == null) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("You should login again!");
        } else {
            MyFavourite favourite = new MyFavourite();
            favourite.setUserid(userinfo.getId());
            favourite.setSeries_id(series.getId());

            MyFavourite flag = favouriteService.getByOne(favourite);
            if (flag == null) {
                favouriteService.insertFavourite(favourite);
            } else {
                favouriteService.deleteFavourite(favourite);
            }
            resultInfo = ResultInfoFactory.getSuccessResultInfo();

        }
        return resultInfo;
    }

    /**
     * This is rest api which unlike my favourite data set.
     * When you unlike the data set on the page ,
     * this api will be called via ajax which is using POST mode.
     * when you click unlike button , this function will be called.
     * The ajax is called from js/favourite.js.
     *
     * @param id      This variable is data set id which you are going to unlike.
     *                You can update the data using this id.
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/favourite/unlike/api", method = RequestMethod.POST)
    public ResultInfo unlike(String id, HttpSession session) {
        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo != null || userinfo.getId() == 0 || userinfo.getId() == -1) {
            MyFavourite favourite = new MyFavourite();
            favourite.setId(Long.parseLong(id));

            favouriteService.deleteFavourite(favourite);
        }

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    /**
     * This is contact us page api.
     * When the user sent the message to admin  , this api will be called.
     * It is called from ajax via POST method.
     * The data send using json format.
     *
     * @param username: This is sender name
     * @param email     : This is sender email address
     * @param content   : This is message content
     * @return : ResultInfo
     */
    @RequestMapping(value = "/user/sendmessage")
    public ResultInfo sendmessage(@RequestParam("g-recaptcha-response") String captcha,

                                  @RequestParam("name") String username,
                                  @RequestParam("email") String email,
                                  @RequestParam("content") String content, HttpServletRequest request) {
        ResultInfo resultInfo = null;

        Boolean captcha_result = false;

        String userAgent = request.getHeader("User-Agent");
        try {
            captcha_result = VerifyCaptcha.verify(captcha, userAgent, captcha_secretkey);
        } catch (IOException e) {
            e.printStackTrace();
            captcha_result = false;

            logger.error("Captcha error: ", e);

        }

        if (!captcha_result) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Captch Error!");
        } else {

            Contact_us contact_us = new Contact_us();
            contact_us.setUser_name(username);
            contact_us.setEmail(email);
            contact_us.setMessage(content);
            contact_us.setSubmit_timestamp("" + System.currentTimeMillis());

            contact_usService.saveContact_us(contact_us);

            String subject = "The DataEXO sent you email.";
            String html = UtilClass.ThanksReplyHtmlToUser(username);

            // send thanks mail to user
            //MessageInstance msg = UtilClass.sendMail(email, contact_email, html, subject, mailgun_domain, mailgun_apikey);
            thirdPartyService.sendAwsSes(contact_email, email, subject, html);

            // send notification to admin .
            html = UtilClass.ContactAdminHtmlToUser(username, content);
            thirdPartyService.sendAwsSes(admin_email, contact_email, subject, html);

            resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        }
        return resultInfo;
    }

    @RequestMapping(value = "/user/checklogin")
    public ResultInfo checklogin(HttpSession session) {
        ResultInfo resultInfo = null;
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resultInfo = ResultInfoFactory.getErrorResultInfo();
        } else {
            resultInfo = ResultInfoFactory.getSuccessResultInfo();
        }
        return resultInfo;
    }


    /**
     * The user sign up function
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/ssoinsert/api", method = RequestMethod.POST)
    public ResultInfo addNewUser(
            @RequestParam("enc_data") String enc_data,
            @RequestParam("key_data") String key_data,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String upassword,
            HttpServletRequest request,
            HttpSession session, Model model) {
        ResultInfo resultInfo = null;
        upassword = upassword.replaceAll("%10", "#");
        if (enc_data != null & key_data != null) {
            try {
                if (Md5Util.pwdDigest(key_data).equals(enc_data)) {
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

                            String token = UUID.randomUUID().toString().toLowerCase();
                            user.setToken(token);

                            user.setApiKey(UtilClass.generateAPIKey());
                            userService.insertUserInfo(user);

                            resultInfo = ResultInfoFactory.getSuccessResultInfo();

                        } else {
                            resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
                        }
                    } else {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                logger.error("UserRestController/addNewUser: ", e);


            }
        }

        return resultInfo;
    }

    /**
     * This is function to logout the user on the same with jforum..
     *
     * @param session : session variable.
     * @return : ResultInfo
     */
    @RequestMapping(value = "/user/ssologout/api", method = RequestMethod.POST)
    public ResultInfo ssologout(HttpSession session) {
        ResultInfo resultInfo = null;

        session.removeAttribute("user");

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    /**
     * The user sign up function
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "/user/ssoinsert_login/api", method = RequestMethod.GET)
    public ResultInfo addNewUser_Login(
            HttpServletRequest request,
            HttpSession session, Model model) {

        String enc_data = request.getParameter("enc_data");
        String key_data = request.getParameter("key_data");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String upassword = request.getParameter("password");

        ResultInfo resultInfo = null;
        upassword = upassword.replaceAll("%10", "#");
        if (enc_data != null & key_data != null) {
            try {
                if (Md5Util.pwdDigest(key_data).equals(enc_data)) {
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

                            String token = UUID.randomUUID().toString().toLowerCase();
                            user.setToken(token);

                            user.setApiKey(UtilClass.generateAPIKey());
                            userService.insertUserInfo(user);

                            session.setAttribute("user", user);
                            resultInfo = ResultInfoFactory.getSuccessResultInfo();

                        } else {
                            resultInfo = ResultInfoFactory.getErrorResultInfo("email is already taken someone!");
                        }
                    } else {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("username already exists!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                logger.error(e);
            }
        }

        return resultInfo;
    }

    /**
     *This function is to aplly coupon
     * @param coupon
     * @param datasetId
     */
    //todo coupon apply
    @PostMapping("/user/{coupon}/api/{datasetId}")
    public ResultInfo loadByCoupon(@PathVariable String coupon, @PathVariable String datasetId, HttpSession session) throws ParseException {

        ResultInfo resultInfo = null;

        int discout = 0;
        Coupon_manage coupon_manage = coupon_manageService.getByCoupon(coupon);

        //user not logged in
        User userinfo = (User) session.getAttribute("user");
        if (userinfo == null || userinfo.getId() == 0 || userinfo.getId() == -1) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("login");
            resultInfo.setObject(0);
            return resultInfo;
        }


        // Wrong or bad coupon
        if(coupon_manage == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("badcoupon");
            resultInfo.setObject(0);
            return resultInfo;
        }

        //coupon code is for subscribe returns 0
        if(coupon_manage.getCoupon_for() == 1){
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            resultInfo.setObject(0);
            return resultInfo;
        }

        Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(datasetId));

        int dataset_vendor_id = 0;
        if(data_sets != null){
            dataset_vendor_id = (int)data_sets.getVendor_id();
        }else{
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            resultInfo.setObject(0);
            return resultInfo;
        }

        // if vendor id for different
        if(coupon_manage.getVendor_id() != (int)data_sets.getVendor_id()){
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            resultInfo.setObject(0);
            return resultInfo;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Date expiry = dateFormat.parse(coupon_manage.getExpiry_date());

        //expired date returns discount percent 0
        if(expiry.compareTo(date)<1){
            resultInfo = ResultInfoFactory.getErrorResultInfo("expired");
            resultInfo.setObject(0);
            return resultInfo;
        }

        Pager pager = new Pager();
        pager.setCoupon_id(coupon_manage.getId());
        pager.setUser_id((int)userinfo.getId());

        List<Coupon_user> coupon_users = coupon_userService.getByCouponAndUserId(pager);
        if(coupon_users == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            resultInfo.setObject(0);
            return resultInfo;

        }else{
            for(Coupon_user coupon_user : coupon_users){

                //coupon already used
                if(coupon_user.getUsed_flag() == 1){
                    resultInfo = ResultInfoFactory.getErrorResultInfo("used");
                    resultInfo.setObject(0);
                    return resultInfo;
                }
            }
        }


        String [] datasetIds = coupon_manage.getDataset_ids().split(",");
        for(int i=0; i<datasetIds.length;i++){
            if(datasetId.equalsIgnoreCase(datasetIds[i])){
                discout = coupon_manage.getDiscount();
                break;
            }
            if(i == datasetIds.length-1){
                resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
                resultInfo.setObject(0);
                return resultInfo;
            }
        }
        resultInfo = ResultInfoFactory.getErrorResultInfo("success");
        resultInfo.setObject(discout);
        return resultInfo;
    }


    @RequestMapping(value = "/user/dataset/available", method = RequestMethod.POST)
    public ResultInfo getAvailableDataset(
            HttpServletRequest request) {

        String username = request.getParameter("username");

        ResultInfo resultInfo = null;
        try {

            User userInfo = userService.loadUserByUsername(username);
            if (userInfo == null) {
                userInfo = userService.loadUserByEmail(username);
                if (userInfo == null) {
                    resultInfo = ResultInfoFactory.getErrorResultInfo("This user is not member of dataexo!");
                    resultInfo.setObject("");
                } else {
                    List<String> availableTable = data_setsService.loadAavailableDataset((int) userInfo.getId());
                    String result = "";
                    for (int i = 0; i < availableTable.size(); i++) {
                        if (i != 0) {
                            result += ",";
                        }
                        result += availableTable.get(i);
                    }
                    resultInfo = ResultInfoFactory.getSuccessResultInfo();
                    resultInfo.setObject(result);
                }
            } else {

                List<String> availableTable = data_setsService.loadAavailableDataset((int) userInfo.getId());
                String result = "";
                for (int i = 0; i < availableTable.size(); i++) {
                    if (i != 0) {
                        result += ",";
                    }
                    result += availableTable.get(i);
                }
                resultInfo = ResultInfoFactory.getSuccessResultInfo();
                resultInfo.setObject(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultInfo = ResultInfoFactory.getErrorResultInfo("This user is not member of dataexo!");

            logger.error("This user is not member of dataexo!", e);
        }

        return resultInfo;
    }


    @RequestMapping(value = "/sso/get/userinfo", method = RequestMethod.POST)
    public ResultInfo loginAuth(
            @RequestParam("apiKey") String apiKey,
            @RequestParam("token") String token
    ) {

        ResultInfo resultInfo = null;
        Token tokenModel = tokenService.getByToken(token);

        long current_time = System.currentTimeMillis();
        long diff = current_time - Long.parseLong(tokenModel.getExpire());

        if (diff > 0) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Your token is expired!");
        } else {

            User userInfo = userService.loadUserByApiKey(apiKey);
            if (userInfo == null) {
                resultInfo = ResultInfoFactory.getErrorResultInfo("Username or email doesn't exist.");

            } else {
                resultInfo = ResultInfoFactory.getSuccessResultInfo();
                resultInfo.setObject(userInfo);


            }
        }

        return resultInfo;
    }


    @RequestMapping(value = "/sso/insert/token", method = RequestMethod.POST)
    public ResultInfo loginAuth(
            @RequestParam("token") String token
    ) {

        ResultInfo resultInfo = null;

        Token new_token = new Token();

        new_token.setToken(token);
        long expire = System.currentTimeMillis();
        expire += (long) 1000 * 60 * tokenAuthTimeout;

        new_token.setExpire("" + expire);
        tokenService.insertToken(new_token);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();

        return resultInfo;
    }

    /**
     * This functions is for making order before payment.
     *
     * @param datasetid
     * @return
     */
    @RequestMapping(value = "/user/payment/order", method = RequestMethod.POST)
    public ResultInfo paymentOrder(
            @RequestParam("id") Integer datasetid, @RequestParam("coupon") String coupon, HttpSession session) {

        ResultInfo resultInfo = null;

        Token new_token = new Token();

        long expire = System.currentTimeMillis();
        expire += (long) 1000 * 60 * 60;

        new_token.setExpire("" + expire);
        new_token.setToken(UtilClass.generateToken());
        tokenService.insertToken(new_token);

        Data_sets data_sets = data_setsService.getData_setsById(datasetid);

        if (data_sets == null) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Dataset id issue");
            return resultInfo;
        }
        float onetime_price = data_sets.getOnetime_price();
        int coupon_id = 0;
        if(!coupon.equalsIgnoreCase("coupon")){
            Coupon_manage coupon_manage = coupon_manageService.getByCoupon(coupon);
            int discount = coupon_manage.getDiscount();
            onetime_price -= onetime_price*discount/100;
            coupon_id = coupon_manage.getId();
        }
        Pay_order pay_order = new Pay_order();
        pay_order.setAmount(onetime_price);
        pay_order.setDataset_ids(data_sets.getId() + ",");
        pay_order.setMembership_id("-1");
        pay_order.setToken(new_token.getToken());

        pay_orderService.insertPayOrder(pay_order);
        User userinfo = (User) session.getAttribute("user");
        if (userinfo == null || userinfo.getId() == 0 || userinfo.getId() == -1) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("login");
        }
        Coupon_user coupon_user = new Coupon_user();
        coupon_user.setUsed_flag(0);
        coupon_user.setOrder_id(pay_order.getId());
        coupon_user.setUser_id(userinfo.getId());
        coupon_user.setCoupon_id(coupon_id);
        if(coupon_id > 0){
            coupon_userService.saveCoupon_user(coupon_user);
        }
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        resultInfo.setObject(pay_order.getId());

        return resultInfo;
    }


    /**
     * This functions is for making order before payment.
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/user/payment/do", method = RequestMethod.POST)
    public ResultInfo paymentDo(
            HttpServletRequest request
            , HttpSession session) {

        DecimalFormat df = new DecimalFormat("#.00");

        Integer order_id = Integer.parseInt(request.getParameter("order_id"));

        String paymode = request.getParameter("paymode");
        String email = request.getParameter("email");
        String address_line1 = request.getParameter("address_line1");
        String address_city = request.getParameter("address_city");
        String address_state = request.getParameter("address_state");
        String address_zip = request.getParameter("address_zip");
        String number = "";

        if (request.getParameter("number") != null) {
            number = request.getParameter("number").replaceAll(" ", "");
        }
        String expiry = request.getParameter("expiry");

        String cvc = request.getParameter("cvc");

        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo == null || userinfo.getId() == 0 || userinfo.getId() == -1) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("login");
        } else {

            Pay_order pay_order = pay_orderService.getPayOrder(order_id);

            if (pay_order.getMembership_id().equals("-1")) {
                ////////// This is dataset payment section. //////////////
                if (paymode.equals("mycard")) {
                    Charge charge = stripeService.doPaymentByCustomer(userinfo.getCustomer_id(), "usd", pay_order.getAmount());
                    if (charge == null) {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("Stripe payment has some problem. Please try again . ");
                        return resultInfo;
                    }
                    if (!charge.getStatus().equals("succeeded")) {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("Stripe payment has some problem. Please try again . ");
                        return resultInfo;
                    }

                }

                if (paymode.equals("stripe")) {
                    // checkout for stripe payment

                    com.stripe.model.Token token = stripeService.createToken(number, expiry, cvc
                            , address_city, address_zip, address_state, address_line1);
                    if (token == null) {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("token");
                        return resultInfo;
                    }

                    String result = stripeService.doPayment(token.getId(), pay_order.getAmount(), email);

                    if (!result.equals("success")) {
                        resultInfo = ResultInfoFactory.getErrorResultInfo(result);
                        return resultInfo;
                    }

                }

                /// this is normal user payment section. /////////
                Pay_log pay_log = new Pay_log();
                pay_log.setAmount(pay_order.getAmount());
                pay_log.setTime("" + System.currentTimeMillis());
                pay_log.setDescription("This is checkout payment history. You have paid $" + pay_order.getAmount());

                pay_log.setUserid(userinfo.getId() + "");
                pay_log.setDataset_name("");
                pay_log.setDataset_id(-1);
                if (pay_order.getAmount() != 0) {
                    pay_logService.savePay_log(pay_log);
                }

                //insert to trans_log and update amount in user table Fee
                // and balance management functions

                Trans_log trans_log = new Trans_log();
                trans_log.setStatus(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date();
                trans_log.setDate(dateFormat.format(date));
                int fee_percent = feeService.getFeeById(1).getFee_percent();
                String datasetId = pay_order.getDataset_ids();
                String [] datasetIds = datasetId.split(",");
                if (datasetIds.length == 1) {
                    /////// this is for directly payment for one dataset.  'not using add cart!!!' //////
                    Data_sets data_sets = data_setsService.getData_setsById(Integer.valueOf(datasetIds[0]));
                    if(data_sets.getVendor_id() == -1){
                        //////  if vendor_id=-1 , this is for dataexo platform datasets. So no need to split fee. ////
                        trans_log.setTrans_type(1);
                        trans_log.setDescription("Received from " + userinfo.getUsername() + " for dataset_id " + data_sets.getVendor_id());
                        trans_log.setAmount(pay_order.getAmount());
                        trans_log.setRaw_amount(pay_order.getAmount());
                        trans_log.setVendor_id(-1);
                        trans_log.setDatasets_id(Integer.valueOf(datasetIds[0]));
                        trans_logService.saveTrans(trans_log);
                        userService.updateBalance(-1, pay_order.getAmount());

                        Inbox_Notify inbox_notify = new Inbox_Notify();
                        inbox_notify.setTitle("Dataset Purchase");
                        inbox_notify.setContent("One Time Purchase of Dataset By " + userinfo.getUsername());
                        inbox_notify.setTo_user_ids(1+",");
                        inbox_notify.setUpdated_at(dateFormat.format(date));
                        inboxNotifyService.saveInbox_Notify(inbox_notify);

                    }else {
                        // this is for vendor's dataset section ////////

                        double[] amount = {pay_order.getAmount() * (100 - fee_percent) / 100, pay_order.getAmount() * fee_percent / 100};

                        String[] description = {"You have received "+ df.format(amount[0]) +"$ of "+ df.format(pay_order.getAmount()) + ". "+df.format(amount[1])+"$ is DataExo platform fee. Received from " + userinfo.getUsername() + " for dataset_id=" + data_sets.getVendor_id()
                                , "Service Charge For DataExo Platform. Received " + df.format(amount[1]) + "$ of " +df.format(pay_order.getAmount())+"$ ."};

                        int[] trans_type = {1, -1};
                        int[] datasets_id = {Integer.valueOf(datasetIds[0]), 0};
                        int[] vendor_id = {(int) data_sets.getVendor_id(), -1};
                        for (int i = 0; i < amount.length; i++) {
                            trans_log.setDescription(description[i]);
                            trans_log.setAmount(amount[i]);
                            trans_log.setTrans_type(trans_type[i]);
                            trans_log.setVendor_id(vendor_id[i]);
                            trans_log.setDatasets_id(datasets_id[i]);
                            trans_log.setRaw_amount(pay_order.getAmount());

                            trans_logService.saveTrans(trans_log);
                            userService.updateBalance(vendor_id[i], amount[i]);
                        }
                        Inbox_Notify inbox_notify = new Inbox_Notify();
                        inbox_notify.setTitle("Dataset Purchase");
                        inbox_notify.setContent("One Time Purchase of Dataset By " + userinfo.getUsername());
                        inbox_notify.setTo_user_ids(userService.loadUserByVendorId(data_sets.getVendor_id()).getId()+",");
                        inbox_notify.setUpdated_at(dateFormat.format(date));
                        inboxNotifyService.saveInbox_Notify(inbox_notify);
                    }

                    //set coupon_user used_flag to 1 after successful payment for one time payment

                    Coupon_user coupon_user = coupon_userService.getByOrderId(pay_order.getId());
                    if(coupon_user != null) {
                        coupon_user.setUsed_flag(1);
                        coupon_userService.updateCoupon_user(coupon_user);
                    }
                } else if (datasetIds.length > 1) {

                    /// this is for multi-dataset purchase section. 'my cart function!!!' ////
                    double amount = 0.00;
                    double trans_amount ;
                    for (int i = 0; i < datasetIds.length; i++) {

                        Data_sets data_sets = data_setsService.getData_setsById(Integer.valueOf(datasetIds[i]));
                        if(data_sets.getVendor_id() == -1){
                            trans_amount = data_sets.getOnetime_price();
                            amount += trans_amount;
                        }else {
                            trans_amount = data_sets.getOnetime_price() * (100 - fee_percent) / 100;
                        }
                        trans_log.setTrans_type(1);
                        trans_log.setVendor_id((int) data_sets.getVendor_id());
                        trans_log.setAmount(trans_amount);
                        trans_log.setDatasets_id(Integer.valueOf(datasetIds[i]));
                        trans_log.setRaw_amount(data_sets.getOnetime_price());

                        trans_log.setDescription("You have received "+ df.format(trans_amount) +"$ of "+ df.format(data_sets.getOnetime_price()) + ". Received from " + userinfo.getUsername() + " for dataset_id=" + data_sets.getVendor_id());
                        trans_logService.saveTrans(trans_log);
                        userService.updateBalance((int) data_sets.getVendor_id(), trans_amount);

                        Inbox_Notify inbox_notify = new Inbox_Notify();
                        inbox_notify.setTitle("Dataset Purchase");
                        inbox_notify.setContent("One Time Purchase of Dataset By " + userinfo.getUsername());
                        inbox_notify.setTo_user_ids(userService.loadUserByVendorId(data_sets.getVendor_id()).getId()+",");
                        inbox_notify.setUpdated_at(dateFormat.format(date));
                        inboxNotifyService.saveInbox_Notify(inbox_notify);

                    }
                    if(amount != pay_order.getAmount()) {
                        trans_amount = pay_order.getAmount() - amount;
                        trans_log.setAmount(trans_amount * fee_percent / 100);
                        trans_log.setTrans_type(-1);
                        trans_log.setVendor_id(-1);
                        trans_log.setDatasets_id(0);
                        trans_log.setRaw_amount(pay_order.getAmount());
                        trans_log.setDescription( "Service Charge For DataExo Platform. Received " + df.format(trans_log.getAmount()) + "$ of " + df.format(pay_order.getAmount())+"$ .");
                        trans_logService.saveTrans(trans_log);
                        userService.updateBalance(-1, (trans_amount * fee_percent / 100));
                    }

                }


                //for cart payment set used_flag to 1
                Pager pager = new Pager();
//                pager.setUser_id((int)userinfo.getId());
//                List<Coupon_user> coupon_users = coupon_userService.getByCouponAndUserId(pager);
//                if(coupon_users != null) {
//                    System.out.println("I am here");
//                    for (Coupon_user coupon_user : coupon_users) {
//                        if (coupon_user.getOrder_id() < 0 && coupon_user.getUsed_flag() < 0) {
//                            System.out.println(coupon_user.getId());
//                            coupon_user.setUsed_flag(1);
//                            coupon_userService.updateCoupon_user(coupon_user);
//                        }
//                    }
//                }

                //////// remove all data from my cart list
                pager.setSearch_str("");
                pager.setCateid(userinfo.getId() + "");
                bucketService.initPage(pager);

                pager.setStart(0);
                pager.setLimit(pager.getTotalCount());

                List<Bucket> bucketList = null;

                if (pager.getTotalCount() > 0) {
                    bucketList = bucketService.loadBucket(pager);
                    for (int i = 0; i < bucketList.size(); i++) {
                        Bucket bucket = bucketList.get(i);

                        //todo for cart payment set used_flag to 1
                        Coupon_user coupon_user = coupon_userService.getByOrderId(-1 * (long)bucket.getId());
                        if(coupon_user!= null){
                            if(coupon_user.getUsed_flag()<0) {
                                coupon_user.setUsed_flag(1);
                                coupon_userService.updateCoupon_user(coupon_user);
                            }
                        }

                        //deletes carts item
                        bucketService.deleteBucket(bucket);
                    }
                }

                //////////
                String subject = "You can download data set from this link.";

                //   String html = "<html><body><span style='color:red;'>"+url+"</span></body></html>";

                String[] idsAr = pay_order.getDataset_ids().split(",");

                String html_items = "";

                for (int i = 0; i < idsAr.length; i++) {

                    /////////// increase download nums
                    data_setsService.increaseDownloadNum(idsAr[i]);

                    ////////////////////

                    Purchase purchase = new Purchase();
                    purchase.setUserid(Integer.parseInt("" + userinfo.getId()));
                    purchase.setDataset_id(Integer.parseInt(idsAr[i]));

                    String token = UUID.randomUUID().toString().toLowerCase();
                    purchase.setToken(token);
                    purchase.setOrder_date("" + System.currentTimeMillis());

                    purchaseService.insertPurchase(purchase);

                    purchase = purchaseService.selectPurchase(purchase);

                    Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(idsAr[i]));
                    Token tokenmodel = new Token();
                    tokenmodel.setToken(token);

                    long current = System.currentTimeMillis();

                    long expire = (long) 1000 * 3600 * 100 * data_sets.getOnetime_expires();
                    current += expire;
                    tokenmodel.setExpire("" + current);

                    tokenService.insertToken(tokenmodel);

                    String url = domain + "/api/v3/dataset_download/" + purchase.getId() + "/" + token;

                    html_items += "<tr>" +
                            "<td align='center' valign='top' style='    border-bottom: 3px solid #c1bfbf;padding: 2px;'>" +
                            "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                            "<tr style='padding-top:0;'>" +
                            "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                            data_sets.getName() +
                            "</td>" +
                            "<td style='vertical-align: middle;    padding: 0px 10px;'>" + data_sets.getOnetime_price() + "$</td>" +
                            "<td style='vertical-align: middle;' align='center' valign='top'>" +
                            "<a href='" + url + "'>Download</a>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</td>" +
                            "</tr>";

                }

                html_items += "<tr>" +
                        "<td align='center' valign='top' style=' padding: 2px;'>" +
                        "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                        "<tr style='padding-top:0;'>" +
                        "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                        "Total Price :" +
                        "</td>" +
                        "<td style='vertical-align: middle;    padding: 0px 10px;'>" + pay_order.getAmount() + "$</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td>" +
                        "</tr>";


                String html = UtilClass.checkoutHtmlString(1);
                html += html_items;
                html += UtilClass.checkoutHtmlString(2);

                // mailgun email service
                // UtilClass.sendMail(appEmail, userinfo.getEmail() , html , subject, mailgun_domain, mailgun_apikey);
                thirdPartyService.sendAwsSes(appEmail, userinfo.getEmail(), subject, html);
            }
            if (pay_order.getDataset_ids().equals("-1")) {
                ////////// This is subscription payment section /////////////////

                String mode = pay_order.getMembership_id();

                Plan plan = planService.getPlanById(Integer.parseInt(mode));
                if (plan == null)
                    plan = new Plan();
                Subscription subscription = new Subscription();

                if (paymode.equals("stripe")) {

                    Pager<Pay_sources> pager = new Pager<Pay_sources>();
                    pager.setSearch_str("");
                    pager.setUser_id(Integer.parseInt("" + userinfo.getId()));

                    List<Pay_sources> dataList = pay_sourceService.loadPay_Source(pager);

                    Pay_sources pay_sources = new Pay_sources();

                    String card_num = number;
                    String card_exp = expiry;
                    String card_cvc = cvc;

                    pay_sources.setCard_number(card_num);
                    pay_sources.setCard_expiry(card_exp);
                    pay_sources.setCard_cvc(cvc);


                    //     com.stripe.model.Token token = stripeService.createToken(card_num, card_exp, card_cvc, "dataexo", "unknow", "unknow", "unknow");
                    Card card = stripeService.attachCard(card_num, card_exp, card_cvc, userinfo.getCustomer_id());

                    if (card != null) {
                        pay_sources.setCustomer_id(card.getId());
                    } else {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("You input invalid card number. Please try again. ");
                        return resultInfo;
                    }

                    pay_sources.setLast_4_digits(card_num.substring(card_num.length() - 4, card_num.length()));
                    pay_sources.setCard_type(UtilClass.getCreditCardTypeByNumber(card_num.replaceAll(" ", "")));


                    if (dataList.size() == 0) {
                        pay_sources.setIs_default(1);
                    } else {
                        pay_sources.setIs_default(0);
                    }
                    pay_sources.setUser_id(Integer.parseInt("" + userinfo.getId()));
                    pay_sourceService.savePay_Source(pay_sources);


                    subscription = stripeService.subscriptionPlan(plan.getPlan_id(), userinfo.getCustomer_id());
                    if(subscription == null){
                        resultInfo = ResultInfoFactory.getErrorResultInfo("subscription_null");
                        return resultInfo;
                    }
                   /* if (subscription.getStatus().indexOf("success") >= 0) {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("pay");
                        return resultInfo;
                    }*/

                }

                if (paymode.equals("mycard")) {
                    subscription = stripeService.subscriptionPlan(plan.getPlan_id(), userinfo.getCustomer_id());
                    if (subscription.getStatus().indexOf("success") >= 0) {
                        resultInfo = ResultInfoFactory.getErrorResultInfo("pay");
                        return resultInfo;
                    }
                }

                Map<String, Object> map = new HashMap<String, Object>();


                //not required as sub_manage is used for subscription_management
//                userinfo.setExpire_date(UtilClass.getExpireDate(Integer.parseInt(mode)));
//                userinfo.setMembership(Integer.parseInt(mode));
//                userinfo.setBalance(plan.getVr_price());
//
//                userService.updateInfo(userinfo);

                //here insert sub_manage add subscription id to table
                Sub_manage sub_manage = new Sub_manage();
                sub_manage.setPlan_id(plan.getId());
                sub_manage.setUser_id(userinfo.getId());
                sub_manage.setBalance(plan.getVr_price());
                sub_manage.setExpiry_date(UtilClass.getExpiryDate(Integer.parseInt(mode)));
                sub_manage.setStatus(1);
                sub_manage.setSubscription_id(subscription.getId());
                sub_manageService.saveSubscription(sub_manage);

                Pay_log pay_log = new Pay_log();
                pay_log.setAmount(pay_order.getAmount());
                pay_log.setTime("" + System.currentTimeMillis());
                pay_log.setDescription("You upgrade your membership. You have paid $" + pay_order.getAmount() + " for membership");
                pay_log.setUserid(userinfo.getId() + "");
                pay_log.setDataset_name("");
                pay_log.setDataset_id(-1);

                pay_logService.savePay_log(pay_log);
                //subscription fee management complete
                Trans_log trans_log = new Trans_log();
                trans_log.setStatus(1);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date();
                trans_log.setDate(dateFormat.format(date));
                //if vendor is admin no need to cut fees
                if(plan.getVendor_id() == -1){
                    trans_log.setTrans_type(1);
                    trans_log.setDescription("Received " + df.format(pay_order.getAmount()) + "$ from " + userinfo.getUsername() +" for plan_id" + plan.getVendor_id());

                    trans_log.setRaw_amount(pay_order.getAmount());
                    trans_log.setAmount(pay_order.getAmount());
                    trans_log.setVendor_id(plan.getVendor_id());
                    trans_logService.saveTrans(trans_log);
                    userService.updateBalance(plan.getVendor_id(), pay_order.getAmount());

                    Inbox_Notify inbox_notify = new Inbox_Notify();
                    inbox_notify.setTitle("Dataset Purchase");
                    inbox_notify.setContent("Subscription By " + userinfo.getUsername());
                    inbox_notify.setTo_user_ids(1+",");
                    inbox_notify.setUpdated_at(dateFormat.format(date));
                    inboxNotifyService.saveInbox_Notify(inbox_notify);

                }else { //for vendor other than admin cut fees
                    int fee_percent = feeService.getFeeById(2).getFee_percent();
                    double [] amount = {pay_order.getAmount() * (100-fee_percent)/100 , pay_order.getAmount() * fee_percent/100};



                    String [] description = {"You have received "+ df.format(amount[0]) +"$ of "+ pay_order.getAmount() + ". Received from " + userinfo.getUsername() + " for plan_id=" + plan.getId()
                            ,"Received " + df.format(amount[1]) + "$ from " + userinfo.getUsername() +" for plan_id" + plan.getVendor_id()};
                    int[] vendor_id = {plan.getVendor_id(), -1};
                    int[] trans_type = {1, -1};
                    for (int i = 0; i < amount.length; i++) {
                        trans_log.setDescription(description[i]);
                        trans_log.setAmount(amount[i]);
                        trans_log.setVendor_id(vendor_id[i]);
                        trans_log.setRaw_amount(pay_order.getAmount());
                        trans_log.setTrans_type(trans_type[i]);
                        trans_logService.saveTrans(trans_log);
                        userService.updateBalance(vendor_id[i], amount[i]);
                    }

                    Inbox_Notify inbox_notify = new Inbox_Notify();
                    inbox_notify.setTitle("Dataset Purchase");
                    inbox_notify.setContent("Subscription By " + userinfo.getUsername());
                    inbox_notify.setTo_user_ids(userService.loadUserByVendorId(plan.getVendor_id()).getId()+",");
                    inbox_notify.setUpdated_at(dateFormat.format(date));
                    inboxNotifyService.saveInbox_Notify(inbox_notify);
                }

            }

            resultInfo = ResultInfoFactory.getSuccessResultInfo();

        }

        return resultInfo;
    }



    //todo for cart coupon
    @RequestMapping(value = "/user/payment/checkout", method = RequestMethod.POST)
    public ResultInfo paymentCheckOut(HttpSession session) {

        ResultInfo resultInfo = null;

        float sum = 0, discount_value = 0;
        List<Data_sets> data_setsList = new ArrayList<Data_sets>();

        User userinfo = (User) session.getAttribute("user");

        String dataset_ids = "";
        Pager pager = new Pager();
        if (userinfo == null) {
            pager.setSearch_str(session.getId());
            pager.setCateid("-1");
            bucketService.initPage(pager);

            List<Bucket> bucketList = null;

            if (pager.getTotalCount() > 0) {
                bucketList = bucketService.loadBucket(pager);

                for (int i = 0; i < bucketList.size(); i++) {
                    Bucket bucket = bucketList.get(i);

                    Data_sets data_sets = data_setsService.getData_setsById(bucketList.get(i).getDataset_id());

                    dataset_ids += data_sets.getId() + ",";
                    Coupon_user coupon_user = coupon_userService.getByOrderId( (long)(-1 *bucketList.get(i).getId()));
                    if(coupon_user != null){
                        int discount = coupon_user.getDiscount();
                        discount_value += (data_sets.getOnetime_price() * discount )/ 100;
                    }

                    sum += data_sets.getOnetime_price();
                    // bucketService.deleteBucket(bucket);

                }
            }
        } else {

            pager.setSearch_str("");
            pager.setCateid(userinfo.getId() + "");
            bucketService.initPage(pager);

            pager.setStart(0);
            pager.setLimit(pager.getTotalCount());

            List<Bucket> bucketList = null;

            if (pager.getTotalCount() > 0) {
                bucketList = bucketService.loadBucket(pager);

                for (int i = 0; i < bucketList.size(); i++) {
                    Bucket bucket = bucketList.get(i);

                    Data_sets data_sets = data_setsService.getData_setsById(bucketList.get(i).getDataset_id());

                    dataset_ids += data_sets.getId() + ",";

                    Coupon_user coupon_user = coupon_userService.getByOrderId( (long)(-1 *bucketList.get(i).getId()));
                    if(coupon_user != null){
                        int discount = coupon_user.getDiscount();
                        discount_value += (data_sets.getOnetime_price() * discount )/ 100;
                    }

                    sum += data_sets.getOnetime_price();

                    //bucketService.deleteBucket(bucket);
                }
            }
        }

        sum -= discount_value;

        Token new_token = new Token();

        long expire = System.currentTimeMillis();
        expire += (long) 1000 * 60 * 60;

        new_token.setExpire("" + expire);
        new_token.setToken(UtilClass.generateToken());
        tokenService.insertToken(new_token);


        Pay_order pay_order = new Pay_order();
        pay_order.setAmount(sum);
        pay_order.setDataset_ids(dataset_ids);
        pay_order.setMembership_id("-1");
        pay_order.setToken(new_token.getToken());

        pay_orderService.insertPayOrder(pay_order);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        resultInfo.setObject(pay_order.getId());

        return resultInfo;
    }


    @RequestMapping(value = "/user/cart/subscription/checkout", method = RequestMethod.POST)
    public ResultInfo subscriptionCheckOut(HttpSession session) {

        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo == null || userinfo.getId() == 0 || userinfo.getId() == -1) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("redirect");
        } else {

            int not_download_cnt = 0;

            Pager pager = new Pager();
            pager.setSearch_str("");
            pager.setCateid(userinfo.getId() + "");
            bucketService.initPage(pager);
            pager.setStart(0);
            pager.setLimit(pager.getTotalCount());

            List<Bucket> bucketList = null;

            String subject = "You can download data set from this link.";

            String html_items = "";
            float sum = 0;
            if (pager.getTotalCount() > 0) {
                bucketList = bucketService.loadBucket(pager);

                for (int i = 0; i < bucketList.size(); i++) {
                    Bucket bucket = bucketList.get(i);
                    Data_sets data_sets = data_setsService.getData_setsById(bucket.getDataset_id());

                    int vendor_id = (int) data_sets.getVendor_id();
                    List<Plan> planList = planService.getPlanByVendorId(vendor_id);
                    for(int j = 0; j < planList.size() - 1; j ++){
                        Plan plan = planList.get(j);

                        Sub_manage sub_manage =  sub_manageService.getSubscriptionByUserIdAndPlanId(plan.getId(),userinfo.getId());

                        if(sub_manage != null){
                            if(data_sets.getDownload_price() < sub_manage.getBalance()){
                                sub_manage.setBalance(sub_manage.getBalance() - data_sets.getDownload_price());
                                data_sets.setSub_available(1);

                                sub_manageService.updateSubscription(sub_manage);
                                break;
                            }
                        }
                    }
                    if(data_sets.getSub_available() == 1)
                    {
                        sum += data_sets.getDownload_price();
                    }
                }


                for (int i = 0; i < bucketList.size(); i++) {
                    Bucket bucket = bucketList.get(i);

                    Data_sets data_sets = data_setsService.getData_setsById(bucket.getDataset_id());

                    int vendor_id = (int) data_sets.getVendor_id();
                    List<Plan> planList = planService.getPlanByVendorId(vendor_id);
                    for(int j = 0; j < planList.size() - 1; j ++){
                        Plan plan = planList.get(j);

                        Sub_manage sub_manage =  sub_manageService.getSubscriptionByUserIdAndPlanId(plan.getId(),userinfo.getId());

                        if(sub_manage != null){
                            if(data_sets.getDownload_price() < sub_manage.getBalance()){
                                data_sets.setSub_available(1);
                                break;
                            }
                        }
                    }
                    if(data_sets.getSub_available() == 1)
                    {
                        Purchase purchase = new Purchase();
                        purchase.setUserid(bucket.getUserid());
                        purchase.setDataset_id(bucket.getDataset_id());

                        String token = UUID.randomUUID().toString().toLowerCase();
                        purchase.setToken(token);
                        purchase.setOrder_date("" + System.currentTimeMillis());

                        purchaseService.insertPurchase(purchase);

                        purchase = purchaseService.selectPurchase(purchase);

                        data_sets = data_setsService.getData_setsById(bucket.getDataset_id());
                        Token tokenmodel = new Token();
                        tokenmodel.setToken(token);

                        long current = System.currentTimeMillis();

                        long expire = (long) 1000 * 3600 * 24 * data_sets.getOnetime_expires();
                        current += expire;
                        tokenmodel.setExpire("" + current);

                        tokenService.insertToken(tokenmodel);

                        bucketService.deleteBucket(bucket);

                        String url = domain + "/api/v3/dataset_download/" + purchase.getId() + "/" + token;

                        html_items += "<tr>" +
                                "<td align='center' valign='top' style='    border-bottom: 3px solid #c1bfbf;padding: 2px;'>" +
                                "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                                "<tr style='padding-top:0;'>" +
                                "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                                data_sets.getName() +
                                "</td>" +
                                "<td style='vertical-align: middle;    padding: 0px 10px;'>" + data_sets.getOnetime_price() + "$</td>" +
                                "<td style='vertical-align: middle;' align='center' valign='top'>" +
                                "<a href='" + url + "'>Download</a>" +
                                "</td>" +
                                "</tr>" +
                                "</table>" +
                                "</td>" +
                                "</tr>";

                    }
                    else{
                        not_download_cnt ++;
                    }
                }


            }

            Pay_log pay_log = new Pay_log();
            pay_log.setAmount(sum);
            pay_log.setTime("" + System.currentTimeMillis());
            pay_log.setDescription("This is subscription checkout. You used  $" + sum + " to download dataset");

            pay_log.setUserid(userinfo.getId() + "");
            pay_log.setDataset_name("subscription dataset download");
            pay_log.setDataset_id(1);
            if (sum != 0) {
                pay_logService.savePay_log(pay_log);
            }

            // minus price for download dataset
            /*double cur_balance = userinfo.getBalance();
            userinfo.setBalance(cur_balance - sum);
            userService.updateInfo(userinfo);*/

            html_items += "<tr>" +
                    "<td align='center' valign='top' style=' padding: 2px;'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                    "<tr style='padding-top:0;'>" +
                    "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                    "Total Price :" +
                    "</td>" +
                    "<td style='vertical-align: middle;    padding: 0px 10px;'>" + sum + "$</td>" +
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

            thirdPartyService.sendAwsSes(appEmail, userinfo.getEmail(), subject, html);

            if(not_download_cnt == 0) {
                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            }
            else{
                resultInfo = ResultInfoFactory.getErrorResultInfo("payment");
            }
        }

        return resultInfo;
    }

    @RequestMapping(value = "/user/cart/free_data/checkout", method = RequestMethod.POST)
    public ResultInfo freeDatasetCheckOut(HttpSession session) {

        ResultInfo resultInfo = null;
        User userinfo = (User) session.getAttribute("user");
        if (userinfo == null || userinfo.getId() == 0 || userinfo.getId() == -1) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("redirect");
        } else {

            Pager pager = new Pager();
            pager.setSearch_str("");
            pager.setCateid(userinfo.getId() + "");
            bucketService.initPage(pager);
            pager.setStart(0);
            pager.setLimit(pager.getTotalCount());

            List<Bucket> bucketList = null;

            String subject = "You can download data set from this link.";

            String html_items = "";

            if (pager.getTotalCount() > 0) {
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

                    long expire = (long) 1000 * 3600 * 24 * data_sets.getOnetime_expires();
                    current += expire;
                    tokenmodel.setExpire("" + current);

                    tokenService.insertToken(tokenmodel);

                    bucketService.deleteBucket(bucket);

                    String url = domain + "/api/v3/dataset_download/" + purchase.getId() + "/" + token;

                    html_items += "<tr>" +
                            "<td align='center' valign='top' style='    border-bottom: 3px solid #c1bfbf;padding: 2px;'>" +
                            "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                            "<tr style='padding-top:0;'>" +
                            "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                            data_sets.getName() +
                            "</td>" +
                            "<td style='vertical-align: middle;    padding: 0px 10px;'>0$</td>" +
                            "<td style='vertical-align: middle;' align='center' valign='top'>" +
                            "<a href='" + url + "'>Download</a>" +
                            "</td>" +
                            "</tr>" +
                            "</table>" +
                            "</td>" +
                            "</tr>";

                }
            }


            html_items += "<tr>" +
                    "<td align='center' valign='top' style=' padding: 2px;'>" +
                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                    "<tr style='padding-top:0;'>" +
                    "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                    "Total Price :" +
                    "</td>" +
                    "<td style='vertical-align: middle;    padding: 0px 10px;'>0$</td>" +
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

            thirdPartyService.sendAwsSes(appEmail, userinfo.getEmail(), subject, html);
            resultInfo = ResultInfoFactory.getSuccessResultInfo();
        }

        return resultInfo;
    }


    @RequestMapping(value = "/user/ssoauth", method = RequestMethod.POST)
    public void SSOAuth(String username, String apikey, HttpSession session) {
        User user = userService.loadUserByUsername(username);
        User user_apikey = userService.loadUserByApiKey(apikey);
        if (user != null && user_apikey != null) {

            if (user_apikey.getUsername().equals(user.getUsername())) {
                session.setAttribute("user", user);
            }
        }
    }

    @RequestMapping(value = "/user/stripe/webhook")
    public Response handleWebhook(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.debug("hook started");

        Stripe.apiKey = stripApiKey;

        StringBuilder body = new StringBuilder();
        BufferedReader reader = request.getReader();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } finally {
            reader.close();
        }

        String sigHeader = request.getHeader("Stripe-Signature");
        Event event = null;

        event = Event.GSON.fromJson(body.toString(), Event.class);
//        logger.debug("even string:" + event.getType());

        if (event.getType().equals("invoice.payment_succeeded")) {
            Invoice invoice = (Invoice) event.getData().getObject();
            String customerId = invoice.getCustomer();
            logger.debug("even customerId:" + customerId);
            User user = userService.loadUserByCustomId(customerId);

//            logger.debug("even user:" + user.getUsername());

            if (user != null) {
                String mailTemplate = UtilClass.getWebhookEmails(domain);

                long amount = invoice.getAmountDue() / 100;
                long expiry = invoice.getPeriodEnd();
                if(invoice.getLines().getData().size() == 0){
                    response.setStatus(200);
                    return Response.ok("", MediaType.APPLICATION_JSON).build();
                }

                expiry = invoice.getLines().getData().get(0).getPeriod().getEnd();

                String subId = invoice.getSubscription();
                try {
                    Subscription subscription = Subscription.retrieve(subId);
                    String planId = subscription.getPlan().getId();

                    Plan plan = planService.getPlanByPlanId(planId);

                    int membership = plan.getId();

                    logger.debug("planid:" + membership);

                    user.setMembership(membership);
                    user.setExpire_date(UtilClass.getExpireDate(membership));

                    user.setBalance(plan.getVr_price());

                    userService.updateInfo(user);

                    Pay_log pay_log = new Pay_log();
                    pay_log.setAmount(amount);
                    pay_log.setTime("" + System.currentTimeMillis());
                    pay_log.setDescription("You charged $" + Long.toString(amount) + " for membership fee.");

                    pay_log.setUserid(Long.toString(user.getId()));
                    pay_log.setDataset_name("");
                    pay_log.setDataset_id(-1);
                    pay_logService.savePay_log(pay_log);


                    mailTemplate = mailTemplate.replace("domain", domain);
                    mailTemplate = mailTemplate.replace("user_name", user.getUsername());
                    mailTemplate = mailTemplate.replace("membership_fee", Long.toString(amount));
                    mailTemplate = mailTemplate.replace("membership_plan", plan.getPlan_name());
                    mailTemplate = mailTemplate.replace("membership_expiry", UtilClass.timeToString(expiry * 1000, "yyyy.MM.dd"));

                    boolean result = thirdPartyService.sendAwsSes(appEmail, user.getEmail(), "Membership Payment", mailTemplate);

                    logger.debug("result:" + result);

                } catch (AuthenticationException e) {
                    e.printStackTrace();
                    logger.debug("error1:" + e.getMessage());
                } catch (InvalidRequestException e) {
                    e.printStackTrace();
                    logger.debug("error2:" + e.getMessage());
                } catch (APIConnectionException e) {
                    e.printStackTrace();
                    logger.debug("error3:" + e.getMessage());
                } catch (CardException e) {
                    e.printStackTrace();
                    logger.debug("error4:" + e.getMessage());
                } catch (APIException e) {
                    e.printStackTrace();
                    logger.debug("error5:" + e.getMessage());
                }
            }
        }

        if (event.getType().equals("account.updated")) {

            Account account = (Account) event.getData().getObject();
            if(account == null){
                response.setStatus(200);
                return Response.ok("", MediaType.APPLICATION_JSON).build();
            }

            Vendors vendor = vendorService.getVendorByAccountId(account.getId());
            if(vendor == null){
                response.setStatus(200);
                return Response.ok("", MediaType.APPLICATION_JSON).build();
            }
            User user = userService.loadUserByVendorId(vendor.getId());

            if(vendor != null){
                if(account.getLegalEntity().getVerification().getStatus().equals("verified")){
                    vendor.setStripe_verify(1);
                    vendor.setReject_reason("");
                    vendorService.updateVendorInfoById(vendor);

                    Inbox_Notify obj = new Inbox_Notify();
                    obj.setTitle("Successfully Verified!");
                    obj.setContent("Your provided information successfully verified. You can request withdraw money. ");
                    obj.setUpdated_at(UtilClass.convertTime(System.currentTimeMillis()));

                    obj.setTo_user_ids(user.getId() + "");
                    inboxNotifyService.saveInbox_Notify(obj);

                }
                else{
                    if(account.getLegalEntity().getVerification().getStatus().equals("pending")){
                        vendor.setStripe_verify(-1);
                        vendor.setReject_reason("");
                        vendorService.updateVendorInfoById(vendor);
                    }
                    else{
                        String reason = account.getVerification().getDisabledReason();
                        vendor.setReject_reason(reason);
                        vendor.setStripe_verify(0);
                        vendor.setReject_reason("");
                        vendorService.updateVendorInfoById(vendor);

                        Inbox_Notify obj = new Inbox_Notify();
                        obj.setTitle("Verification rejected!");
                        obj.setContent("Your provided information was rejected. Reason:" + reason);
                        obj.setUpdated_at(UtilClass.convertTime(System.currentTimeMillis()));

                        obj.setTo_user_ids(user.getId() + "");
                        inboxNotifyService.saveInbox_Notify(obj);
                    }
                }
            }

        }

        response.setStatus(200);
        return Response.ok("", MediaType.APPLICATION_JSON).build();
    }


    @RequestMapping(value = "/user/account/addcard", method = RequestMethod.POST)
    public ResultInfo addCardSources(Pay_sources pay_sources, HttpSession session) {

        ResultInfo resultInfo = null;

        User user = (User) session.getAttribute("user");
        if (user == null) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid user");
            return resultInfo;
        }

        Pager<Pay_sources> pager = new Pager<Pay_sources>();
        pager.setSearch_str("");
        pager.setUser_id(Integer.parseInt("" + user.getId()));

        List<Pay_sources> dataList = pay_sourceService.loadPay_Source(pager);

        String card_num = pay_sources.getCard_number();
        String card_exp = pay_sources.getCard_expiry();
        String card_cvc = pay_sources.getCard_cvc();

        //     com.stripe.model.Token token = stripeService.createToken(card_num, card_exp, card_cvc, "dataexo", "unknow", "unknow", "unknow");
        Card card = stripeService.attachCard(card_num, card_exp, card_cvc, user.getCustomer_id());

        if (card != null) {
            pay_sources.setCustomer_id(card.getId());
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("You input invalid card number. Please try again. ");
            return resultInfo;
        }

        pay_sources.setLast_4_digits(card_num.substring(card_num.length() - 4, card_num.length()));
        pay_sources.setCard_type(UtilClass.getCreditCardTypeByNumber(card_num.replaceAll(" ", "")));


        if (dataList.size() == 0) {
            pay_sources.setIs_default(1);
        } else {
            pay_sources.setIs_default(0);
        }


        pay_sources.setUser_id(Integer.parseInt("" + user.getId()));
        pay_sourceService.savePay_Source(pay_sources);

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    @RequestMapping(value = "/user/account/setCardDefault", method = RequestMethod.POST)
    public ResultInfo setCardDefault(Integer id, HttpSession session) {

        ResultInfo resultInfo = null;

        User user = (User) session.getAttribute("user");
        if (user == null) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid user");
            return resultInfo;
        }

        Pay_sources pay_sources = pay_sourceService.getPay_SourceById(id);

        boolean result = stripeService.setDefaultCard(user.getCustomer_id(), pay_sources.getCustomer_id());
        if (!result) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("Sorry. It has some error. Please try again!");
            return resultInfo;
        }

        pay_sources.setIs_default(1);
        pay_sourceService.updatePay_Source(pay_sources);

        pay_sourceService.setDefaultCard(pay_sources);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

}
