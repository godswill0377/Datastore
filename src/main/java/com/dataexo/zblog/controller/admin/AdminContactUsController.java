package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.Contact_usService;
import com.dataexo.zblog.service.Contact_usService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Contact_us;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * This is controller class implements an management of Contact_us table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used Contact_usService class to get data from database.
 * Contact_usService is for Contact_us table.
 *
 * <p> This class is integrate with templates/admin/contact_us folder
 *  for example  , when the return result is "admin/contact_us/table" , it means admin/contact_us/table.html file
 *  so the browser loads the admin/contact_us/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put contact_us attribute to model variable ,
 * then you can use contact_us attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${contact_us}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/contact_us")
public class AdminContactUsController extends AdminAbstractController {

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_table.
     * It contains findAll,savecontact_us,checkExist,loadcontact_us,getcontact_usById ...
     */
    @Resource
    private Contact_usService contact_usService;

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
    public Pager initPage(Pager<Contact_us> pager, Model model){
        contact_usService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/contact_us/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/contact_us/edit.html file.
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

        Contact_us data = contact_usService.getContact_usById(id);

        model.addAttribute("data",data);
        return "admin/contact_us/edit";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/contact_us/list , then this function will be called.
     * This function is mapped admin/contact_us/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/contact_us/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/contact_us/list , then this function will be called to get table data by pager.
     * This function is mapped admin/contact_us/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of contact_us ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Contact_us> pager,String name,Model model){

        pager.setSearch_str(name);
        List<Contact_us> dataList = contact_usService.loadContact_us(pager,null);

        model.addAttribute("dataList",dataList);
        return "admin/contact_us/table";
    }

}
