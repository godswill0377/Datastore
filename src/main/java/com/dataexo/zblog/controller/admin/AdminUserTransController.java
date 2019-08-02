package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.controller.vendor.VendorAbstractController;
import com.dataexo.zblog.service.Pay_logService;
import com.dataexo.zblog.service.Trans_logService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Trans_log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/user/transaction")
public class AdminUserTransController extends AdminAbstractController {

    @Autowired
    private Pay_logService pay_logService;


    @RequestMapping("initPage")
    @ResponseBody
    public Pager initPage(Pager<Trans_log> pager, HttpSession session){

        pager.setSearch_str("");
        pager.setCateid("-1");
        pay_logService.initPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/transaction/user/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Trans_log> pager,  HttpSession session , Model model) {
        pager.setSearch_str("");
        pager.setCateid("-1");
        List<Pay_log> transactionList = pay_logService.loadPay_log(pager);
        model.addAttribute("transactionList", transactionList);
        return "admin/transaction/user/table";
    }

}
