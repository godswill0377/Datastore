package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.*;
import com.dataexo.zblog.vo.*;
import com.dataexo.zblog.vo.auth.Email;
import com.stripe.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vendor")
public class VendorRestController {


    @Value("${captcha.secretkey}")
    public String captcha_secretkey;

    @Value("${address.domain}")
    public String domain;

    @Value("${address.email}")
    public String contact_email;

    @Value("${address.admin_email}")
    public String admin_email;

    @Value("${address.email}")
    public String appEmail;


    @Autowired
    private UserService userService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private Data_setsService data_setsService;


    @Autowired
    private InboxQuestionService inboxQuestionService;

    @Autowired
    private Pay_SourceService pay_sourceService;


    @Autowired
    private InboxNotifyService inboxNotifyService;

    @Autowired
    private VendorMarketingService vendorMarketingService;

    @Autowired
    private ThirdPartyService thirdPartyService;


    @Value("${aws.access_key_id}")
    public String access_key_id;

    @Value("${aws.secret_access_key}")
    public String secret_access_key;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PlanService planService;

    @Autowired
    private Coupon_manageService coupon_manageService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private  Trans_logService trans_logService;

    @Autowired
    private TokenService tokenService;

    /**
     * This is login rest api function.
     * @param captcha
     * @param username
     * @param password
     * @param request
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "/login/auth",method = RequestMethod.POST)
    public ResultInfo vendorLoginPage(@RequestParam("g-recaptcha-response") String captcha,

                                     @RequestParam("username") String username,
                                     @RequestParam("password") String password ,
                                     HttpServletRequest request,
                                     HttpSession session, Model model){

        ResultInfo resultInfo = null;
        Boolean captcha_result = false;

        String userAgent = request.getHeader("User-Agent");
        try {
            captcha_result = VerifyCaptcha.verify(captcha, userAgent,captcha_secretkey);
        } catch (IOException e) {
            e.printStackTrace();
            captcha_result =false;
        }
        //captcha_result = true;
        if(!captcha_result){
            resultInfo = ResultInfoFactory.getErrorResultInfo("Captcha Error!");
        }
        else {
            User userInfo = userService.loadUserByUsername(username);
            if (userInfo == null) {
                userInfo = userService.loadUserByEmail(username);
            }
            if (userInfo == null) {
                resultInfo = ResultInfoFactory.getErrorResultInfo("The user doesn't exist!");
            } else {
                if (userInfo.getPassword().equals(Md5Util.pwdDigest(password))) {

                    if(userInfo.getVendor_id() == 0){
                        resultInfo = ResultInfoFactory.getErrorResultInfo("The user doesn't exist!");
                    }
                    else {
                        Vendors vendor = vendorService.getVendorIDById(userInfo.getVendor_id());

                        session.setAttribute(Static.VENDOR_USER_OBJ, userInfo);
                        session.setAttribute(Static.VENDOR_OBJ, vendor);

                        if(userInfo.getActivate() == 0){
                            resultInfo = ResultInfoFactory.getSuccessResultInfo("noactivate");
                        }
                        else{

                            if(vendor.getStripe_verify() ==  -1)
                            {
                                if(!vendor.getStripe_acc_id().equals("")){
                                    Account account = stripeService.getStripeCustomAccount(vendor.getStripe_acc_id());
                                    if(account != null){
                                        if(account.getLegalEntity().getVerification().getStatus().equals("verified")){
                                            vendor.setStripe_verify(1);
                                            vendor.setReject_reason("");
                                            vendorService.updateVendorInfoById(vendor);

                                            Inbox_Notify obj = new Inbox_Notify();
                                            obj.setTitle("Successfully Verified!");
                                            obj.setContent("Your provided information successfully verified. You can request withdraw money. ");
                                            obj.setUpdated_at(UtilClass.convertTime(System.currentTimeMillis()));

                                            obj.setTo_user_ids(userInfo.getId() + ",");
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
                                                vendorService.updateVendorInfoById(vendor);

                                                Inbox_Notify obj = new Inbox_Notify();
                                                obj.setTitle("Verification rejected!");
                                                obj.setContent("Your provided information was rejected. Reason:" + reason);
                                                obj.setUpdated_at(UtilClass.convertTime(System.currentTimeMillis()));

                                                obj.setTo_user_ids(userInfo.getId() + ",");
                                                inboxNotifyService.saveInbox_Notify(obj);
                                            }
                                        }
                                    }
                                }
                            }


                            resultInfo = ResultInfoFactory.getSuccessResultInfo();
                        }
                    }
                } else {
                    resultInfo = ResultInfoFactory.getErrorResultInfo("Password is incorrect!");
                }
            }
        }
        return resultInfo;
    }

    /**
     * This is login rest api function.
     * @param request
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "/login/forgot_pass",method = RequestMethod.POST)
    public ResultInfo vendorForgotPass(@RequestParam("email") String email,
                                      HttpServletRequest request,
                                      HttpSession session, Model model){

        ResultInfo resultInfo = null;

        User userInfo = userService.loadUserByEmail(email);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        if (userInfo != null) {
            String token = UUID.randomUUID().toString().toUpperCase() + System.currentTimeMillis();
            long expire = System.currentTimeMillis() + 1000 * 60 * 15;
            Token token_class = new Token();
            token_class.setExpire("" + expire);
            token_class.setToken(token);
            tokenService.insertToken(token_class);

            String subject = "Please visit this site to reset your password";
            String url = domain + "/vendor/reset_pass/" + userInfo.getId() + "/" + token;

            String html = UtilClass.getResetPasswordHTML(url);

            // mailgun email service
            // MessageInstance res = UtilClass.sendMail(myemail, email, html, subject, mailgun_domain, mailgun_apikey);

            thirdPartyService.sendAwsSes(appEmail, userInfo.getEmail(), subject, html);


        }

        return resultInfo;
    }


    @RequestMapping(value = "/login/reset_pass",method = RequestMethod.POST)
    public ResultInfo vendorResetPass(@RequestParam("password") String password,
                                      @RequestParam("confpassword") String confpassword,
                                      @RequestParam("userid") String userid,
                                      @RequestParam("token") String token,
                                       HttpServletRequest request,
                                       HttpSession session, Model model){

        ResultInfo resultInfo = null;

        Token tokenModel = tokenService.getByToken(token);
        if(tokenModel == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("expire");
            return resultInfo;
        }

        long cur = System.currentTimeMillis();
        if(cur > Long.parseLong(tokenModel.getExpire())){
            resultInfo = ResultInfoFactory.getErrorResultInfo("expire");
            return resultInfo;
        }

        User userinfo = userService.loadUserById(Long.parseLong(userid));
        if(userinfo == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            return resultInfo;
        }

        if(!password.equals(confpassword)){
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            return resultInfo;
        }


        String new_pass = Md5Util.pwdDigest(password);
        userinfo.setPassword(new_pass);
        userService.updatePassword(userinfo);


        Vendors vendor = vendorService.getVendorIDById(userinfo.getVendor_id());

        session.setAttribute(Static.VENDOR_USER_OBJ, userinfo);
        session.setAttribute(Static.VENDOR_OBJ, vendor);

        if(userinfo.getActivate() == 0){
            resultInfo = ResultInfoFactory.getErrorResultInfo("invalid");
            return resultInfo;
        }

        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    /**
     * This is register rest api for vendor.
     * @param captcha
     * @param username
     * @param email
     * @param upassword
     * @param confirmpassword
     * @param request
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "/register/auth",method = RequestMethod.POST)
    public ResultInfo signupAuth(@RequestParam("g-recaptcha-response") String captcha,

                                 @RequestParam("username") String username,
                                 @RequestParam("email") String email ,
                                 @RequestParam("password") String upassword ,
                                 @RequestParam("confpassword") String confirmpassword ,
                                 HttpServletRequest request,
                                 HttpSession session, Model model) {

        ResultInfo resultInfo = null;
        Boolean captcha_result = false;

        String userAgent = request.getHeader("User-Agent");
        try {
            captcha_result = VerifyCaptcha.verify(captcha, userAgent,captcha_secretkey);
        } catch (IOException e) {
            e.printStackTrace();

            captcha_result =false;
        }

        if(!captcha_result){
            resultInfo = ResultInfoFactory.getErrorResultInfo("Captcha Error!");
        }
        else {
            Vendors vendor= new Vendors();

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

                    String tokenVal = UUID.randomUUID().toString().toLowerCase();
                    user.setToken(tokenVal);
                    user.setApiKey(UtilClass.generateAPIKey());

                    /////vendor table insertion//////
                    vendor.setLegal_name("");
                    //vendor.setVendorBalance(0);

                    long vendor_id = vendorService.insertVendorInfo(vendor);

                    user.setVendor_id(vendor.getId());
                    userService.insertUserInfo(user);
                    userInfo = userService.loadUserByEmail(user.getEmail());

                    Token new_token = new Token();
                    tokenVal = UUID.randomUUID().toString().toLowerCase();
                    new_token.setToken(tokenVal);
                    long expire = System.currentTimeMillis();
                    expire += (long) 1000 * 60 * 10;
                    new_token.setExpire("" + expire);

                    resultInfo = ResultInfoFactory.getSuccessResultInfo();
                    resultInfo.setObject(userInfo);

                    session.setAttribute(Static.VENDOR_USER_OBJ , user);
                    session.setAttribute(Static.VENDOR_OBJ , vendor);

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
     * This is vendor registration.
     * @param vendor
     * @param session
     * @return
     */
    @RequestMapping(value = "/registerVendor",method = RequestMethod.POST)
    public ResultInfo registerVendorPage(Vendors vendor, HttpSession session)
    {

        User user = (User)session.getAttribute(Static.VENDOR_USER_OBJ);
        ResultInfo resultInfo = null;

        if(user == null){
            resultInfo = ResultInfoFactory.getErrorResultInfo("Session Expired! Please Logout and login again.");
            return resultInfo;
        }

        if(vendor.getLegal_name() !=null && !vendor.getLegal_name().equals(""))
        {
             /*try {

               com.stripe.model.Token token = stripeService.createToken( vendor.getCard_number(), vendor.getExpiration(), vendor.getCvc_num()
                        , vendor.getAddress(), vendor.getZip_postal(), vendor.getState_province(),vendor.getAddress());*/
               /* if(token == null){
                    resultInfo = ResultInfoFactory.getErrorResultInfo(" Please put valid information for your card!");
                }
                else {*/

                    //userService.updateCustomerID(token.getId(), user.getVendor_id());
                    user.setCustomer_id("");
                    userService.updateInfo(user);

                    vendor.setId(user.getVendor_id());
                    vendorService.updateVendorInfoById(vendor);
                    //creating four plans each time when new vendor sign ups
                    //Plans will be activated when real price and virtual price are set
                    List<Plan> planList = planService.getPlanByVendorId(-1);
                    for(Plan plan: planList){
                        plan.setVendor_id((int)vendor.getId());
                        plan.setReal_price(0.00);
                        plan.setVr_price(0.00);
                        plan.setPlan_name(plan.getPlan_name() + " - " + vendor.getLegal_name());
                        plan.setFrequency(plan.getFrequency());
                        plan.setPlan_id(plan.getPlan_id()+"-vendor-"+vendor.getId());
                        planService.savePlan(plan);
                    }
                    resultInfo = ResultInfoFactory.getSuccessResultInfo();
                    resultInfo.setObject(vendor);

                    session.setAttribute(Static.VENDOR_OBJ, vendor);
           /* }catch (Exception e)
            {
                resultInfo = ResultInfoFactory.getErrorResultInfo(" Error  Occurred!");
            }*/

        }else
        {
            resultInfo = ResultInfoFactory.getErrorResultInfo("you have to put legal name");
        }

        return resultInfo;
    }


