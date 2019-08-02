package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.Coupon_manageService;
import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Coupon_manage;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Vendors;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/vendor/coupon")
public class VendorCouponController extends VendorAbstractController {

    @Autowired
    private Coupon_manageService coupon_manageService;

    @Autowired
    private Data_setsService data_setsService;

    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Coupon_manage> pager, HttpSession session){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        coupon_manageService.initPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/coupon/list";
    }

    @RequestMapping("/load")
    public String loadList(Pager<Coupon_manage> pager,String name, HttpSession session, Model model){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        List<Coupon_manage> dataList = coupon_manageService.loadCoupon(pager);


        model.addAttribute("dataList",dataList);
        return "vendor/coupon/table";
    }

    @RequestMapping("/add")
    public String addCoupon(Pager<Coupon_manage> pager, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);


        pager.setVendor_id(Integer.parseInt("" + vendors.getId()));
        model.addAttribute("coupon", RandomStringUtils.random(10, true, true));
        List<Data_sets> dataList = data_setsService.loadData_getsByvendor_id(pager);
        model.addAttribute("dataList" , dataList);
        return "vendor/coupon/add";
    }

    @RequestMapping("/edit/{id}")
    public String editCoupon(HttpSession session, Model model, @PathVariable int id){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        Coupon_manage coupon_manage = coupon_manageService.getById(id);
        String[] ar = coupon_manage.getDataset_ids().split(",");
        String names = "";
        for(int i = 0 ; i < ar.length ; i ++){
            Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(ar[i]));
            names += data_sets.getName() + ",";
        }
        coupon_manage.setDataset_names(names);
        model.addAttribute("data", coupon_manage);
        return  "vendor/coupon/edit";
    }
}
