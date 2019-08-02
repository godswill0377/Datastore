package com.dataexo.zblog.controller.admin;

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


/**
 * This is controller class implements an management of plan table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used planService class to get data from database.
 * planService is for plan table.
 *
 * <p> This class is integrate with templates/admin/plan folder
 *  for example  , when the return result is "admin/plan/table" , it means admin/plan/table.html file
 *  so the browser loads the admin/plan/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put plan attribute to model variable ,
 * then you can use plan attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${plan}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/plan")
public class AdminPlanController extends AdminAbstractController {

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_table.
     * It contains findAll,saveplan,checkExist,loadplan,getplanById ...
     */
    @Resource
    private PlanService planService;

    /**
     * This is function implements to get total count of table data and total pages.
     * So it can be used to implement page navigation function
     * If you call this function in frontend using ajax ,
     * this function returns pager information such total pages and total data count in json format
     * @param pager It is used for page navigation function.
     * @param model It is for maaping the data to frontend using thymeleaf
     * @return pager This is return param with total pages and total data count.
     *                  So it will determine page navigation bar.
     */
    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Plan> pager, Model model){
        pager.setVendor_id(-1);
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
    public String editPage(@PathVariable Integer id, HttpSession session ,  Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        Plan data = planService.getPlanById(id);

        model.addAttribute("data",data);
        return "admin/plan/edit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/admin/plan/add , then this function will be called.
     * This function is mapped admin/plan/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/plan/add";
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
            return "redirect:/login";
        }
        return "admin/plan/list";
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
    public String loadList(Pager<Plan> pager,String name,Model model){
        pager.setVendor_id(-1);
        pager.setSearch_str(name);
        List<Plan> dataList = planService.loadPlan(pager, null);
        model.addAttribute("dataList",dataList);
        return "admin/plan/table";
    }

}