    @RequestMapping(value = "/profile/pwchange",method = RequestMethod.POST)
    public ResultInfo vendorProfileUpdate(User user, HttpSession session)
    {

        ResultInfo resultInfo = null;
        boolean validate = true;
        User userinfo = userService.loadUserByUsername(user.getUsername());

        if(userinfo != null && userinfo.getId() != user.getId()) {
            validate = false;
            resultInfo = ResultInfoFactory.getErrorResultInfo("The username is already exists. Please use another one!");
        }

        userinfo = userService.loadUserByEmail(user.getEmail());

        if(userinfo != null && userinfo.getId() != user.getId()) {
            validate = false;
            resultInfo = ResultInfoFactory.getErrorResultInfo("The email is already exists. Please use another one!");
        }

        if(!user.getNew_password().equals("")){
            if(user.getCur_password().equals("")){
                validate = false;
                resultInfo = ResultInfoFactory.getErrorResultInfo("Please input current password.");
            }

            if(!userinfo.getPassword().equals(Md5Util.pwdDigest(user.getCur_password()))){
                validate = false;
                resultInfo = ResultInfoFactory.getErrorResultInfo("Your current password is not correct. Please check again!");
            }
        }



        if(validate){
            userinfo = userService.loadUserById(user.getId());
            userinfo.setUsername(user.getUsername());
            userinfo.setEmail(user.getEmail());

            if(user.getNew_password().equals("")){
                userService.updateInfo(user);
            }
            else{
                user.setPassword(Md5Util.pwdDigest(user.getNew_password()));

                userService.updateInfo(user);
                userService.updatePassword(user);
            }

            resultInfo = ResultInfoFactory.getSuccessResultInfo();
        }
        return resultInfo;
    }

