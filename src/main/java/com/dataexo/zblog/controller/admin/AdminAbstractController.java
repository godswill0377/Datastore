package com.dataexo.zblog.controller.admin;

import com.dataexo.zblog.service.BucketService;
import com.dataexo.zblog.service.Data_categoryService;
import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.service.InboxQuestionService;
import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * This is base controller implements to get user's basic information
 * such as user's membership information , membership expire date, current balance, ...
 *
 */
@Controller
public abstract class AdminAbstractController {

    @Resource
    private InboxQuestionService questionService;

    public boolean baseRequest(HttpSession session, Model model )
    {
        User userinfo = (User) session.getAttribute("adminuser");
        if(userinfo == null){

            return false;
        }

        int nums = questionService.getUnreadQuestion();
        model.addAttribute("unread_nums", nums);
        return true;
    }

}