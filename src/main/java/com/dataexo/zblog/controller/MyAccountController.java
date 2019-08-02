package com.dataexo.zblog.controller;


import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.AESencrp;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.*;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSubscriptionCollection;
import com.stripe.model.Subscription;
import org.apache.log4j.Logger;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.rmi.CORBA.Util;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This is controller implements user profile setting function.
 * You can see the ACCOUNT_SETTINGS menu item on the right-top corner menu item.
 * This controller is for account setting.
 * There are EDIT PROFILE , PASSWORD, Token , ....
 * You can update your profile (email , username) and reset the password in this page.
 * And also there is a membership page. You can upgrade or downgrade your membership.
 */
@Controller
@RequestMapping(value = "/account")
public class MyAccountController extends AbstractController {

    private static final Logger logger = Logger.getLogger(Data_setsController.class);

    @Value("${address.domain}")
    private String domain;

    @Value("${strip.pubkey}")
    private String pubkey;

    @Autowired
    private Pay_logService pay_logService;

    @Autowired
    private Data_categoryService data_categoryService;


    @Autowired
    private UserService userService;

    @Autowired
    private Pay_SourceService pay_sourceService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private Data_setsService data_setsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private FavouriteService favouriteService;

    @Autowired
    private PlanService planService;

    @Autowired
    private Sub_manageService sub_manageService;

    @Autowired
    private Coupon_userService coupon_userService;


    /**
     * This function is for My Favourite page
     *
     * @param session
     * @return
     */
    @RequestMapping("/favourite")
    public String favourite(Model model, HttpSession session) {

        if (!baseRequest(session, model)) {

            return "redirect:/";
        }

        User userinfo = (User) session.getAttribute("user");

        MyFavourite favourite = new MyFavourite();
        favourite.setSeries_id(-1);
        favourite.setUserid(userinfo.getId());
        List<MyFavourite> favouriteList = favouriteService.getFavouriteList(favourite);

        model.addAttribute("cateid", 1);
        model.addAttribute("favouriteList", favouriteList);

        return "user/favourite";
    }

