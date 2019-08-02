package com.dataexo.zblog.controller.vendor;


import com.dataexo.zblog.controller.AbstractController;
import com.dataexo.zblog.mapper.Data_setsMapper;
import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.util.VerifyCaptcha;
import com.dataexo.zblog.vo.*;
import com.stripe.model.Account;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/vendor")
public class VendorMainController extends VendorAbstractController {

    private static final Logger logger = Logger.getLogger(VendorMainController.class);

    @Value("${captcha.secretkey}")
    public String captcha_secretkey;

    @Value("${ssoauth.auth_url}")
    public String ssoAuthUrl;


    @Value("${captcha.sitekey}")
    public String captcha_sitekey;

    @Value("${ssoauth.logout}")
    public String logout;

    @Value("${ssoauth.jforum_url}")
    public String jforum_url;


    @Value("${ssoauth.login_url}")
    public String login_url;

    @Value("${address.domain}")
    public String domain;

    @Value("${address.email}")
    public String support_email;

    @Value("${token.auth.timeout}")
    public int tokenAuthTimeout;

    @Value("${server.mode}")
    public String serverMode;

    @Autowired
    private UserService userService;

    @Resource
    private Data_setsService data_setsService;

    @Resource
    private  ReviewService reviewService;

    @Resource
    private VendorService vendorService;

    @Resource
    private Trans_logService trans_logService;

    @Resource
    private StripeService stripeService;


    @Resource
    private PaymentService paymentService;

    @Resource
    private InboxNotifyService inboxNotifyService;


    @Autowired
    private TokenService tokenService;

    @RequestMapping("")
    public String vendorFirstPage() {

        return "redirect:/vendor/login";
    }

