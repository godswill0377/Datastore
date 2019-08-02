package com.dataexo.zblog.controller.vendor;


import com.dataexo.zblog.controller.AbstractController;
import com.dataexo.zblog.service.Pay_SourceService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_sources;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.vo.Vendors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/vendor")
public class VendorCardManageController extends VendorAbstractController {

    private static final Logger logger = Logger.getLogger(VendorCardManageController.class);

    @Autowired
    private Pay_SourceService pay_sourceService;

    @RequestMapping("/card/list")
    public String vendorCardList(HttpSession session, Model model) {

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);

        Pager<Pay_sources> pager = new Pager<Pay_sources>();
        pager.setSearch_str("");
        pager.setUser_id(Integer.parseInt("" + user.getId()));

        List<Pay_sources> dataList =pay_sourceService.loadPay_Source(pager);

        model.addAttribute("dataList", dataList);

        return "vendor/mycard/list";

    }

    @RequestMapping("/card/add")
    public String vendorAddCard(HttpSession session, Model model) {

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/mycard/add";
    }

}