    /**
     * This is "Edit Profile" page in edit profile
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(Model model, HttpSession session) {

        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        model.addAttribute("menu", "profile");
        model.addAttribute("cateid", 1);
        return "user/profile";
    }


    /**
     * This is "My Data" page in edit profile
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/mydata", method = RequestMethod.GET)
    public String mydata(Model model, HttpSession session) {
        if (!baseRequest(session, model)) {

            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        model.addAttribute("menu", "mydata");
        model.addAttribute("cateid", 1);

        return "user/mydata";
    }

    /**
     * This function is for user account page
     *
     * @param model
     * @param session
     * @return
     */

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public String password(Model model, HttpSession session) {

        if (!baseRequest(session, model)) {

            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        String resetPass = (String) session.getAttribute("resetPassword");

        if (resetPass == null) {
            model.addAttribute("reset_pass", "0");
        } else {
            model.addAttribute("reset_pass", "1");
        }
        model.addAttribute("cateid", 1);

        model.addAttribute("menu", "password");
        return "user/password";
    }

//    /**
//     * This function is for user membership page.
//     * If you upgrade your membership , you can be a subscription user.
//     * There are two types of membership. It includes single and enterprise membership.
//     * Each of the membership has two types : monthly and yearly.
//     * Each of the payment amount should be determined by admin in admin panel.
//     * Then if the user is subscription , they can download data-set anytime.
//     * But they have a limitation for download. This limitation also will be determined by admin.
//     *
//     * @param model
//     * @param session
//     * @return
//     */
//    @RequestMapping(value = "/membership", method = RequestMethod.GET)
//    public String membership(Model model, HttpSession session) {
//        User userinfo = (User) session.getAttribute("user");
//
//        if (!baseRequest(session, model)) {
//            model.addAttribute("resetPassword", "1");
//            return "redirect:/";
//        }
//        if (userinfo.getId() == -1) {
//            return "redirect:/account/user_account";
//        }
//        long expire = 0;
//        if (userinfo.getExpire_date() != null) {
//            expire = Long.parseLong(userinfo.getExpire_date());
//        }
//
//        if (userinfo.getMembership() != 0) {
//            long current = System.currentTimeMillis();
//            if (current <= expire) {
//                Date date = new Date(expire);
//                model.addAttribute("expire_time", "Expired at " + UtilClass.timeToString(expire, "yyyy.MM.dd"));
//            } else {
//                model.addAttribute("expire_time", "You membership already expired. Please purchase new membership.");
//            }
//        } else {
//            model.addAttribute("expire_time", "You are a free membership.");
//        }
//
//        Plan plan = planService.getPlanById(userinfo.getMembership());
//
//        Double price = 0.0;
//        if (plan != null)
//            price = plan.getReal_price();
//
//        model.addAttribute("membership_text1", UtilClass.membershipText1(userinfo.getMembership()));
//        model.addAttribute("membership_text2", UtilClass.membershipText2(userinfo.getMembership(), price));
//        model.addAttribute("membership", userinfo.getMembership());
//
//        plan = planService.getPlanById(userinfo.getUp_membership());
//        price = 0.0;
//        if (plan != null)
//            price = plan.getReal_price();
//
//        model.addAttribute("upcoming", userinfo.getUp_membership());
//        model.addAttribute("upcoming_membership", UtilClass.membershipText2(userinfo.getUp_membership(), price));
//        model.addAttribute("expiry", UtilClass.timeToString(expire, "yyyy.MM.dd"));
//
//        String resetPass = (String) session.getAttribute("resetPassword");
//        if (resetPass == null) {
//            model.addAttribute("reset_pass", "0");
//        } else {
//            model.addAttribute("reset_pass", "1");
//        }
//
//        model.addAttribute("menu", "membership");
//        model.addAttribute("cateid", 1);
//
//        return "user/membership";
//    }

    /**
     * This is membership manage page function. (/account/membership/manage)
     * In this function , it gets membership information
     * such as monthly payment price , monthly membership expire data.
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/membership/manage/{vendor_id}", method = RequestMethod.GET)
    public String membershipManage(Model model, HttpSession session, @PathVariable("vendor_id") int vendor_id) {

        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        Membership membership = new Membership();

        List<Plan> list = planService.getPlanByVendorId(vendor_id);


        // single monthly
        membership.setM_single(list.get(0).getReal_price());
        membership.setM_single_value(list.get(0).getVr_price());

        // enterprise monthly
        membership.setM_enterprise(list.get(1).getReal_price());
        membership.setM_enterprise_value(list.get(1).getVr_price());

        // single yearly
        membership.setY_single(list.get(2).getReal_price());
        membership.setY_single_value(list.get(2).getVr_price());

        // enterprise yearly
        membership.setY_enterprise(list.get(3).getReal_price());
        membership.setY_enterprise_value(list.get(3).getVr_price());

        //model attribute for getting plan_id
        model.addAttribute("m_single_id", list.get(0).getId());
        model.addAttribute("m_enterprise_id", list.get(1).getId());
        model.addAttribute("y_single_id", list.get(2).getId());
        model.addAttribute("y_enterprise_id", list.get(3).getId());


        String m_single = "" + membership.getM_single();
        String m_enterprise = "" + membership.getM_enterprise();
        String y_single = "" + membership.getY_single();
        String y_enterprise = "" + membership.getY_enterprise();

        int m_single_pos = m_single.indexOf(".");
        String dec = "";
        if (m_single_pos >= 0) {
            dec = m_single.substring(m_single_pos + 1);
            if (dec.length() == 1)
                dec += "0";
        } else {
            dec = "00";
        }

        model.addAttribute("m_single_int", m_single.substring(0, m_single_pos));
        model.addAttribute("m_single_dec", dec);

        int m_enterprise_pos = m_enterprise.indexOf(".");
        if (m_enterprise_pos >= 0) {
            dec = m_enterprise.substring(m_enterprise_pos + 1);
            if (dec.length() == 1)
                dec += "0";
        } else {
            dec = "00";
        }

        model.addAttribute("m_enterprise_int", m_enterprise.substring(0, m_enterprise_pos));
        model.addAttribute("m_enterprise_dec", dec);

        int y_single_pos = y_single.indexOf(".");
        if (y_single_pos >= 0) {
            dec = y_single.substring(y_single_pos + 1);
            if (dec.length() == 1)
                dec += "0";
        } else {
            dec = "00";
        }
        model.addAttribute("y_single_int", y_single.substring(0, y_single_pos));
        model.addAttribute("y_single_dec", dec);

        int y_enterprise_pos = y_enterprise.indexOf(".");
        if (y_enterprise_pos >= 0) {
            dec = y_enterprise.substring(y_enterprise_pos + 1);
            if (dec.length() == 1)
                dec += "0";
        } else {
            dec = "00";
        }

        User userinfo = (User) session.getAttribute("user");

        if (userinfo.getMembership() != 0) {
            long expire = Long.parseLong(userinfo.getExpire_date());
            model.addAttribute("expire_time", "You will pay in " + UtilClass.timeToString(expire, "yyyy.MM.dd"));

        } else {
            model.addAttribute("expire_time", "You are a free membership.");
        }


        model.addAttribute("y_enterprise_int", y_enterprise.substring(0, y_enterprise_pos));
        model.addAttribute("y_enterprise_dec", dec);
        model.addAttribute("pubkey", pubkey);
        model.addAttribute("membership_price", membership);
        List<Sub_manage> sub_manages = sub_manageService.getSubscriptionByUserId(userinfo.getId());
        int current_plan = 0;
        int upcoming_plan =0;
        String expiry_date = "";
        for(Sub_manage sub_manage : sub_manages){
            if(sub_manage.getVendor_id() == vendor_id && sub_manage.getStatus() == 1){
                current_plan = sub_manage.getPlan_id();
                expiry_date = sub_manage.getExpiry_date();
            }
            if(sub_manage.getVendor_id() == vendor_id && sub_manage.getStatus() == 0){
                upcoming_plan = sub_manage.getPlan_id();
            }
        }
        model.addAttribute("current_plan_type",current_plan);
        if (current_plan == 0)
            model.addAttribute("current_plan_expiry", "Free membership");
        else {
            model.addAttribute("current_plan_expiry", expiry_date);
        }
        model.addAttribute("upcoming_plan_type", upcoming_plan);

        /*
        Customer customer = stripeService.retriveCustomer(userinfo.getCustomer_id());

        if (customer == null) {
            model.addAttribute("current_plan_type", 0);
            model.addAttribute("current_plan_expiry", "Free membership");
            model.addAttribute("upcoming_plan_type", -1);
        } else {
            CustomerSubscriptionCollection subscriptions =  customer.getSubscriptions();
            int subCount = subscriptions.getData().size();

            if (subCount == 0) {
                model.addAttribute("current_plan_type", 0);
                model.addAttribute("current_plan_expiry", "Free membership");
                model.addAttribute("upcoming_plan_type", -1);
            } else if (subCount == 1){
                Subscription current = subscriptions.getData().get(0);
                model.addAttribute("current_plan_type", UtilClass.getPlanType(current.getPlan().getId()));
                model.addAttribute("current_plan_expiry", UtilClass.timeToString(current.getCurrentPeriodEnd() * 1000, "yyyy.MM.dd"));
                model.addAttribute("upcoming_plan_type", -1);
            } else {
                Subscription current = subscriptions.getData().get(1);
                model.addAttribute("current_plan_type", UtilClass.getPlanType(current.getPlan().getId()));
                model.addAttribute("current_plan_expiry", UtilClass.timeToString(current.getCurrentPeriodEnd() * 1000, "yyyy.MM.dd"));
                model.addAttribute("upcoming_plan_type", UtilClass.getPlanType(subscriptions.getData().get(0).getPlan().getId()));
            }
        }
        */

        model.addAttribute("cateid", 1);
        return "user/pch_membership";
    }


