package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.Trans_logService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.Vendors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/vendor/transaction")
public class VendorTransController extends VendorAbstractController {

    @Autowired
    private Trans_logService trans_logService;


    @RequestMapping("initPage")
    @ResponseBody
    public Pager initPage(Pager<Trans_log> pager, HttpSession session){
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        pager.setStatus(1);
        trans_logService.intiPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/transaction/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Trans_log> pager, String dateFrom, String dateTo, HttpSession session , Model model) {
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        pager.setDateFrom(dateFrom);
        pager.setDateTo(dateTo);
        pager.setStatus(1);
        List<Trans_log> transactionList = trans_logService.loadTrans(pager);
        model.addAttribute("transactionList", transactionList);
        return "/vendor/transaction/table";
    }

}
