package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.Data_FrequencyService;
import com.dataexo.zblog.service.QuestionService;
import com.dataexo.zblog.service.UserService;
import com.dataexo.zblog.util.ResultInfo;
import com.dataexo.zblog.util.ResultInfoFactory;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("/admin/dataset")
public class AdminQuestionsController extends AdminAbstractController {

    @Resource
    private QuestionService questionService;


    @Resource
    private UserService userService;

    @RequestMapping("/question/initPage")
    @ResponseBody
    public Pager initPage(Pager<Question_anwsers> pager, Model model){
        questionService.initPage(pager);
        return pager;
    }

    @RequestMapping("/question/edit/{id}")
    public String editPage(@PathVariable Integer id, HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        Question_anwsers data = questionService.getRowById(id);

        User user = userService.loadUserById((long) data.getQuestion_by_userid());
        data.setQuestion_by_name(user.getUsername());

        model.addAttribute("data",data);
        return "admin/question_answer/question_edit";
    }


    @RequestMapping("/question/list")
    public String listPage(HttpSession session, Model model){
        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        return "admin/question_answer/question_list";
    }

    @RequestMapping("/question/load")
    public String loadList(Pager<Question_anwsers> pager,String name,Model model){

        pager.setSearch_str(name);
        List<Question_anwsers> dataList = questionService.loadQuestionList(pager);
        for(int i = 0 ; i < dataList.size() ; i ++){
            Question_anwsers answer = dataList.get(i);
            int size = questionService.loadAnswerList(answer.getId()).size();

            answer.setAnswers_num(size);

            User user = userService.loadUserById((long) answer.getQuestion_by_userid());
            answer.setQuestion_by_name(user.getUsername());

            int len = answer.getDataset_name().length();
            int len1 = len;
            if(len > 30){
                len1 = 30;
            }
            String tmp = answer.getDataset_name().substring(0, len1);

            if(len > 30){
                tmp += "...";
            }
            answer.setDataset_name(tmp);
            dataList.set(i, answer);
        }
        model.addAttribute("dataList",dataList);
        return "admin/question_answer/question_table";
    }



    @RequestMapping("/question/answer/initPage")
    @ResponseBody
    public Pager initAnswerPage(Pager<Question_anwsers> pager, Model model,  HttpSession session){

        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if(vendor != null){
            pager.setVendor_id((int)vendor.getId());
        }
        questionService.initAnswerPage(pager);
        return pager;
    }
    @RequestMapping("/question/answer/list/{questionId}")
    public String answerListAnswerPage(@PathVariable Integer questionId, HttpSession session,Model model){
        model.addAttribute("questionId",questionId);
        return "admin/question_answer/answer_list";
    }

    @RequestMapping("/question/answer/load")
    public String answerLoadAnswerList(Pager<Question_anwsers> pager, String name,Integer questionId, Model model, HttpSession session){

        pager.setSearch_str(name);
        Vendors vendor = (Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if(vendor != null ){
            pager.setVendor_id((int)vendor.getId());
        }
        pager.setQuestionId(questionId);

        List<Question_anwsers> dataList = questionService.loadAnswerListPage(pager);

        model.addAttribute("dataList",dataList);
        return "admin/question_answer/answer_table";
    }

    @RequestMapping("/question/answer/edit/{id}")
    public String editAnswerPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        if(id != null){
            Question_anwsers data = questionService.getRowById(id);
            model.addAttribute("data",data);
        }

        return "admin/question_answer/answer_edit";
    }

    @RequestMapping("/question/answer/add/{questionId}")
    public String addAnswerPage(@PathVariable Integer questionId, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        model.addAttribute("questionId",questionId);
        return "admin/question_answer/answer_add";
    }

}
