package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.InboxNotifyService;
import com.dataexo.zblog.service.InboxQuestionService;
import com.dataexo.zblog.service.VendorService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;



@Controller
@RequestMapping("/vendor/inbox")
public class VendorInboxNotifyController extends VendorAbstractController {
    @Resource
    private InboxNotifyService inboxNotifyService;

    @Resource
    private VendorService vendorService;

    @RequestMapping("/notification/initPage")
    @ResponseBody
    public Pager initPage(Pager<Inbox_Notify> pager, HttpSession session){
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);

        pager.setUser_id(Integer.parseInt("" + user.getId()));
        inboxNotifyService.initPage(pager);
        return pager;
    }

    @RequestMapping("/notification/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        return "vendor/inbox_notification/list";
    }

    @RequestMapping("/notification/edit/{id}")
    public String editQuestionPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        if(id != null){
            Inbox_Notify data = inboxNotifyService.getInbox_NotifyById(id);

            model.addAttribute("data",data);
        }

        return "vendor/inbox_notification/edit";
    }


    @RequestMapping("/notification/load")
    public String loadList(Pager<Inbox_Notify> pager,String name, Model model, HttpSession session){

        pager.setSearch_str(name);

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);

        pager.setUser_id(Integer.parseInt("" + user.getId()));
        List<Inbox_Notify> dataList = inboxNotifyService.loadInbox_Notify(pager );

        model.addAttribute("dataList",dataList);

        for(int i = 0 ; i < dataList.size() ; i ++){
            inboxNotifyService.eraseReadFlag(dataList.get(i).getId());

        }
        return "vendor/inbox_notification/table";
    }

    @RequestMapping("/notification/add")
    public String addQuestionPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        return "vendor/inbox_notification/add";
    }


}
