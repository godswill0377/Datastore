package com.dataexo.zblog.controller;

import com.dataexo.zblog.service.BucketService;
import com.dataexo.zblog.service.Data_categoryService;
import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.util.AESencrp;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.util.UtilClass;
import com.dataexo.zblog.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * This is base controller implements to get user's basic information
 * such as user's membership information , membership expire date, current balance, ...
 *
 */
@Controller
public abstract class AbstractController  {


    @Resource
    private BucketService bucketService;

    @Resource
    private Data_categoryService data_categoryService;
    @Resource
    private Data_setsService data_setsService;

    @Value("${address.domain}")
    private String domain;

    @Value("${address.email}")
    private String email;

    @Value("${strip.pubkey}")
    private String strip_pubkey;

    @Value("${captcha.sitekey}")
    public String captcha_sitekey;

    @Value("${ssoauth.jforum_url}")
    private String jforum_url;

    @Value("${ssoauth.zeppelin_url}")
    private String zeppelin_url;

    /**
     * This is base function of all controller's.
     *
     * @param session
     * @param model
     * @return
     */
    public boolean baseRequest(HttpSession session,Model model )
    {

        List<Data_category> data_categoryList= data_categoryService.findAll();
        model.addAttribute("data_categoryList",data_categoryList);
        model.addAttribute("domain",domain);
        model.addAttribute("contact_email",email);


        model.addAttribute("sitekey",captcha_sitekey);
        model.addAttribute("jforum_url",jforum_url);
        model.addAttribute("zeppelin_url",zeppelin_url);
        User userinfo = (User) session.getAttribute("user");

        if(userinfo == null){
            Pager pager = new Pager();
            pager.setCateid("-1");
            pager.setSearch_str(session.getId());
            bucketService.initPage(pager);

            model.addAttribute("userid","0");
            model.addAttribute("membership","0");
            model.addAttribute("signed_view","none");
            model.addAttribute("items",pager.getTotalCount());
            model.addAttribute("username","");
            model.addAttribute("balance","0");
            return false;
        }
        else{

            Pager pager = new Pager();
            pager.setSearch_str("");
            pager.setCateid(userinfo.getId() + "");
            bucketService.initPage(pager);
            pager.setStart(0);
            pager.setLimit(pager.getTotalCount());

            int total_unread = 0;

            /*try {
                String response = UtilClass.sendGet(jforum_url + "/jforum.page?module=ajax&action=getUnRead&username="+userinfo.getUsername());
                total_unread = Integer.parseInt(response);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("JForum is not running.");
            }*/

            model.addAttribute("total_unread", total_unread);

//            if(userinfo.getMembership() != 0){
//                long expire = Long.parseLong(userinfo.getExpire_date());
//                long cur = System.currentTimeMillis();
//                if(cur > expire){
//                    model.addAttribute("expired","1");
//                }
//                else{
//                    model.addAttribute("expired","0");
//                }
//            }
//            else{
//                model.addAttribute("expired","1");
//            }
            model.addAttribute("items",pager.getTotalCount());

            model.addAttribute("userid",userinfo.getId());
            model.addAttribute("signed_view","block");

            model.addAttribute("user_apikey",userinfo.getApiKey());
            model.addAttribute("userinfo",userinfo);
            model.addAttribute("username",userinfo.getUsername());


            model.addAttribute("balance",""+userinfo.getBalance());

            //model.addAttribute("membership",userinfo.getMembership());

            // key generate

            String key_data = UUID.randomUUID().toString().toLowerCase();
            try {
                String enc_data  = Md5Util.pwdDigest(key_data);
                model.addAttribute("enc_data",enc_data);
                model.addAttribute("key_data",key_data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        model.addAttribute("pubkey",strip_pubkey);
        return true;
    }

}