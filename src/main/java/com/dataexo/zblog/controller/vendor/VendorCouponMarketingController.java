package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.Coupon_manageService;
import com.dataexo.zblog.service.VendorMarketingService;
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
@RequestMapping("/vendor/couponmarketing")
public class VendorCouponMarketingController extends VendorAbstractController{

    @Resource
    private VendorMarketingService vendorMarketingService;

    @Resource
    private Coupon_manageService coupon_manageService;

    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Email_lists> pager, HttpSession session){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        vendorMarketingService.initCouponPage(pager);

        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/coupon_marketing/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Email_lists> pager,String name, HttpSession session, Model model){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setSearch_str(name);
        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        List<Email_lists> dataList = vendorMarketingService.loadCouponEmail(pager );

        model.addAttribute("dataList",dataList);
        return "vendor/coupon_marketing/table";
    }

    @RequestMapping("/add")
    public String addCouponMarketingPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        Pager pager = new Pager();
        pager.setSearch_str("");
        pager.setVendor_id(Integer.parseInt("" + vendors.getId()));

        List<Coupon_manage> dataList = coupon_manageService.loadCoupon(pager);
        model.addAttribute("dataList" , dataList);
        return "vendor/coupon_marketing/add";
    }

    @RequestMapping("/edit/{id}")
    public String editCouponMarketingPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        if(id != null){
            Email_lists data = vendorMarketingService.getEmail_listsById(id);
            String[] ar = data.getDataset_ids().split(",");
            String names = "";
            for(int i = 0 ; i < ar.length ; i ++){
                Coupon_manage coupon_manage = coupon_manageService.getById(Integer.parseInt(ar[i]));
                names += coupon_manage.getCoupon() + ",";
            }
            data.setDataset_names(names);

            model.addAttribute("data",data);
        }

        return "vendor/coupon_marketing/edit";
    }

}
