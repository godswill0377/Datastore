package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * This is controller class implements an management of data_set_series table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used Data_sets_seriesService class to get data from database.
 * Data_sets_seriesService is for data_set_series table.
 *
 * <p> This class is integrate with templates/admin/data_series folder
 *  for example  , when the return result is "admin/data_series/table" , it means admin/data_series/table.html file
 *  so the browser loads the admin/data_series/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put data_series attribute to model variable ,
 * then you can use data_series attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${data_series}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

@Controller
@RequestMapping("/admin/data_series")
public class AdminData_SeriesController extends AdminAbstractController {

    /**
     * This is domain address which comes from application.yml
     * You can get address.domain attribute from application.yml
     *  For example , @Value("${server.port}") means to get 8080 value.
     */
    @Value("${address.domain}")
    private String domain;

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_sets_series.
     * It contains findAll,saveData_sets_series,checkExist,loadData_sets_series,getData_sets_seriesById ...
     */
    @Resource
    private Data_sets_seriesService data_sets_seriesService;

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from data_update_frequency.
     * It contains findAll,saveData_frequency,checkExist,loadData_frequency,getData_frequencyById ...
     */
    @Resource
    private Data_FrequencyService frequencyService;


    /**
     * This is Data_setsService variable to get data from data_sets table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private Data_setsService data_setsService;


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
    public Pager initPage(Pager<Data_sets> pager, Model model){

        if(pager.getSearch_str() == null){
            pager.setSearch_str("");
        }
        data_sets_seriesService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/data_series/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/data_series/edit.html file.
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

        Data_sets_series data = data_sets_seriesService.getData_sets_seriesById(id);
        List<Data_frequency> frequencyList= frequencyService.findAll();

        Data_sets data_sets = data_setsService.getData_setsById(data.getData_set_id());
        model.addAttribute("data_sets",data_sets);

        model.addAttribute("frequencyList",frequencyList);
        model.addAttribute("domain",domain);
        model.addAttribute("data",data);
        return "admin/data_series/edit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/admin/data_series/add , then this function will be called.
     * This function is mapped admin/data_series/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add/{id}")
    public String addPage(@PathVariable Integer id,HttpSession session ,Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        Data_sets data_sets = data_setsService.getData_setsById(id);

        List<Data_frequency> frequencyList= frequencyService.findAll();

        model.addAttribute("data_sets",data_sets);

        model.addAttribute("frequencyList",frequencyList);
        model.addAttribute("domain",domain);
        return "admin/data_series/add";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/data_series/list , then this function will be called.
     * This function is mapped admin/data_series/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/data_series/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/data_series/list , then this function will be called to get table data by pager.
     * This function is mapped admin/data_series/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of data_series ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Publisher> pager,String name,Model model){

        pager.setSearch_str(name);
        List<Data_sets_series> dataList = data_sets_seriesService.loadData_sets_series(pager,null);

        model.addAttribute("dataList",dataList);
        return "admin/data_series/table";
    }

}