    /**
     * This is payment history page in edit profile.
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/payment_history", method = RequestMethod.GET)
    public String payment(Model model, HttpSession session) {

        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        model.addAttribute("cateid", 1);

        model.addAttribute("menu", "payment_history");
        return "user/pay_history";
    }


    /**
     * This is card management page in edit profile.
     *
     * @param model
     * @param session
     * @return
     */
    @RequestMapping(value = "/card_management", method = RequestMethod.GET)
    public String cardManagement(Model model, HttpSession session) {

        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        User user = (User) session.getAttribute("user");

        Pager<Pay_sources> pager = new Pager<Pay_sources>();
        pager.setSearch_str("");
        pager.setUser_id(Integer.parseInt("" + user.getId()));

        List<Pay_sources> dataList = pay_sourceService.loadPay_Source(pager);

        model.addAttribute("dataList", dataList);
        model.addAttribute("menu", "card_management");
        return "user/card_management";
    }

    //////

    /**
     * @param pager
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("/load/purchase")
    public String loadPurchase(Pager<Data_sets> pager, Model model, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        List<Purchase> purchaseList = new ArrayList<Purchase>();
        if (userinfo != null) {
            pager.setCateid(userinfo.getId() + "");
            if (pager.getSearch_str() == null) {
                pager.setSearch_str("");
            }
            purchaseList = purchaseService.loadPurchase(pager);
        }

        List<Data_sets> data_setsList = new ArrayList<Data_sets>();
        for (int i = 0; i < purchaseList.size(); i++) {
            Data_sets data_sets = data_setsService.getData_setsById(purchaseList.get(i).getDataset_id());
            data_sets.setPurchase_id(purchaseList.get(i).getId());
            data_sets.setToken(purchaseList.get(i).getToken());
            Date date = new Date(Long.parseLong(purchaseList.get(i).getOrder_date()));
            DateFormat df = new SimpleDateFormat("MMMM d, yyyy");
            String reportDate = df.format(date);
            data_sets.setOrder_date(reportDate);

            Token token = tokenService.getByToken(purchaseList.get(i).getToken());
            date = new Date(Long.parseLong(token.getExpire()));
            String expire_on = df.format(date);
            data_sets.setExpries_on_date(expire_on);

            data_setsList.add(data_sets);
        }


        model.addAttribute("data_setsList", data_setsList);
        model.addAttribute("cateid", pager.getCateid());

        model.addAttribute("domain", domain);

        return "subpart/mydata-sets";
    }

    @RequestMapping("/load/payment")
    public String loadPaymentLog(Pager pager, HttpSession session, Model model) {

        User userinfo = (User) session.getAttribute("user");
        pager.setCateid("" + userinfo.getId());
        List<Pay_log> pay_logsList = pay_logService.loadPay_log(pager);
        for (int i = 0; i < pay_logsList.size(); i++) {
            Pay_log pay_log = pay_logsList.get(i);
            Long time = Long.parseLong(pay_log.getTime());
            Date now = new Date(time);

            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            pay_log.setTime(df.format(now));
            pay_logsList.set(i, pay_log);
        }
        model.addAttribute("pay_logsList", pay_logsList);

        return "subpart/paylog_list";
    }

    /**
     * This function implenets to activate the user's account.
     * When the user sign up in this page , the email will be sent to user to activate the account.
     * This function is activate function.
     *
     * @param id      : This is user's id
     * @param tokenid : this token will be used to check expire date. If the token is already expired , you cann't activate your account.
     * @return
     */

