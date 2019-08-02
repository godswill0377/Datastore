package com.dataexo.zblog.controller.vendor;


import com.dataexo.zblog.service.PaymentService;
import com.dataexo.zblog.service.VendorService;
import com.dataexo.zblog.service.WithdrawService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Vendors;
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
@RequestMapping("/vendor/withdraw")
public class VendorWithdrawController extends VendorAbstractController{

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private VendorService vendorService;

    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Withdraw> pager, HttpSession session){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        withdrawService.initPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/withdraw/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Withdraw> pager,String name, HttpSession session, Model model){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        pager.setTrans_type(1);

        pager.setStatus(1);

        Double total = paymentService.getTotalAmount((int)vendor.getId());

        Double earning = paymentService.getAmountByDateRange(pager);

        model.addAttribute("total", total);

        pager.setStatus(0);

        List<Withdraw> dataList = withdrawService.loadWithdraw(pager);

        model.addAttribute("withdraw_request", withdrawService.withdrawTotalByStatus(pager, 1));

        model.addAttribute("withdraw", withdrawService.withdrawTotalByStatus(pager, 3));

        model.addAttribute("earning", earning);

        model.addAttribute("dataList",dataList);
        return "vendor/withdraw/table";
    }

    @RequestMapping("/add")
    public String add (HttpSession session, Model model, Pager pager){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        vendor = vendorService.getVendorIDById(vendor.getId());

        if(vendor.getStripe_acc_id() == null || vendor.getStripe_acc_id().equals(""))
        {
            model.addAttribute("message", "You have to do identify verification before first withdraw. After verification , you can withdraw your money.");
            return "vendor/withdraw/stripe_acc_error";
        }

        if(vendor.getStripe_verify() == 0)
        {
            model.addAttribute("message", "You have to do verification.");
            return "vendor/withdraw/stripe_acc_error";
        }
        if(vendor.getStripe_verify() == -1)
        {
            model.addAttribute("message", "Your verification is still pending. Please wait...");
            return "vendor/withdraw/stripe_acc_error";
        }

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        Double total= paymentService.getTotalAmount((int)vendor.getId());
        if(total == null){
            total = 0.0;
        }
        if(withdrawService.withdrawTotalByStatus(pager, 1) != null){
            model.addAttribute("withdraw_request", "1");
        }

        model.addAttribute("total", total);

        return "vendor/withdraw/add";
    }

    @RequestMapping("/edit/{id}")
    public String add (HttpSession session, Model model,Pager pager, @PathVariable int id){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        model.addAttribute("total", paymentService.getTotalAmount((int)vendor.getId()));

        model.addAttribute("data", withdrawService.getById(id));
        return "vendor/withdraw/edit";
    }

}
