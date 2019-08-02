package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.FeeService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Fee;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Vendors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/fee")
public class AdminFeeController extends AdminAbstractController {

    @Autowired
    private FeeService feeService;

    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Fee> pager, HttpSession session){
        feeService.intiPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "admin/fee/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Fee> pager, String dateFrom, String dateTo, HttpSession session , Model model) {
        List<Fee> feeList = feeService.loadFee(pager);
        model.addAttribute("feeList", feeList);
        return "admin/fee/table";
    }

    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        Fee fee = feeService.getFeeById(id);
        model.addAttribute("data",fee);
        return "admin/fee/edit";
    }
}
