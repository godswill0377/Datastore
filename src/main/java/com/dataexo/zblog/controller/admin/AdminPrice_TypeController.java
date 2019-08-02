package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.Price_modelService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Price_model;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * This is controller class implements an management of price_type table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used price_typeService class to get data from database.
 * price_typeService is for price_type table.
 *
 * <p> This class is integrate with templates/admin/data_price_type folder
 *  for example  , when the return result is "admin/data_price_type/table" , it means admin/data_price_type/table.html file
 *  so the browser loads the admin/data_price_type/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put price_type attribute to model variable ,
 * then you can use price_type attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${price_type}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/price_type")
public class AdminPrice_TypeController extends AdminAbstractController {

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from asset_table.
     * It contains findAll,saveprice_type,checkExist,loadprice_type,getprice_typeById ...
     */
    @Resource
    private Price_modelService price_modelService;


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

    public Pager initPage(Pager<Price_model> pager, Model model){
        price_modelService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/data_price_type/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/data_price_type/edit.html file.
     * @param id This is edited item id. It come from url. As you can see,
     *  if you navigate to "/edit/1" in browser, the id should be 1.
     * @param model  It is for maaping the data to frontend using thymeleaf
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        Price_model data = price_modelService.getPrice_modelById(id);

        model.addAttribute("data",data);
        return "admin/data_price_type/edit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/admin/price_type/add , then this function will be called.
     * This function is mapped admin/data_price_type/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/data_price_type/add";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/price_type/list , then this function will be called.
     * This function is mapped admin/data_price_type/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/data_price_type/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/price_type/list , then this function will be called to get table data by pager.
     * This function is mapped admin/data_price_type/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of price_type ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Price_model> pager,String name,Model model){

        pager.setSearch_str(name);
        List<Price_model> dataList = price_modelService.loadPrice_model(pager,null);

        model.addAttribute("dataList",dataList);
        return "admin/data_frequency/table";
    }

}
