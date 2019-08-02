package com.dataexo.zblog.controller.vendor;

import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.service.InboxNotifyService;
import com.dataexo.zblog.service.InboxQuestionService;
import com.dataexo.zblog.util.Static;
import com.dataexo.zblog.vo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is base controller implements to get user's basic information
 * such as user's membership information , membership expire date, current balance, ...
 *
 */
@Controller
public abstract class VendorAbstractController {

    @Resource
    private InboxQuestionService questionService;

    @Resource
    private InboxNotifyService inboxNotifyService;

    public boolean baseRequest(HttpSession session ,Model model)
    {
         User user =(User) session.getAttribute(Static.VENDOR_USER_OBJ);
        Vendors vendor =(Vendors) session.getAttribute(Static.VENDOR_OBJ);
        if (user == null || vendor == null) {

            return false;
        }

        Pager pager = new Pager();
        pager.setVendor_id(Integer.parseInt("" + vendor.getId()));
        pager.setSearch_str("");

        List<Question_anwsers> dataList = questionService.loadQuestionList(pager);

        int read_nums = 0;
        for(int i = 0 ; i < dataList.size() ; i ++){
            Question_anwsers question = dataList.get(i);

            int size =  questionService.getUnreadNums(question.getId());

            read_nums += size;
        }


        //// calc notification

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date createDate = new Date();
        try {
            createDate = dateFormat.parse(user.getCreateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        pager.setUser_id(Integer.parseInt("" + user.getId()));

        int size = inboxNotifyService.getUnreadNums(pager);


        model.addAttribute("notify_read_nums",size);

        model.addAttribute("read_nums",read_nums);

        model.addAttribute("vendor_name", vendor.getLegal_name());

        model.addAttribute("vendor_buisness", vendor.getBusiness_name());

        model.addAttribute("join_date", dateFormat.format(createDate));

        model.addAttribute("notification", inboxNotifyService.loadInbox_Notify(pager));

        List<Question_anwsers> questionList = questionService.loadQuestionList(pager);

        model.addAttribute("question", questionList);

        List<Question_anwsers> anwserList = new ArrayList<>();

        for(int i=0; i<questionList.size(); i++){
            List<Question_anwsers> answers = questionService.loadAnswerList(questionList.get(i).getId());

            for(int j =0; j<answers.size(); j++){
                if(answers.get(j).getRead_flag()==0){
                    anwserList.add(questionList.get(i));
                    anwserList.add(answers.get(j));
                }
            }

        }

        model.addAttribute("answers", anwserList);

        return true;
    }

}