package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.controller.vendor.VendorAbstractController;
import com.dataexo.zblog.service.Trans_logService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;
import com.dataexo.zblog.vo.Vendors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin/vendor/transaction")
public class AdminVendorTransController extends AdminAbstractController {

    @Autowired
    private Trans_logService trans_logService;


    @RequestMapping("initPage")
    @ResponseBody
    public Pager initPage(Pager<Trans_log> pager, HttpSession session){

        pager.setVendor_id(-100);
        pager.setStatus(1);
        trans_logService.intiPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/transaction/vendor/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Trans_log> pager, String dateFrom, String dateTo, HttpSession session , Model model) {
        pager.setVendor_id(-100);

        pager.setDateFrom(dateFrom);
        pager.setDateTo(dateTo);
        pager.setStatus(1);
        List<Trans_log> transactionList = trans_logService.loadTrans(pager);
        model.addAttribute("transactionList", transactionList);
        return "admin/transaction/vendor/table";
    }

}
