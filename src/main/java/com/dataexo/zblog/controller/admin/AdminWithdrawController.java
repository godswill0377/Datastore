package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.PaymentService;
import com.dataexo.zblog.service.WithdrawService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Withdraw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/withdraw")
public class AdminWithdrawController extends AdminAbstractController{

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private PaymentService paymentService;

    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager pager, HttpSession session){

        pager.setStatus(2);

        withdrawService.initPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/withdraw/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager pager,String name, HttpSession session, Model model){

        pager.setStatus(2);

        List<Withdraw> dataList = withdrawService.loadWithdraw(pager);

        model.addAttribute("dataList",dataList);
        return "admin/withdraw/table";
    }

    @RequestMapping("/edit/{id}")
    public String add (HttpSession session, Model model,Pager pager, @PathVariable int id){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        model.addAttribute("data", withdrawService.getById(id));
        return "admin/withdraw/edit";
    }

}
