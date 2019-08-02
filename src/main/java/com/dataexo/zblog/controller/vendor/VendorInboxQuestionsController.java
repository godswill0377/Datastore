package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.InboxQuestionService;
import com.dataexo.zblog.service.QuestionService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.service.VendorService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Question_anwsers;
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
 * This is controller class implements an management of data_update_frequency in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used Data_frequencyService class to get data from database.
 * Data_frequencyService is for data_update_frequency.
 *
 * <p> This class is integrate with templates/admin/data_frequency folder
 *  for example  , when the return result is "admin/data_frequency/table" , it means admin/data_frequency/table.html file
 *  so the browser loads the admin/data_frequency/table.html file for frontend.
 *
 * <p> You can see the Model class in every functions.
 * This param is for pass over the database data to frontend based on thymeleaf
 * For example , if you put data_frequency attribute to model variable ,
 * then you can use data_frequency attribute in frontend using thymeleaf
 * you can use like this in frontend: th:value="${data_frequency}"
 *
 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */


@Controller
@RequestMapping("/vendor/inbox")
public class VendorInboxQuestionsController extends VendorAbstractController {

    @Resource
    private InboxQuestionService questionService;

    @Resource
    private VendorService vendorService;

    @RequestMapping("/question/initPage")
    @ResponseBody
    public Pager initPage(Pager<Question_anwsers> pager, HttpSession session){

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt("" + vendors.getId()));

        questionService.initPage(pager);
        return pager;
    }

    @RequestMapping("/question/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        return "vendor/inbox/question_list";
    }


    @RequestMapping("/question/edit/{id}")
    public String editQuestionPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        if(id != null){
            Question_anwsers data = questionService.getRowById(id);
            Vendors vendors = vendorService.getVendorIDById(data.getQuestion_by_userid());
            data.setQuestion_by_name(vendors.getLegal_name());

            model.addAttribute("data",data);
        }

        return "vendor/inbox/question_edit";
    }


    @RequestMapping("/question/load")
    public String loadList(Pager<Question_anwsers> pager,String name,HttpSession session, Model model){

        pager.setSearch_str(name);

        Vendors vendors = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        pager.setVendor_id(Integer.parseInt("" + vendors.getId()));

        List<Question_anwsers> dataList = questionService.loadQuestionList(pager);

        for(int i = 0 ; i < dataList.size() ; i ++){
            Question_anwsers question = dataList.get(i);

            int size = 0;
            List<Question_anwsers> answerList = questionService.loadAnswerList(question.getId());
            for(int j = 0 ; j < answerList.size() ; j ++){
                if(answerList.get(j).getRead_flag() == 0){
                    size ++;
                }
            }

            if(answerList.size() == 0){
                question.setRead_flag(0);
            }
            else{

                if(size > 0) {
                    question.setRead_flag(0);
                }
                else {
                    question.setRead_flag(1);
                }
            }
            //question.setAnswers_num(size);

            vendors = vendorService.getVendorIDById((long) question.getQuestion_by_userid());
            if(vendors != null){
                question.setQuestion_by_name(vendors.getLegal_name());
            }
            else{
                question.setQuestion_by_name("The vendor deleted");
            }

            dataList.set(i, question);
        }

        // model.addAttribute("read_nums",read_nums);

        model.addAttribute("dataList",dataList);
        return "vendor/inbox/question_table";
    }

    @RequestMapping("/question/add")
    public String addQuestionPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        return "vendor/inbox/question_add";
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping("/question/answer/initPage")
    @ResponseBody
    public Pager initAnswerPage(Pager<Question_anwsers> pager, Model model,  HttpSession session){


        questionService.initAnswerPage(pager);
        return pager;
    }

    @RequestMapping("/question/answer/list/{questionId}")
    public String listAnswerPage(@PathVariable Integer questionId, HttpSession session,Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }
        questionService.eraseAnswerReadFlag(questionId);
        model.addAttribute("questionId",questionId);
        return "vendor/inbox/answer_list";
    }


    @RequestMapping("/question/answer/load")
    public String loadAnswerList(Pager<Question_anwsers> pager, String name,Integer questionId, Model model, HttpSession session){

        pager.setSearch_str(name);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if(vendor != null ){
            pager.setVendor_id((int)vendor.getId());
        }
        pager.setQuestionId(questionId);

        List<Question_anwsers> dataList = questionService.loadAnswerListPage(pager);

        model.addAttribute("dataList",dataList);
        return "vendor/inbox/answer_table";
    }

    @RequestMapping("/question/answer/edit/{id}")
    public String editAnswerPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        if(id != null){
            Question_anwsers data = questionService.getRowById(id);
            model.addAttribute("data",data);
        }

        return "vendor/inbox/answer_edit";
    }

    @RequestMapping("/question/answer/add/{questionId}")
    public String addAnswerPage(@PathVariable Integer questionId, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/vendor/login";
        }

        model.addAttribute("questionId",questionId);
        return "vendor/inbox/answer_add";
    }

}
