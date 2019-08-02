package com.dataexo.zblog.controller.admin;

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
 * This is controller class implements an management of inbox_question in admin panel.
 * This class contains three main pages function. (list page , add page, edit page).
 * There is also getting list data per page function.
 * This controller used inbox_questionService class to get data from database.
 * inbox_questionService is for inbox_question.
 *
 * <p> This class is integrate with templates/admin/inbox_question folder
 *  for example  , when the return result is "admin/inbox_question/table" , it means admin/inbox_question/table.html file
 *  so the browser loads the admin/inbox_question/table.html file for frontend.

 * @version 1.0
 * @author lang
 * @since 2017-06-22
 */


@Controller
@RequestMapping("/admin/inbox")
public class AdminInboxQuestionsController extends AdminAbstractController {



    @Resource
    private InboxQuestionService questionService;

    @Resource
    private VendorService vendorService;

    @RequestMapping("/question/initPage")
    @ResponseBody
    public Pager initPage(Pager<Question_anwsers> pager, HttpSession session){
        questionService.initPage(pager);
        return pager;
    }

    @RequestMapping("/question/list")
    public String listPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }
        return "admin/inbox/question_list";
    }


    @RequestMapping("/question/edit/{id}")
    public String editQuestionPage(@PathVariable Integer id, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        if(id != null){
            Question_anwsers data = questionService.getRowById(id);
            Vendors vendors = vendorService.getVendorIDById(data.getQuestion_by_userid());
            data.setQuestion_by_name(vendors.getLegal_name());

            model.addAttribute("data",data);
        }

        return "admin/inbox/question_edit";
    }


    @RequestMapping("/question/load")
    public String loadList(Pager<Question_anwsers> pager,String name, Model model){

        pager.setSearch_str(name);
        List<Question_anwsers> dataList = questionService.loadQuestionList(pager);

        for(int i = 0 ; i < dataList.size() ; i ++){
            Question_anwsers answer = dataList.get(i);

            int size = questionService.loadAnswerList(answer.getId()).size();
            answer.setAnswers_num(size);

            Vendors vendors = vendorService.getVendorIDById((long) answer.getQuestion_by_userid());
            if(vendors != null){
                answer.setQuestion_by_name(vendors.getLegal_name());
            }
            else{
                answer.setQuestion_by_name("The vendor deleted");
            }

            dataList.set(i, answer);
        }

        model.addAttribute("dataList",dataList);
        return "admin/inbox/question_table";
    }

    @RequestMapping("/question/add")
    public String addQuestionPage(HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        return "admin/inbox/question_add";
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
            return "redirect:/login";
        }

        model.addAttribute("questionId",questionId);
        return "admin/inbox/answer_list";
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
        return "admin/inbox/answer_table";
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

        return "admin/inbox/answer_edit";
    }

    @RequestMapping("/question/answer/add/{questionId}")
    public String addAnswerPage(@PathVariable Integer questionId, HttpSession session, Model model){

        if(!baseRequest(session,model)){
            return "redirect:/login";
        }

        model.addAttribute("questionId",questionId);
        return "admin/inbox/answer_add";
    }
}
