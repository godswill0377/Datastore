package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.controller.AbstractController;
import com.dataexo.zblog.service.*;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/vendor/dataset_management")
public class VendorDatasetController extends VendorAbstractController {
    /**
     * This is domain address which comes from application.yml
     * You can get address.domain attribute from application.yml
     * For example , @Value("${server.port}") means to get 8080 value.
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


    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Data_sets> pager
            , HttpSession session
            , Model model) {


        if (pager.getSearch_str() == null) {
            pager.setSearch_str("");
        }

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);

        pager.setVendor_flag((int) vendor.getId());
        data_setsService.initPageByVendorId(pager);
        return pager;
    }

    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/vendor/login";
        }

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if (user != null && vendor != null) {
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
            return "vendor/data_set/add";
        } else {
            return "redirect:/vendor/login";
        }
    }


    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model) {

        if (!baseRequest(session, model)) {
            return "redirect:/vendor/login";
        }

        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if (user != null && vendor != null) {
            Data_sets data = data_setsService.getData_setsById(id);

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
            return "vendor/data_set/edit";
        } else {
            return "redirect:/vendor/login";
        }
    }

    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/vendor/login";
        }

        return "vendor/data_set/list";

    }

    @RequestMapping("/table")
    public String tablePage(HttpSession session, Model model) {
        System.out.println("table fired  inside vendor dataset controller1");
        return "vendor/data_set/table";
    }


    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/data_set/list , then this function will be called to get table data by pager.
     * This function is mapped admin/data_series/table.html file.
     *
     * @param pager It is used for page navigation function.
     * @param name  It is used for search data by name. If you are going to search name of data_series ,
     *              then you should put name param to search.
     * @return This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(HttpSession session, Pager<Publisher> pager, String name, Model model) {

        String template = "vendor/data_set/table";
        User user = (User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setSearch_str(name);

        if (pager.getOrder_by() != null) {
            if (pager.getOrder_by().equals("")) {
                pager.setOrder_by(null);
            } else {
                template = "vendor/data_set/analyze_table";
            }
        }

        pager.setVendor_id(Integer.parseInt(vendor.getId() + ""));

        List<Data_sets> dataList = data_setsService.loadData_getsByvendor_id(pager);
        model.addAttribute("dataList", dataList);
        return template;
    }

    @RequestMapping("/analyze")
    public String listAnalyze(HttpSession session, Model model) {
        if (!baseRequest(session, model)) {
            return "redirect:/vendor/login";
        }

        return "vendor/data_set/analyze";

    }
}
