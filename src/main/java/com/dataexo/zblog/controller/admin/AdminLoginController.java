package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.Md5Util;
import com.dataexo.zblog.util.ResultInfo;
import com.dataexo.zblog.util.ResultInfoFactory;
import com.dataexo.zblog.util.VerifyCaptcha;
import com.dataexo.zblog.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This is controller class implements to rest api for admin user.
 * It contains login auth , check login auth and reset password auth.
 * For example, when you are going to login to admin panel , you should call /admin/login/auth using ajax
 * then it will redirect admin landing page.
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@RestController
public class AdminLoginController {

    @Value("${captcha.secretkey}")
    public String captcha_secretkey;

    @Autowired
    private UserService userService;

    /**
     * This function implement to save admin login user to session.
     * The sesion variable name is 'adminuser'
     * Then it will be used to check if the admin logged in or not.
     * @param session This is HttpSession variable which contains sesion values.
     *                This will be used to check that the user is logged in or not.
     * @return The return class is ResultInfo which contains resultCode and errorInfo
     *          If the resultCode is success , the login auth has been set to session successfully.
     *          If the resultCode is fail , the login auth is fail. The error information will be set to errorInfo variable.
     */

    @RequestMapping(value = "/admin/login/auth",method = RequestMethod.POST)
    public ResultInfo adminloginPage(@RequestParam("g-recaptcha-response") String captcha,

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

        if(!captcha_result){
            resultInfo = ResultInfoFactory.getErrorResultInfo("Captcha Error!");
        }
        else {
            User userInfo = userService.loadUserByAdminUsername(username);
            if (userInfo == null) {
                resultInfo = ResultInfoFactory.getErrorResultInfo("The user doesn't exist!");
            } else {
                if (userInfo.getPassword().equals(Md5Util.pwdDigest(password))) {
                    session.setAttribute("adminuser", userInfo);
                    resultInfo = ResultInfoFactory.getSuccessResultInfo();
                } else {
                    resultInfo = ResultInfoFactory.getErrorResultInfo("Password is incorrect!");
                }
            }
        }
        return resultInfo;
    }

    /**
     * This is function implement to check admin user is logged in or not.
     * If the resultCode is success , the admin user is already logged in.
     * @param password It will be used to check admin user authenticate.
     * @return The return class is ResultInfo which contains resultCode and errorInfo
     *          If the resultCode is success , the admin user logged in successfully.
     */
    @RequestMapping(value = "/admin/check_login/auth",method = RequestMethod.POST)
    public ResultInfo check_login(String password){
        ResultInfo resultInfo = null;
        User userInfo = userService.loadUserByAdminUsername("admin");
        if (userInfo==null){
            resultInfo =  ResultInfoFactory.getErrorResultInfo("The admin error!");
        }else{
            if (userInfo.getPassword().equals(Md5Util.pwdDigest(password))){
                resultInfo = ResultInfoFactory.getSuccessResultInfo();
            }else {
                resultInfo = ResultInfoFactory.getErrorResultInfo("Password is incorrect!");
            }

        }
        return resultInfo;
    }

    /**
     * This function implement to reset the admin password.
     * This function will be used to /admin/reset.html to reset the password
     * @param password It is password for reset.
     * @return The return class is ResultInfo which contains resultCode and errorInfo
     *          If the resultCode is success , the reset password is done successfully.
     */
    @RequestMapping(value = "/admin/reset/auth",method = RequestMethod.POST)
    public ResultInfo reset(String password){
        ResultInfo resultInfo = null;
        User userInfo = userService.loadUserByAdminUsername("admin");
        if (userInfo==null){

            resultInfo =  ResultInfoFactory.getErrorResultInfo("The admin error!");
        }else{
            User newuser = new User();
            newuser.setUsername("admin");
            newuser.setId(userInfo.getId());
            newuser.setPassword(Md5Util.pwdDigest(password));
            userService.resetPassword(newuser);
            resultInfo = ResultInfoFactory.getSuccessResultInfo();
        }
        return resultInfo;
    }

}
