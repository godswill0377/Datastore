package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.service.VendorMarketingService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Email_lists;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Vendors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@RequestMapping("/vendor/marketing")
public class VendorMarketingController extends VendorAbstractController {

    @Resource
    private VendorMarketingService vendorMarketingService;

    @Resource
    private Data_setsService data_setsService;

    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Email_lists> pager, HttpSession session){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        vendorMarketingService.initPage(pager);
        return pager;
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/email_marketing/list";
    }

    @RequestMapping("/edit/{id}")
    public String editQuestionPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        if(id != null){
            Email_lists data = vendorMarketingService.getEmail_listsById(id);
            String[] ar = data.getDataset_ids().split(",");
            String names = "";
            for(int i = 0 ; i < ar.length ; i ++){
                Data_sets data_sets = data_setsService.getData_setsById(Integer.parseInt(ar[i]));
                names += data_sets.getName() + ",";
            }
            data.setDataset_names(names);

            model.addAttribute("data",data);
        }

        return "vendor/email_marketing/edit";
    }


    @RequestMapping("/load")
    public String loadList(Pager<Email_lists> pager,String name, HttpSession session, Model model){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setSearch_str(name);
        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));

        List<Email_lists> dataList = vendorMarketingService.loadEmail_lists(pager );


        model.addAttribute("dataList",dataList);
        return "vendor/email_marketing/table";
    }

    @RequestMapping("/add")
    public String addQuestionPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        Pager pager = new Pager();
        pager.setSearch_str("");
        pager.setVendor_id(Integer.parseInt("" + vendors.getId()));

        List<Data_sets> dataList = data_setsService.loadData_getsByvendor_id(pager);
        model.addAttribute("dataList" , dataList);
        return "vendor/email_marketing/add";
    }


}
