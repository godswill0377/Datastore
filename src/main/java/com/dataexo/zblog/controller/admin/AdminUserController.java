package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.ResultInfo;
import com.dataexo.zblog.util.ResultInfoFactory;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;
import org.json.HTTP;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * This is controller class implements an management of user table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used UserService class to get data from database.
 * UserService is for user table.
 *
 * <p> This class is integrate with templates/admin/user folder
 *  for example  , when the return result is "admin/user/table" , it means admin/user/table.html file
 *  so the browser loads the admin/user/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put user attribute to model variable ,
 * then you can use user attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${user}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/user")
public class AdminUserController extends AdminAbstractController {

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_table.
     * It contains findAll,saveuser,checkExist,loaduser,getuserById ...
     */
    @Resource
    private UserService userService;


    /**
     * This is function implements to get total count of table data and total pages.
     * So it can be used to implement page navigation function
     * If you call this function in frontend using ajax ,
     * this function returns pager information such total pages and total data count in json format
     * @param pager It is used for page navigation function.
     * @param model It is for maaping the data to frontend using thymeleaf
     * @return pager This is return param with total pages and total data count.
     *                  So it will determine page navigation bar.
     */
    @RequestMapping("/initPage")
    @ResponseBody

    public Pager initPage(Pager<User> pager,HttpSession session, Model model){
        pager.setVendor_flag(0);
        userService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/user/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/user/edit.html file.
     * @param id This is edited item id. It come from url. As you can see,
     *  if you navigate to "/edit/1" in browser, the id should be 1.
     * @param model  It is for maaping the data to frontend using thymeleaf
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

       User user = userService.loadUserById(id);
       String role =  user.getRole();
       if(role.equals("admin")){
           return "error/404";
       }

       if(user.getExpire_date() == null){
           user.setExpire_date("0");
       }
       if(user.getExpire_date().equals("")){
           user.setExpire_date("0");
       }
       long exp = Long.parseLong( user.getExpire_date());
       Date date = new Date(exp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        user.setExpire_date(str);
        model.addAttribute("userinfo",user);
        return "admin/user/userEdit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/admin/user/add , then this function will be called.
     * This function is mapped admin/user/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/user/userAdd";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/user/list , then this function will be called.
     * This function is mapped admin/user/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/user/userList";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/user/list , then this function will be called to get table data by pager.
     * This function is mapped admin/user/table.html file.
     * @param pager It is used for page navigation function.
     *  @param userName It is used for search data by name. If you are going to search name of user ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadUserList(Pager<User> pager,String userName,Model model,HttpSession session){
        if(!baseRequest(session,model)){
            return "redirect:/error/404.html";
        }
        pager.setVendor_flag(0);
        pager.setSearch_str(userName);
        List<User> userList = userService.load_userList(pager,null);

        model.addAttribute("userList",userList);
        return "admin/user/userTable";
    }


}
