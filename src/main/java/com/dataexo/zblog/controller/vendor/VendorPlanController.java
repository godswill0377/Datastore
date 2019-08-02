package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.PlanService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Plan;
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
@RequestMapping("/vendor/plan")
public class VendorPlanController extends VendorAbstractController {
    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_table.
     * It contains findAll,saveplan,checkExist,loadplan,getplanById ...
     */
    @Resource
    private PlanService planService;


    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Plan> pager, HttpSession session){
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        planService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/plan/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/plan/edit.html file.
     * @param id This is edited item id. It come from url. As you can see,
     *  if you navigate to "/edit/1" in browser, the id should be 1.
     * @param model  It is for maaping the data to frontend using thymeleaf
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session , Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        Plan data = planService.getPlanById(id);

        model.addAttribute("data",data);
        return "vendor/plan/edit";
    }



    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/plan/list , then this function will be called.
     * This function is mapped admin/plan/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/plan/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/plan/list , then this function will be called to get table data by pager.
     * This function is mapped admin/plan/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of plan ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Plan> pager,String name,Model model, HttpSession session){

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));
        pager.setSearch_str(name);
        List<Plan> dataList = planService.loadPlan(pager,null);

        model.addAttribute("dataList",dataList);
        return "admin/plan/table";
    }


}
