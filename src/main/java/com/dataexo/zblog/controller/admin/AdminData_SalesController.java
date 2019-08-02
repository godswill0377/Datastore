package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.Data_salesService;
import com.dataexo.zblog.service.Pay_logService;
import com.dataexo.zblog.vo.Data_sales;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * This is controller class implements an management of data_sales table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used Data_salesService class to get data from database.
 * Data_salesService is for data_sales table.
 *
 * <p> This class is integrate with templates/admin/data_sales folder
 *  for example  , when the return result is "admin/data_sales/table" , it means admin/data_sales/table.html file
 *  so the browser loads the admin/data_sales/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put data_sales attribute to model variable ,
 * then you can use data_sales attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${data_sales}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */


@Controller
@RequestMapping("/admin/data_sales")
public class AdminData_SalesController extends AdminAbstractController {

    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from pay_log.
     * It contains findAll,savePay_log,loadpay_log...
     */
    @Resource
    private Pay_logService pay_logService;

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
    public Pager initPage(Pager<Pay_log> pager, Model model){
        pager.setCateid(-1 + "");
        if(pager.getSearch_str() == null){
            pager.setSearch_str("");
        }
        pay_logService.initPage(pager);
        return pager;
    }


    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/data_sales/list , then this function will be called.
     * This function is mapped admin/data_sales/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        Pager pager = new Pager();
        pager.setSearch_str("");

        float total  = pay_logService.getTotalPrice(pager);

        model.addAttribute("total",total);
        return "admin/data_sales/list";
    }


    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/data_sales/list , then this function will be called to get table data by pager.
     * This function is mapped admin/data_sales/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of data_sales ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Pay_log> pager,String name,Model model){

        pager.setSearch_str(name);
        pager.setCateid(-1 + "");
        List<Pay_log> dataList = pay_logService.loadPay_log(pager);
        float total  = pay_logService.getTotalPrice(pager);

        model.addAttribute("total",total);
        model.addAttribute("dataList",dataList);
        return "admin/data_sales/table";
    }

}
