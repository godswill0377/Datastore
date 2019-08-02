package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.PurchaseService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Purchase;
import com.dataexo.zblog.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * This is controller class implements an management of shopping table in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used shoppingService class to get data from database.
 * shoppingService is for shopping table.
 *
 * <p> This class is integrate with templates/admin/shopping folder
 *  for example  , when the return result is "admin/shopping/table" , it means admin/shopping/table.html file
 *  so the browser loads the admin/shopping/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put shopping attribute to model variable ,
 * then you can use shopping attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${shopping}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */


@Controller
@RequestMapping("/admin/shopping")
public class AdminShoppingController extends AdminAbstractController {

    @Resource
    private UserService userService;


    /**
     * This is dao variable to connect database.
     * Mainly this variable get data from pay_log.
     * It contains findAll,savePay_log,loadpay_log...
     */

    @Resource
    private PurchaseService purchaseService;

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
        purchaseService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/admin/shopping/list , then this function will be called.
     * This function is mapped admin/shopping/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/shopping/list";
    }


    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/admin/shopping/list , then this function will be called to get table data by pager.
     * This function is mapped admin/shopping/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of shopping ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Purchase> pager, String name, Model model){

        pager.setSearch_str(name);
        pager.setCateid(-1 + "");

        List<Purchase> dataList = purchaseService.loadPurchase(pager);

        for(int i = 0 ; i < dataList.size() ; i ++){
            Purchase purchase = dataList.get(i);
            User user = userService.loadUserById(dataList.get(i).getUserid());
            purchase.setUsername(user.getUsername());

            long order_date = Long.parseLong(purchase.getOrder_date());
            Date date = new Date(order_date);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            purchase.setOrder_date(format.format(date));
            dataList.set(i,purchase);

        }
        model.addAttribute("dataList",dataList);
        return "admin/shopping/table";
    }

}
