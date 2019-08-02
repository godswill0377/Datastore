package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.*;
import com.dataexo.zblog.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;


/**
 * This is controller class implements an management of data_sets table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used Data_setsService class to get data from database.
 * Data_setsService is for data_sets table.
 *
 * <p> This class is integrate with templates/admin/data_set folder
 * for example  , when the return result is "admin/data_set/table" , it means admin/data_set/table.html file
 * so the browser loads the admin/data_set/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put data_set attribute to model variable ,
 * then you can use data_set attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${data_set}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */

/**
 * It loads the resource file to get beans for sending mail and upload function
 * It contains multipartResolver and  mailSender beans
 *
 */
@Controller
@RequestMapping("/admin/data_set")
public class AdminData_SetController extends AdminAbstractController {

    /**
     * This is domain address which comes from application.yml
     * You can get address.domain attribute from application.yml
     *  For example , @Value("${server.port}") means to get 8080 value.
     */
    @Value("${address.domain}")
    private String domain;

    /**
     * This is Data_setsService variable to get data from data_sets table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private Data_setsService data_setsService;

    /**
     * This is Price_modelService variable to get data from price_model table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private Price_modelService price_modelService;  //price_model

    /**
     * This is Asset_classService variable to get data from asset_class table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private Asset_classService asset_classService;  //asset_class

    /**
     * This is Data_typeService variable to get data from data_type table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private Data_typeService data_typeService;  //data_type

    /**
     * This is RegionService variable to get data from region table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private RegionService regionService;  //region

    /**
     * This is PublisherService variable to get data from publisher table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private PublisherService publisherService;  //publisher

    /**
     * This is Data_categoryService variable to get data from data_category table.
     * It contains getbyid , insertdata , deletedata , getalldata function.
     */
    @Resource
    private Data_categoryService data_categoryService;


    @Resource
    private VendorService vendorService;  //publisher


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
    public Pager initPage(Pager<Data_sets> pager, Model model) {
        if (pager.getSearch_str() == null) {
            pager.setSearch_str("");
        }
        data_setsService.initPage(pager);

        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/admin/data_set/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped admin/data_set/edit.html file.
     * @param id This is edited item id. It come from url. As you can see,
     *  if you navigate to "/edit/1" in browser, the id should be 1.
     * @param model  It is for maaping the data to frontend using thymeleaf
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/login";
        }

        Data_sets data = data_setsService.getData_setsById(id);

        if (data.getVendor_id() == 0) {
            data.setVendor_name("Administrator");
        } else {
            Vendors vendors = vendorService.getVendorIDById(data.getVendor_id());
            data.setVendor_name(vendors.getLegal_name());
        }
        List<Price_model> price_modelsList = price_modelService.findAll();
        List<Asset_class> assetClassLists = asset_classService.findAll();
        List<Asset_class> assetClassList = new ArrayList<>();
        for (int i = 1; i < assetClassLists.size(); i++) {
            assetClassList.add(assetClassLists.get(i));
        }
        List<Data_type> dataTypeList = data_typeService.findAll();
        List<Region> regionList = regionService.findAll();
        List<Publisher> publisherList = publisherService.findAll();
        List<Data_category> data_categoryList = data_categoryService.findAll();

        model.addAttribute("data_categoryList", data_categoryList);
        model.addAttribute("price_modelsList", price_modelsList);
        model.addAttribute("asset_classList", assetClassList);
        model.addAttribute("data_typeList", dataTypeList);
        model.addAttribute("regionList", regionList);
        model.addAttribute("publisherList", publisherList);

        model.addAttribute("domain", domain);

        model.addAttribute("data", data);
        return "admin/data_set/edit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/admin/data_set/add , then this function will be called.
     * This function is mapped admin/data_set/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/login";
        }
        List<Price_model> price_modelsList = price_modelService.findAll();
        List<Asset_class> assetClassLists = asset_classService.findAll();
        List<Asset_class> assetClassList = new ArrayList<>();
        for (int i = 1; i < assetClassLists.size(); i++) {
            assetClassList.add(assetClassLists.get(i));
        }
        List<Data_type> dataTypeList = data_typeService.findAll();
        List<Region> regionList = regionService.findAll();
        List<Publisher> publisherList = publisherService.findAll();
        List<Data_category> data_categoryList = data_categoryService.findAll();

        model.addAttribute("data_categoryList", data_categoryList);
        model.addAttribute("price_modelsList", price_modelsList);
        model.addAttribute("asset_classList", assetClassList);
        model.addAttribute("data_typeList", dataTypeList);
        model.addAttribute("regionList", regionList);
        model.addAttribute("publisherList", publisherList);

        model.addAttribute("domain", domain);

        return "admin/data_set/add";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/data_set/list , then this function will be called.
     * This function is mapped admin/data_set/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/login";
        }
        return "admin/data_set/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/data_set/list , then this function will be called to get table data by pager.
     * This function is mapped admin/data_series/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of data_series ,
     *  then you should put name param to search.
     * @return This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Publisher> pager, String name, Model model) {

        pager.setSearch_str(name);
        List<Data_sets> dataList = data_setsService.loadData_sets(pager, null);

        model.addAttribute("dataList", dataList);
        return "admin/data_set/table";
    }

}