    /**
     * This is vendor login page .
     * In this page , the vendor has to do login with username and password.
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/login")
    public String vendorLoginPage(HttpSession session, Model model, HttpServletRequest request) {

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user != null && vendor != null) {
            if (user.getActivate() == 1) {
                return "redirect:/vendor/dashboard";
            } else {
                if (vendor.getLegal_name() != null && !vendor.getLegal_name().equals("")) {
                    return "redirect:/vendor/reg-auth-result";
                }
            }
        }

        model.addAttribute("sitekey", captcha_sitekey);
        return "vendor/vendorLogin";
    }

    /**
     * this is vendor signup page.
     * In this page , the vendor has to setup username , email and password for login cridential.
     *
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/signup")
    public String vendorSignup(HttpSession session, Model model) {

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user != null && vendor != null) {
            if (user.getActivate() == 1) {
                return "redirect:/vendor/dashboard";
            } else {
                if (vendor.getLegal_name() != null && !vendor.getLegal_name().equals("")) {
                    return "redirect:/vendor/reg-auth-result";
                }
            }
        }


        model.addAttribute("sitekey", captcha_sitekey);

        return "vendor/vendorRegisteration";
    }

    /**
     * This is vendor information setup page.
     * In this page , the vendors have to setup their own information.
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/set-info")
    public String vendorSetInfo(HttpSession session, Model model) {

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user != null && vendor != null) {
            if (user.getActivate() == 1) {
                return "redirect:/vendor/dashboard";
            } else {
                if (vendor.getLegal_name() != null && !vendor.getLegal_name().equals("")) {
                    return "redirect:/vendor/reg-auth-result";
                }
            }
        }

        model.addAttribute("sitekey", captcha_sitekey);

        if (user == null) {
            return "redirect:/vendor/login";
        }

        return "vendor/set-info";
    }

    /**
     * This is waiting authentication page from admin
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/reg-auth-result")
    public String vendorRegAuthenticate(HttpSession session, Model model) {

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user != null && vendor != null) {
            user = userService.loadUserById(user.getId());
            if(user != null) {
                if (user.getActivate() == 1) {

                    session.setAttribute(Static.VENDOR_USER_OBJ, user);
                    return "redirect:/vendor/dashboard";
                } else {

                }
            }
        }


        model.addAttribute("sitekey", captcha_sitekey);

        return "vendor/reg_authenticate";
    }

    /**
     * This is logout function.
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session, Model model) {

        session.removeAttribute(Static.VENDOR_USER_OBJ);
        session.removeAttribute(Static.VENDOR_OBJ);

        return "redirect:/vendor/login";
    }

    /**
     * this redirects to login page.
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/")
    public String vendorMain(HttpSession session, Model model) {
        return "redirect:/vendor/login";
    }


    /**
     * This is main vendor dashboard.
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/dashboard")
    public String vendorDashboard(HttpSession session, Model model, Pager<Data_sets> pager) {

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        Vendors vendor =(Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id((int)vendor.getId());

        pager.setTrans_type(1);

        pager.setStatus(1);

        model.addAttribute("sales", trans_logService.getTotalSales((int)vendor.getId()));

        model.addAttribute("datasets", data_setsService.loadData_getsByvendor_id(pager));

        model.addAttribute("datasets_count", data_setsService.getTotalCount(pager));

//        model.addAttribute("likes", reviewService.getTotalReview(pager));

        // This one is for earning graph
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();

        model.addAttribute("dates", "From "+ dateFormat.format(DateUtils.addYears(date, -1)) +" to "+ dateFormat.format(date));

        String [] labels = new String[12];

        double [] earnings = new double[12];

        DateFormat monthFormat = new SimpleDateFormat("MMMM");

        dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00");

        DateFormat dateToFormat = new SimpleDateFormat("yyyy-MM-dd 24:00");

        Date temp = new Date(date.getYear(), date.getMonth(),1);

        for(int i = labels.length-1; i>=0 ; i--){
            labels[i] = monthFormat.format(date);

            String dateFrom = dateFormat.format(temp);

            String dateTo = dateToFormat.format(date);

            pager.setDateTo(dateTo);

            pager.setDateFrom(dateFrom);

            earnings[i] = paymentService.getAmountByDateRange(pager);

            date = DateUtils.addMonths(date, -1);
            temp = DateUtils.addMonths(temp, -1);
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            date = calendar.getTime();

        }

        model.addAttribute("labels", labels);

        model.addAttribute("earnings", earnings);

        Double total= paymentService.getTotalAmount((int)vendor.getId());
        if(total == null){
            total = 0.0;
        }
        model.addAttribute("total", total);


        return "vendor/dashboard";
    }

    /**
     * This is vendor profile page.
     * In this page , the user can update username , email , password.
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/pwchange")
    public String vendorProfile(HttpSession session, Model model) {

        User user =(User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor =(Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user == null || vendor == null) {
            return "redirect:/vendor/login";
        }

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        model.addAttribute("user" , user);
        model.addAttribute("vendor" , vendor);

        return "vendor/pwchange";
    }

    @RequestMapping("/profile")
    public String vendorProfileUpdate(HttpSession session, Model model) {

        User user =(User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor =(Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user == null || vendor == null) {
            return "redirect:/vendor/login";
        }
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        vendor = vendorService.getVendorIDById(vendor.getId());

        model.addAttribute("vendor" , vendor);

        model.addAttribute("user" , user);

        return "vendor/profile";
    }

    @RequestMapping("/verification")
    public String vendorVerification(HttpSession session, Model model) {

        User user =(User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor =(Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if (user == null || vendor == null) {
            return "redirect:/vendor/login";
        }
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        vendor = vendorService.getVendorIDById(vendor.getId());
        if(vendor.getStripe_acc_id() == null){
            vendor.setStripe_acc_id("");
        }
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

                        obj.setTo_user_ids(user.getId() + ",");
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

                            obj.setTo_user_ids(user.getId() + ",");
                            inboxNotifyService.saveInbox_Notify(obj);
                        }
                    }
                }
            }
        }


        session.setAttribute(Static.VENDOR_OBJ, vendor);

        if(vendor.getReject_reason() == null) vendor.setReject_reason("");

        model.addAttribute("vendor" , vendor);
        model.addAttribute("user" , user);

        if(!vendor.getStripe_acc_id().equals("") && vendor.getReject_reason() != "" && vendor.getStripe_verify()==0){
            model.addAttribute("reject_reason" , vendor.getReject_reason());
        }
        return "vendor/verification";
    }

    @RequestMapping("/account_source")
    public String accountSource(HttpSession session, Model model) {

        User user =(User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor =(Vendors) session.getAttribute(Static.VENDOR_OBJ);

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        if(vendor.getStripe_verify() != 1){
            model.addAttribute("message", "Please do verification.");
            return "vendor/withdraw/stripe_acc_error";
        }
        model.addAttribute("vendor" , vendor);
        model.addAttribute("user" , user);

        return "vendor/account_source";
    }

    @RequestMapping("/forget_pass_page")
    public String forgetPassPage(HttpSession session, Model model) {

        return "vendor/forgot_pass";
    }

    @RequestMapping(value = "/reset_pass/{userid}/{token}", method = RequestMethod.GET)
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

        model.addAttribute("userid",userinfo.getId());
        model.addAttribute("token",token);

        return "vendor/reset_pass";

    }


}