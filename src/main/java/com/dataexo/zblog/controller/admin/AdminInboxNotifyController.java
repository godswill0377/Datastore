package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.InboxNotifyService;
import com.dataexo.zblog.service.InboxQuestionService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.service.VendorService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Inbox_Notify;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.Vendors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * This is controller class implements an management of inbox_question in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used inbox_questionService class to get data from database.
 * inbox_questionService is for inbox_question.
 *
 * <p> This class is integrate with templates/admin/inbox_question folder
 *  for example  , when the return result is "admin/inbox_notification/table" , it means admin/inbox_notification/table.html file
 *  so the browser loads the admin/inbox_notification/table.html file for frontend.

 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/inbox")
public class AdminInboxNotifyController extends AdminAbstractController {

    @Resource
    private InboxNotifyService inboxNotifyService;

    @Resource
    private VendorService vendorService;

    @Resource
    private UserService userService;

    @RequestMapping("/notification/initPage")
    @ResponseBody
    public Pager initPage(Pager<Inbox_Notify> pager, HttpSession session){
        inboxNotifyService.initPage(pager);
        return pager;
    }

    @RequestMapping("/notification/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/inbox_notification/list";
    }

    @RequestMapping("/notification/edit/{id}")
    public String editQuestionPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        if(id != null){
            Inbox_Notify data = inboxNotifyService.getInbox_NotifyById(id);

            model.addAttribute("data",data);
        }

        return "admin/inbox_notification/edit";
    }


    @RequestMapping("/notification/load")
    public String loadList(Pager<Inbox_Notify> pager,String name, Model model){

        pager.setSearch_str(name);
        List<Inbox_Notify> dataList = inboxNotifyService.loadInbox_Notify(pager );


        model.addAttribute("dataList",dataList);
        return "admin/inbox_notification/table";
    }

    @RequestMapping("/notification/add")
    public String addQuestionPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        List<User> userList = userService.getAllUsers(1);

        model.addAttribute("userList",userList);
        return "admin/inbox_notification/add";
    }

}