    @RequestMapping("/activate/{id}/{tokenid}")
    public String activateAccount(@PathVariable String id, @PathVariable String tokenid) {

        Token token = tokenService.getByToken(tokenid);
        long cur = System.currentTimeMillis();
        if (cur > Long.parseLong(token.getExpire())) {

            return "error/token_expire";
        } else {

            userService.activeAccount(Integer.parseInt(id));
        }
        return "activated";
    }
    @RequestMapping("/mycart")
    public String mycart(HttpSession session, Model model) {

        baseRequest(session, model);

        float sum = 0, download_sum = 0, discount_value = 0;
        List<Data_sets> data_setsList = new ArrayList<Data_sets>();

        User userinfo = (User) session.getAttribute("user");

        Pager pager = new Pager();
        if (userinfo == null) {
            pager.setSearch_str(session.getId());
            pager.setCateid("-1");
            bucketService.initPage(pager);

            List<Bucket> bucketList = null;

            if (pager.getTotalCount() > 0) {
                bucketList = bucketService.loadBucket(pager);

                for (int i = 0; i < bucketList.size(); i++) {
                    Data_sets data_sets = data_setsService.getData_setsById(bucketList.get(i).getDataset_id());
                    data_sets.setPurchase_id(bucketList.get(i).getId());
                    Date date = new Date(Long.parseLong(bucketList.get(i).getOrder_date()));
                    DateFormat df = new SimpleDateFormat("MMMM d, yyyy");
                    String reportDate = df.format(date);
                    data_sets.setOrder_date(reportDate);
                    data_setsList.add(data_sets);

                    Coupon_user coupon_user = coupon_userService.getByOrderId( (long)(-1 *bucketList.get(i).getId()));
                    if(coupon_user != null){
                        int discount = coupon_user.getDiscount();
                        discount_value += (data_sets.getOnetime_price() * discount )/ 100;
                    }
                    sum += data_sets.getOnetime_price();

                    int vendor_id = (int) data_sets.getVendor_id();
                    List<Plan> planList = planService.getPlanByVendorId(vendor_id);
                    for(int j = 0; j < planList.size() - 1; j ++){
                        Plan plan = planList.get(j);

                        Sub_manage sub_manage =  sub_manageService.getSubscriptionByUserIdAndPlanId(plan.getId(),userinfo.getId());

                        if(sub_manage != null){
                            data_sets.setSub_available(1);
                            break;
                        }
                    }
                    if(data_sets.getSub_available() == 1)
                    {
                        download_sum += data_sets.getDownload_price();
                    }
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
                    Data_sets data_sets = data_setsService.getData_setsById(bucketList.get(i).getDataset_id());
                    data_sets.setPurchase_id(bucketList.get(i).getId());
                    Date date = new Date(Long.parseLong(bucketList.get(i).getOrder_date()));
                    DateFormat df = new SimpleDateFormat("MMMM d, yyyy");
                    String reportDate = df.format(date);

                    data_sets.setOrder_date(reportDate);
                    data_setsList.add(data_sets);

                    Coupon_user coupon_user = coupon_userService.getByOrderId( -1 *(long)bucketList.get(i).getId());
                    if(coupon_user != null){
                        int discount = coupon_user.getDiscount();
                        discount_value += (data_sets.getOnetime_price() * discount )/ 100;
                    }
                    sum += data_sets.getOnetime_price();

                    int vendor_id = (int) data_sets.getVendor_id();
                    List<Plan> planList = planService.getPlanByVendorId(vendor_id);
                    for(int j = 0; j < planList.size() - 1 ; j ++){
                        Plan plan = planList.get(j);

                        Sub_manage sub_manage =  sub_manageService.getSubscriptionByUserIdAndPlanId(plan.getId(),userinfo.getId());

                        if(sub_manage != null){
                            data_sets.setSub_available(1);
                            break;
                        }
                    }
                    if(data_sets.getSub_available() == 1)
                    {
                        download_sum += data_sets.getDownload_price();
                    }
                }
            }

        }
        float totalWithoutDiscout = sum;
        sum -= discount_value;
        model.addAttribute("items", pager.getTotalCount());

        model.addAttribute("download_sum", download_sum);
        model.addAttribute("sum", String.format(java.util.Locale.US,"%.2f", sum));
        model.addAttribute("discount", discount_value);
        model.addAttribute("total", String.format(java.util.Locale.US,"%.2f", totalWithoutDiscout));
        model.addAttribute("itemsList", data_setsList);

        return "user/mycart";

    }