    @RequestMapping(value = "/profile/update",method = RequestMethod.POST)
    public ResultInfo vendorProfileChange(Vendors vendor, HttpSession session)
    {

        ResultInfo resultInfo = null;

        if(vendor == null ) {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The email is already exists. Please use another one!");
        }

        vendorService.updateVendorInfoById(vendor);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }


    @RequestMapping(value = "/reviews/delete",method = RequestMethod.POST)
    public ResultInfo deleteReviewById(Integer id){
        ResultInfo resultInfo = null;
        reviewService.deleteReviews(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }




/// question by dataset


    @RequestMapping(value = "/dataset/question/delete",method = RequestMethod.POST)
    public ResultInfo deleteQuestionById(Integer id){
        ResultInfo resultInfo = null;
        questionService.deleteQuestion( "" + id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    @RequestMapping("/dataset/question/answer/update")
    public ResultInfo updateAnswer(Question_anwsers answer, HttpSession session){
        ResultInfo resultInfo = null;
        User user = (User)session.getAttribute(Static.VENDOR_USER_OBJ);
        if(user != null){
            answer.setAnswer_by(user.getUsername());
        }
        questionService.updateAnswer(answer);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/dataset/question/answer/delete/{id}",method = RequestMethod.GET)
    public ResultInfo deleteAnswerById(@PathVariable Integer id){
        ResultInfo resultInfo = null;
        questionService.deleteAnswer(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping("/dataset/question/answer/create")
    public ResultInfo createAnswer(Question_anwsers question,HttpSession session)
    {
        ResultInfo resultInfo = null;
        User user = (User)session.getAttribute(Static.VENDOR_USER_OBJ);
        if(user != null){
            question.setAnswer_by(user.getUsername());
        }
        questionService.createAnswer(question);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }



    /////// question for inbox
    @RequestMapping("/inbox/question/create")
    public ResultInfo createInboxQuestion(Question_anwsers question,HttpSession session)
    {
        ResultInfo resultInfo = null;
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if(vendors != null){
            question.setQuestion_by_userid((int)vendors.getId());
        }

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);

        inboxQuestionService.createQuestion(question);

        ////////////// send mail to administrator

        String html = UtilClass.ContactAdminHtmlToUser( vendors.getBusiness_name(), question.getContent());
        boolean result =  thirdPartyService.sendAwsSes(admin_email, contact_email, "Vendor's Question", html);

        /////////////
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/inbox/question/delete",method = RequestMethod.POST)
    public ResultInfo deleteInboxQuestionById(Integer id){
        ResultInfo resultInfo = null;
        inboxQuestionService.deleteQuestion( "" + id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }


    @RequestMapping("/inbox/question/answer/update")
    public ResultInfo updateInboxAnswer(Question_anwsers answer, HttpSession session){
        ResultInfo resultInfo = null;
        User user = (User)session.getAttribute(Static.VENDOR_USER_OBJ);
        if(user != null){
            answer.setAnswer_by(user.getUsername());
        }
        inboxQuestionService.updateAnswer(answer);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/inbox/question/answer/delete/{id}",method = RequestMethod.GET)
    public ResultInfo deleteInboxAnswerById(@PathVariable Integer id){
        ResultInfo resultInfo = null;
        inboxQuestionService.deleteAnswer(id);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping("/inbox/question/answer/create")
    public ResultInfo createInboxAnswer(Question_anwsers question,HttpSession session)
    {
        ResultInfo resultInfo = null;
        question.setType(2);
        inboxQuestionService.createAnswer(question);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }





    //////////////////////////

    @RequestMapping(value = "/inbox/notification/update",method = RequestMethod.POST)
    public ResultInfo updateNotification(Inbox_Notify inbox_notify){

        ResultInfo resultInfo = null;

        Inbox_Notify inbox_notify11 = inboxNotifyService.getInbox_NotifyById(inbox_notify.getId());

        inbox_notify11.setTitle(inbox_notify.getTitle());
        inbox_notify11.setContent(inbox_notify.getContent());
        inbox_notify11.setTo_user_ids(inbox_notify.getTo_user_ids());

        inboxNotifyService.updateInbox_Notify(inbox_notify11);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/inbox/notification/delete",method = RequestMethod.POST)
    public ResultInfo deleteNotification(String id){
        ResultInfo resultInfo = null;
        inboxNotifyService.deleteInbox_Notify(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/inbox/notification/add",method = RequestMethod.POST)
    public ResultInfo addNotification(Inbox_Notify inbox_notify){

        ResultInfo resultInfo = null;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        inbox_notify.setUpdated_at(dateFormat.format(date));

        inboxNotifyService.saveInbox_Notify(inbox_notify);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }


    /////////////////

    @RequestMapping(value = "/account/addcard", method = RequestMethod.POST)
    public ResultInfo addCardSources(Pay_sources pay_sources, HttpSession session) {

        ResultInfo resultInfo = null;

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
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

        com.stripe.model.Token token = stripeService.createToken(card_num, card_exp, card_cvc, "unknow", "unknow", "unknow", "unknow");
        if (token != null) {
            pay_sources.setCustomer_id(token.getId());
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


    @RequestMapping(value = "/account/setCardDefault", method = RequestMethod.POST)
    public ResultInfo setCardDefault(Integer id, HttpSession session) {

        ResultInfo resultInfo = null;
        Pay_sources pay_sources = pay_sourceService.getPay_SourceById(id);
        pay_sources.setIs_default(1);
        pay_sourceService.updatePay_Source(pay_sources);

        pay_sourceService.setDefaultCard(pay_sources);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }





    //////// Email Marketing //////////////


    //////////////////////////


    @RequestMapping(value = "/marketing/update",method = RequestMethod.POST)
    public ResultInfo updateEmailMarketing(Email_lists email_lists){

        ResultInfo resultInfo = null;

        vendorMarketingService.updateEmail_lists(email_lists);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/marketing/delete",method = RequestMethod.POST)
    public ResultInfo deleteEmailMarketing(String id){
        ResultInfo resultInfo = null;
        vendorMarketingService.deleteEmail_lists(Integer.parseInt(id));
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        return resultInfo;
    }

    @RequestMapping(value = "/marketing/add",method = RequestMethod.POST)
    public ResultInfo addEmailMarketing(Email_lists email_lists, HttpSession session){

        ResultInfo resultInfo = null;

        String[] dataset_list = email_lists.getDataset_ids().split(",");
        for(int i = 0 ; i < dataset_list.length ; i ++){
            Integer id = 0;
            try {
                id = Integer.parseInt(dataset_list[i]);
            }
            catch (Exception e){

            }
            Data_sets data_sets = data_setsService.getData_setsById(id);
            if(data_sets == null){
                resultInfo = ResultInfoFactory.getErrorResultInfo("Datasets Error!");
                return resultInfo;
            }
        }

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        email_lists.setVendor_id(Integer.parseInt(vendor.getId()+""));
        email_lists.setCouponOrEmail(1);
        int nums = 0;
        String[] list = email_lists.getEmail_address().split(",");

        for(int i = 0 ; i < list.length ; i ++){
            if(!list[i].equals("")){
                nums ++;
            }
        }
        email_lists.setEmail_nums(nums);

         vendorMarketingService.saveEmail_lists(email_lists);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        resultInfo.setObject(email_lists.getId());

        return resultInfo;
    }

    @RequestMapping(value = "/couponmarketing/add",method = RequestMethod.POST)
    public ResultInfo addCouponMarketing(Email_lists email_lists, HttpSession session){

        ResultInfo resultInfo = null;
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        email_lists.setVendor_id(Integer.parseInt(vendor.getId()+""));
        email_lists.setCouponOrEmail(2);
        int nums = 0;
        String[] list = email_lists.getEmail_address().split(",");
        for(int i = 0 ; i < list.length ; i ++){
            if(!list[i].equals("")){
                nums ++;
            }
        }
        email_lists.setEmail_nums(nums);

        vendorMarketingService.saveEmail_lists(email_lists);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
        resultInfo.setObject(email_lists.getId());

        return resultInfo;
    }


    @RequestMapping(value = "/couponmarketing/sendmails", method = RequestMethod.POST)
    public ResultInfo couponMarketingEmailSend(
            Integer id
            ,HttpServletRequest request
            , HttpSession session) {

        ResultInfo resultInfo = null;

        Email_lists email_lists = vendorMarketingService.getEmail_listsById(id);

        ////////// send email template

        String[] emails = email_lists.getEmail_address().split(",");
        for(int i = 0 ; i < emails.length ; i ++){
            if(!"".equals(emails[i]) && emails[i].indexOf("@") > 0){

                String html_items = "";
                String[] idsAr = email_lists.getDataset_ids().split(",");
                for (int j = 0; j < idsAr.length; j++) {

                    ////////////////////

                    if(!"".equals(idsAr[j])) {
                        Coupon_manage coupon_manage = coupon_manageService.getById(Integer.parseInt(idsAr[j]));
                        Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(idsAr[j]));
                        if (coupon_manage != null) {

                            String url = domain ;
                            html_items += "<tr>" +
                                    "<td align='center' valign='top' style='    border-bottom: 3px solid #c1bfbf;padding: 2px;'>" +
                                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                                    "<tr style='padding-top:0;'>" +
                                    "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                                    coupon_manage.getCoupon() +
                                    "</td>" +
                                    "<td style='vertical-align: middle;    padding: 0px 10px;'>" + coupon_manage.getDiscount() + "$</td>" +
                                    "<td style='vertical-align: middle;' align='center' valign='top'>" +
                                    "<a href='" + url + "'>Go to</a>" +
                                    "</td>" +
                                    "</tr>" +
                                    "</table>" +
                                    "</td>" +
                                    "</tr>";
                        }
                    }
                }


                String html = UtilClass.marketingHtmlString(1, email_lists.getTitle(), email_lists.getContent());
                html += html_items;
                html +=  UtilClass.marketingHtmlString(2, email_lists.getTitle(), email_lists.getContent());

                thirdPartyService.sendAwsSes(contact_email, emails[i], "INVITATION", html);

            }
        }
        /////////////////////////
        email_lists.setSend_flag(1);
        vendorMarketingService.updateEmail_lists(email_lists);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }


        @RequestMapping(value = "/marketing/sendmails", method = RequestMethod.POST)
    public ResultInfo paymentDo(
            Integer id
            ,HttpServletRequest request
            , HttpSession session) {

        ResultInfo resultInfo = null;

        Email_lists email_lists = vendorMarketingService.getEmail_listsById(id);

        ////////// send email template

        String[] emails = email_lists.getEmail_address().split(",");
        for(int i = 0 ; i < emails.length ; i ++){
            if(!"".equals(emails[i]) && emails[i].indexOf("@") > 0){

                String html_items = "";
                String[] idsAr = email_lists.getDataset_ids().split(",");
                for (int j = 0; j < idsAr.length; j++) {

                    ////////////////////

                    if(!"".equals(idsAr[j])) {
                        Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(idsAr[j]));
                        if (data_sets != null) {
                            String url = domain + "/data_sets/cateid/" + data_sets.getData_category_id() + "/details/" + data_sets.getId();
                            html_items += "<tr>" +
                                    "<td align='center' valign='top' style='    border-bottom: 3px solid #c1bfbf;padding: 2px;'>" +
                                    "<table border='0' cellpadding='0' cellspacing='0' width='100%'>" +
                                    "<tr style='padding-top:0;'>" +
                                    "<td style='vertical-align: middle;    padding: 20px 0px 20px 7px;' align='left' valign='top'>" +
                                    data_sets.getName() +
                                    "</td>" +
                                    "<td style='vertical-align: middle;    padding: 0px 10px;'>" + data_sets.getOnetime_price() + "$</td>" +
                                    "<td style='vertical-align: middle;' align='center' valign='top'>" +
                                    "<a href='" + url + "'>Go to</a>" +
                                    "</td>" +
                                    "</tr>" +
                                    "</table>" +
                                    "</td>" +
                                    "</tr>";
                        }
                    }
                }


                String html = UtilClass.marketingHtmlString(1, email_lists.getTitle(), email_lists.getContent());
                html += html_items;
                html +=  UtilClass.marketingHtmlString(2, email_lists.getTitle(), email_lists.getContent());

                thirdPartyService.sendAwsSes(contact_email, emails[i], "INVITATION", html);

            }
        }

        /////////////////////////
        email_lists.setSend_flag(1);
        vendorMarketingService.updateEmail_lists(email_lists);
        resultInfo = ResultInfoFactory.getSuccessResultInfo();
        return resultInfo;
    }

    @RequestMapping(value = "/plan/update",method = RequestMethod.POST)
    public ResultInfo updatePlan(Plan plan){

        ResultInfo resultInfo = null;

        Plan data = planService.checkExist(plan);

        if (data == null) {
            String frequency = " ";
            if(plan.getFrequency().contains("30")) {
                frequency = "month";
            }else if(plan.getFrequency().contains("365")){
                frequency = "year";
            }
            if(planService.getPlanByPlanId(plan.getPlan_id()).getReal_price() > 0) {
                if (stripeService.retrivePlan(plan.getPlan_id()) != null) {
                    stripeService.deletePlan(plan.getPlan_id());
                }
            }
            stripeService.createPlan(plan.getPlan_id(), plan.getPlan_name(), frequency, plan.getReal_price());
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Update Success!");
            planService.updatePlan(plan);
        } else {
            resultInfo = ResultInfoFactory.getErrorResultInfo("The name already exists!");
        }
        return resultInfo;
    }

    @RequestMapping(value = "/coupon/add",method = RequestMethod.POST)
    public ResultInfo addCoupon(Coupon_manage coupon_manage, HttpSession session){

        ResultInfo resultInfo = null;

        String[] dataset_list = coupon_manage.getDataset_ids().split(",");
        for(int i = 0 ; i < dataset_list.length ; i ++){
            Integer id = 0;
            try {
                id = Integer.parseInt(dataset_list[i]);
            }
            catch (Exception e){

            }
            Data_sets data_sets = data_setsService.getData_setsById(id);
            if(data_sets == null){
                resultInfo = ResultInfoFactory.getErrorResultInfo("Datasets Error!");
                return resultInfo;
            }
        }

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        coupon_manage.setVendor_id(Integer.parseInt(vendor.getId()+""));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        coupon_manage.setCreated_date(dateFormat.format(date));
        coupon_manageService.saveCoupon(coupon_manage);
        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }

    @RequestMapping(value = "/withdraw/add",method = RequestMethod.POST)
    public ResultInfo addWithdraw(Withdraw withdraw, HttpSession session){

        ResultInfo resultInfo = null;
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        withdraw.setVendor_id(Integer.parseInt(vendor.getId()+""));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        withdraw.setOrder_date(dateFormat.format(date));
        //status 1 for pending or request
        withdraw.setStatus(1);
        withdrawService.saveWithdraw(withdraw);

        Trans_log trans_log = new Trans_log();
        trans_log.setVendor_id((int) vendor.getId());
        trans_log.setAmount(withdraw.getWithdraw_amount());
        trans_log.setDescription("Withdraw Request");
        trans_log.setDate(dateFormat.format(date));
        trans_log.setTrans_type(-1);
        trans_log.setStatus(0);
        trans_logService.saveTrans(trans_log);

        userService.updateBalance((int)vendor.getId(), -(withdraw.getWithdraw_amount()));


        Inbox_Notify inbox_notify = new Inbox_Notify();
        inbox_notify.setTitle("Withdraw Request");
        inbox_notify.setContent("Withdraw Request of $" + withdraw.getWithdraw_amount() + " through" + withdraw.getSource());
        inbox_notify.setTo_user_ids(1+",");
        inbox_notify.setUpdated_at(dateFormat.format(date));
        inboxNotifyService.saveInbox_Notify(inbox_notify);

        thirdPartyService.sendAwsSes(contact_email, admin_email, "Withdraw Request", "Withdraw Requested of Amount $" + withdraw.getWithdraw_amount());

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }

    @RequestMapping(value = "/withdraw/cancel/{id}",method = RequestMethod.POST)
    public ResultInfo cancelWithdraw(HttpSession session, @PathVariable int id) {

        ResultInfo resultInfo = null;
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();

        Withdraw withdraw = withdrawService.getById(id);
        if(withdraw.getVendor_id() != (int) vendor.getId()){
            resultInfo = ResultInfoFactory.getSuccessResultInfo("Error!");
            return resultInfo;
        }
        // status 2 for cancelled
        withdraw.setStatus(2);
        withdraw.setDescription("Withdraw Cancelled at"+dateFormat.format(date));
        withdrawService.updateWithdraw(withdraw);

        Trans_log trans_log = new Trans_log();
        trans_log.setVendor_id((int) vendor.getId());
        trans_log.setAmount(withdraw.getWithdraw_amount());
        trans_log.setDescription("Withdraw Cancelled By Vendor");
        trans_log.setDate(dateFormat.format(date));
        trans_log.setTrans_type(1);
        trans_log.setStatus(0);
        trans_logService.saveTrans(trans_log);
        userService.updateBalance((int)vendor.getId(), (withdraw.getWithdraw_amount()));

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }



    @RequestMapping(value = "/profile/verification",method = RequestMethod.POST)
    public ResultInfo VerifyInformation(Vendors vendor_information,
                                         HttpSession session,
                                        HttpServletRequest request) {

        ResultInfo resultInfo = null;
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        vendor.setFirst_name(vendor_information.getFirst_name());
        vendor.setLast_name(vendor_information.getLast_name());
        vendor.setAddress(vendor_information.getAddress());
        vendor.setCity(vendor_information.getCity());
        vendor.setState_province(vendor_information.getState_province());
        vendor.setZip_postal(vendor_information.getZip_postal());

        //previous code for sensitive information of sensitive datas
        vendor.setDob_day(vendor_information.getDob_day());
        vendor.setDob_month(vendor_information.getDob_month());
        vendor.setDob_year(vendor_information.getDob_year());
        vendor.setPersonal_id_number(vendor_information.getPersonal_id_number());
        vendor.setDocument(vendor_information.getDocument());
        //End of sensitive information

        // unsetting the values for sensitive information

        // End of sensitive information

        vendor.setSsn_last_4(vendor_information.getSsn_last_4());
        vendor.setSource(vendor_information.getSource());
        vendor.setRouting_number(vendor_information.getRouting_number());
        vendor.setAccount_number(vendor_information.getAccount_number());
        vendor.setCard_number(vendor_information.getCard_number());
        vendor.setCard_expiry(vendor.getCard_expiry());
        vendor.setCard_cvc(vendor.getCard_cvc());


        Account account = stripeService.createStripeCustomAcc(user,vendor, UtilClass.getClientIp(request));
        if(account == null ){
            resultInfo = ResultInfoFactory.getErrorResultInfo("Please provide correct information. Try again");
        }
        else{
            // remove unneccessary information for security.
            vendor.setDob_day("");
            vendor.setDob_month("");
            vendor.setDob_year("");
            vendor.setPersonal_id_number("");
            vendor.setDocument("");
//////////////////
            if(account.getType().equals("-1000")){
                resultInfo = ResultInfoFactory.getErrorResultInfo(account.getObject());
                return resultInfo;
            }
            if(vendor.getSource().equals("bank")){
                String route_num = vendor.getRouting_number();
                vendor.setRouting_last_4(route_num.substring(route_num.length() - 4, route_num.length()));
            }
            if(vendor.getSource().equals("card")){
                String card_num = vendor.getCard_number();
                vendor.setCard_last_4(card_num.substring(card_num.length() - 4, card_num.length()));
            }

            vendor.setStripe_acc_id(account.getId());

            if(account.getLegalEntity().getVerification().getStatus().equals("verified")){
                vendor.setStripe_verify(1);
            }
            else{
                if(account.getLegalEntity().getVerification().getStatus().equals("pending")){
                    vendor.setStripe_verify(-1);
                }
                else{
                    vendor.setStripe_verify(0);
                }
            }

            session.setAttribute(Static.VENDOR_OBJ, vendor);
            vendorService.updateVendorInfoById(vendor);

            resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");
           // resultInfo.setObject(response);
        }

        return resultInfo;
    }


    @RequestMapping(value = "/profile/attach_source",method = RequestMethod.POST)
    public ResultInfo attachSource(Vendors vendor_information,
                                        HttpSession session,
                                        HttpServletRequest request) {

        ResultInfo resultInfo = null;
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);

        vendor = vendorService.getVendorIDById(vendor.getId());

        if(vendor.getStripe_acc_id() == null || vendor.getStripe_acc_id().equals("")){
            resultInfo = ResultInfoFactory.getErrorResultInfo("You have to do verification first");

            return resultInfo;
        }

        if(vendor_information.getSource().equals("bank")){
            Account account = stripeService.attachBankAccount(vendor.getStripe_acc_id(), vendor_information.getRouting_number(), vendor_information.getAccount_number());

            if(account.getType().equals("-1000")){
                resultInfo = ResultInfoFactory.getErrorResultInfo(account.getObject());
                return resultInfo;
            }
            String route_num = vendor_information.getRouting_number();
            vendor.setRouting_last_4(route_num.substring(route_num.length() - 4, route_num.length()));
            vendor.setCard_last_4("");
            vendor.setStripe_verify(-1);
            vendorService.updateVendorInfoById(vendor);
        }
        if(vendor_information.getSource().equals("card")){

            String[] ar = vendor_information.getCard_expiry().split("/");

            Account account = stripeService.attachCardAccount(vendor.getStripe_acc_id(), vendor_information.getCard_number()
                    , ar[0], "20" + ar[1], vendor_information.getCard_cvc());

            if(account.getType().equals("-1000")){
                resultInfo = ResultInfoFactory.getErrorResultInfo(account.getObject());
                return resultInfo;
            }

            String card_num = vendor.getCard_number();
            vendor.setCard_last_4(card_num.substring(card_num.length() - 4, card_num.length()));
            vendor.setRouting_last_4("");
            vendorService.updateVendorInfoById(vendor);

        }

        resultInfo = ResultInfoFactory.getSuccessResultInfo("Success!");

        return resultInfo;
    }


}
