package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.PaymentService;
import com.dataexo.zblog.service.Trans_logService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;
import com.dataexo.zblog.vo.Vendors;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/vendor/payment")
public class VendorPaymentController extends VendorAbstractController {

    @Autowired
    private PaymentService paymentService;


    @RequestMapping("initPage")
    @ResponseBody
    public Pager initPage(Pager<Trans_log> pager, HttpSession session){
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        pager.setTrans_type(1);
        pager.setStatus(1);
        paymentService.intiPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(Pager<Trans_log> pager, HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/payment/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Trans_log> pager, String dateFrom, String dateTo,String dayMode, HttpSession session , Model model) {
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00");
        DateFormat dateToFormat = new SimpleDateFormat("yyyy-MM-dd 24:00");
        Date date = new Date();
        switch (dayMode){
            case "1":   //today case
                dateFrom = dateFormat.format(date);
                dateTo = dateToFormat.format(date);
                break;
            case "2":   //yesterday
                dateFrom = dateFormat.format(DateUtils.addDays(date, -1));
                dateTo = dateToFormat.format(DateUtils.addDays(date, -1));
                break;
            case "3":   // this week
                dateFrom = dateFormat.format(DateUtils.addDays(date, -7));
                dateTo = dateToFormat.format(date);
                break;
            case "4":   // this month
                dateFrom = dateFormat.format(DateUtils.addMonths(date, -1));
                dateTo = dateToFormat.format(date);
                break;
            case "5":   // this year
                dateFrom = dateFormat.format(DateUtils.addYears(date, -1));
                dateTo = dateToFormat.format(date);
                break;
        }
        pager.setDateFrom(dateFrom);
        pager.setDateTo(dateTo);
        pager.setTrans_type(1);
        pager.setStatus(1);
        List<Trans_log> transactionList = paymentService.loadPayment(pager);
        for(Trans_log t : transactionList){
            if(t.getDescription().length()>45){
                t.setDescription(t.getDescription().substring(0, 45));
            }
        }
        model.addAttribute("amount", paymentService.getAmountByDateRange(pager));
        model.addAttribute("transactionList", transactionList);
        return "/vendor/payment/table";
    }

    @RequestMapping("/edit/{id}")
    public String editPage(Pager<Trans_log> pager, @PathVariable("id") Integer id, HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/vendor/login";
        }
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        model.addAttribute("data", paymentService.getPaymentById(pager, id));
        return "/vendor/payment/edit";
    }

}
