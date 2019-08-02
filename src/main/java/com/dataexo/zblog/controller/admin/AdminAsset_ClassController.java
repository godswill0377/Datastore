package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.Asset_classService;
import com.dataexo.zblog.vo.Asset_class;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This is controller class implements an management of asset_class table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used Asset_classService class to get data from database.
 * Aseet_classService is for asset_class table.
 *
 * <p> This class is integrate with templates/admin/asset_class folder
 *  for example  , when the return result is "admin/asset_class/table" , it means admin/asset_class/table.html file
 *  so the browser loads the admin/asset_class/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put asset_class attribute to model variable ,
 * then you can use asset_class attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${asset_class}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/asset_class")
public class AdminAsset_ClassController extends AdminAbstractController {


    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_table.
     * It contains findAll,saveAsset_class,checkExist,loadAsset_class,getAsset_classById ...
     */
    @Resource
    private Asset_classService asset_classService;

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
    public Pager initPage(Pager<Asset_class> pager,Model model){
        asset_classService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/asset_class/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/asset_class/edit.html file.
     * @param id This is edited item id. It come from url. As you can see,
     *  if you navigate to "/edit/1" in browser, the id should be 1.
     * @param model  It is for maaping the data to frontend using thymeleaf
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model, HttpSession session){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
       Asset_class asset_class = asset_classService.getAsset_classById(id);

        model.addAttribute("asset_class",asset_class);
        return "admin/asset_class/edit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/admin/asset_class/add , then this function will be called.
     * This function is mapped admin/asset_class/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/asset_class/add";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/asset_class/list , then this function will be called.
     * This function is mapped admin/asset_class/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/asset_class/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/asset_class/list , then this function will be called to get table data by pager.
     * This function is mapped admin/asset_class/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of asset_class ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Asset_class> pager,String name,Model model){

        pager.setSearch_str(name);
        List<Asset_class> asset_classList = asset_classService.loadAsset_class(pager,null);

        model.addAttribute("asset_classList",asset_classList);
        return "admin/asset_class/table";
    }

}
