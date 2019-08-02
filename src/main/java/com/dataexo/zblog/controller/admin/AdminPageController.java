package com.dataexo.zblog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * This is controller class implement to first page request mapping.
 * It redirects to admin landing page.
 * It contains function which redirect to change password page and logout function
 */
@Controller
@RequestMapping("/admin")
public class AdminPageController extends AdminAbstractController {

    /**
     * This is default function for admin panel.
     * If you navigate 'http://localhost/admin' in web browser , then this function will be called.
     * This function redirect to /admin/user/list.
     * /admin/user/list is default page function int admin panel.
     * @return
     */
    @RequestMapping("/")
    public String init(){
        return "redirect:/admin/user/list";
    }

    /**
     * This is default function for admin panel.
     * If you navigate 'http://localhost/admin/home' in web browser , then this function will be called.
     * This function redirect to /admin/user/list.
     * /admin/user/list is default page function int admin panel.
     * @return
     */
    @RequestMapping("/home")
    public String homePage(){

        return "redirect:/admin/user/list";
    }

    /**
     * This is function implement to remove adminuser attribute from session vairable.
     * When you are going to logout from admin panel , this function will be called.
     * After remove the attribute , it will be redirected to /login.
     * @param session This is session variable which contains adminuser variable.
     * @return
     */
    @RequestMapping(value = "/logout/auth",method = RequestMethod.GET)
    public String logoutAuth(HttpSession session){

        session.removeAttribute("adminuser");
        return "redirect:/login";
    }

    /**
     * This is function implement to redirect reset password page.
     * When you are going to reset password in admin panel , this function will be called.
     * It redirects to 'admin/reset'
     * @return
     */
    @RequestMapping(value = "/changePassword",method = RequestMethod.GET)
    public String changePassword(){

        return "admin/reset";
    }
}
