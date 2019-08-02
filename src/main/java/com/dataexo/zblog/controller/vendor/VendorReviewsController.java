package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.controller.admin.AdminAbstractController;
import com.dataexo.zblog.service.ReviewService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Customer_reviews;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;
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
 * This is controller class implements an management of reviews table in vendor panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used reviewsService class to get data from database.
 * reviewsService is for reviews table.
 *
 * <p> This class is integrate with templates/vendor/reviews folder
 *  for example  , when the return result is "vendor/reviews/table" , it means vendor/reviews/table.html file
 *  so the browser loads the vendor/reviews/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put reviews attribute to model variable ,
 * then you can use reviews attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${reviews}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */


@Controller
@RequestMapping("/vendor/reviews")
public class VendorReviewsController extends VendorAbstractController {

    @Resource
    private ReviewService reviewService;

    @Resource
    private UserService userService;

    /**
     * This is function implements to get total count of table data and total pages.
     * So it can be used to implement page navigation function
     * If you call this function in frontend using ajax ,
     * this function returns pager information such total pages and total data count in json format
     * @param pager It is used for page navigation function
     * @return pager This is return param with total pages and total data count.
     *                  So it will determine page navigation bar.
     */
    @RequestMapping("/initPage")
    @ResponseBody
    public Pager initPage(Pager<Customer_reviews> pager, HttpSession session){

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));

        reviewService.initPage(pager);
        return pager;
    }

    /**
     * This is function implements to edit item page.
     * This function will be used when you navigate edit page.
     * For example , if you locate page to http://localhost/vendor/reviews/edit/1 , then this function will be called.
     * Then it will get item based on id param and put attribute in model variable.
     * So it can be used in frontend edit page.
     * This function is mapped vendor/reviews/edit.html file.
     * @param id This is edited item id. It come from url. As you can see,
     *  if you navigate to "/edit/1" in browser, the id should be 1.
     * @param model  It is for maaping the data to frontend using thymeleaf
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        Customer_reviews reviews = reviewService.getReviewsById(id);

        User user = userService.loadUserById((long) reviews.getCustomer_id());
        reviews.setCustomer_name(user.getUsername());

        model.addAttribute("data",reviews);
        return "vendor/reviews/edit";
    }

    /**
     * This is function implements to add item page.
     * This function will be used when you navigate add page.
     * For example , if you locate page to http://localhost/vendor/reviews/add , then this function will be called.
     * This function is mapped vendor/reviews/add.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/add")
    public String addPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/reviews/add";
    }

    /**
     * This is function implements to list items page.
     * This function will be used when you navigate list page.
     * For example , if you locate page to http://localhost/vendor/reviews/list , then this function will be called.
     * This function is mapped vendor/reviews/list.html file.
     * @return String This param is url for frontend page file.
     */
    @RequestMapping("/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/reviews/list";
    }

    /**
     * This is function implements to table data page.
     * This function will be used when you navigate list page.
     * When you are going to load data by pager , then this function is called to get table.
     * For example , if you locate page to http://localhost/vendor/reviews/list , then this function will be called to get table data by pager.
     * This function is mapped vendor/reviews/table.html file.
     * @param pager It is used for page navigation function.
     *  @param name It is used for search data by name. If you are going to search name of reviews ,
     *  then you should put name param to search.
     * @return  This param is url for frontend page file.
     */
    @RequestMapping("/load")
    public String loadList(Pager<Customer_reviews> pager,String name, HttpSession session , Model model){
        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt(vendors.getId() + ""));

        pager.setSearch_str(name);
        List<Customer_reviews> reviewsList = reviewService.loadReviews(pager);
        for(int i = 0 ; i < reviewsList.size() ; i ++){
            Customer_reviews reviews = reviewsList.get(i);
            User user = userService.loadUserById((long) reviews.getCustomer_id());
            reviews.setCustomer_name(user.getUsername());

            int len = reviews.getDataset_name().length();
            int len1 = len;
            if(len > 30){
                len1 = 30;
            }
            String tmp = reviews.getDataset_name().substring(0, len1);

            if(len > 30){
                tmp += "...";
            }
            reviews.setDataset_name(tmp);

            reviewsList.set(i, reviews);
        }
        model.addAttribute("reviewsList",reviewsList);
        return "vendor/reviews/table";
    }

}