    @RequestMapping(path = "/subscriptions")
    public String allSubscriptions(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        model.addAttribute("menu", "subscriptions");

        return "user/subscriptions";

    }


    @RequestMapping("/load/initSubscriptionPage")
    @ResponseBody
    public Pager initSubscriptionsPage(Pager<Plan> pager, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        pager.setUser_id((int) userinfo.getId());
        if(pager.getSearch_str() == null) {
            pager.setSearch_str("");
        }
        planService.initUnsubscribedPage(pager);
        return pager;
    }

    @RequestMapping("/load/plans")
    public String loadSubscriptions(Pager pager, HttpSession session, Model model) {
        User userinfo = (User) session.getAttribute("user");
        pager.setUser_id((int) userinfo.getId());
        if(pager.getSearch_str() == null) {
            pager.setSearch_str("");
        }
        List<Plan> planList = planService.getUnsubscribedPlanByUserId(pager);
        model.addAttribute("planList", planList);

        return "subpart/subscriptions_list";
    }

    @RequestMapping("/load/initCancelPage")
    @ResponseBody
    public Pager initCancelPage(Pager<Sub_manage> pager, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        pager.setUser_id((int) userinfo.getId());
        sub_manageService.initCancelPage(pager);
        return pager;
    }

    @RequestMapping(path = "/past")
    public String pastSubscriptions(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        model.addAttribute("menu", "past");
        return "user/past";

    }

    @RequestMapping("/load/past")
    public String loadCanelSubscriptions(Pager pager, HttpSession session, Model model) {
        User userinfo = (User) session.getAttribute("user");
        pager.setUser_id((int) userinfo.getId());
        List<Sub_manage> subscriptionList = sub_manageService.loadCancelSubscription(pager);
        model.addAttribute("subscriptionList", subscriptionList);

        return "subpart/past_list";
    }

    @RequestMapping(path = "/active")
    public String activeSubscriptions(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            model.addAttribute("resetPassword", "1");
            return "redirect:/";
        }

        model.addAttribute("menu", "active");
        return "user/active";
    }

    @RequestMapping("/load/initActivePage")
    @ResponseBody
    public Pager initActivePage(Pager<Sub_manage> pager, HttpSession session) {
        User userinfo = (User) session.getAttribute("user");
        pager.setUser_id((int) userinfo.getId());
        sub_manageService.initPage(pager);
        return pager;
    }

    @RequestMapping("/load/active")
    public String loadActiveSubscriptions(Pager pager, HttpSession session, Model model) {
        User userinfo = (User) session.getAttribute("user");
        pager.setUser_id((int) userinfo.getId());
        List<Sub_manage> subscriptionList = sub_manageService.loadSubscriptions(pager);
        model.addAttribute("subscriptionList", subscriptionList);

        return "subpart/active_list";
    }

}