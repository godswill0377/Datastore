package com.dataexo.zblog.service.impl;


import com.dataexo.zblog.mapper.PurchaseMapper;
import com.dataexo.zblog.mapper.QuestionMapper;
import com.dataexo.zblog.service.PurchaseService;
import com.dataexo.zblog.service.QuestionService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Price_model;
import com.dataexo.zblog.vo.Question_anwsers;
import com.dataexo.zblog.vo.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Override
    public List<Question_anwsers> loadQuestionByDatasetId(Question_anwsers question){
        return questionMapper.loadQuestionByDatasetId(question);
    }

    @Override
    public List<Question_anwsers> loadAnswerList(Integer id){
        return questionMapper.loadAnswerList(id);
    }

    @Override
    public Question_anwsers getRowById(Integer id){
        return questionMapper.getRowById(id);
    }


    @Override
    public void initPage(Pager pager) {
        int count = questionMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public List<Question_anwsers> loadQuestionList(Pager pager) {
        pager.setStart(pager.getStart());
        return questionMapper.loadQuestionList(pager);
    }

    @Override
    public void deleteQuestion(String id) {
        questionMapper.deleteQuestion(id);
    }

    @Override
    public void updateQuestion(Question_anwsers question_anwsers) {
        questionMapper.updateQuestion(question_anwsers);
    }

    @Override
    public List<Question_anwsers> loadAnswerListPage(Pager<Question_anwsers> pager) {
        pager.setStart(pager.getStart());
        return questionMapper.loadAnswerListPage(pager);
    }

    @Override
    public void initAnswerPage(Pager<Question_anwsers> pager) {
        int count = questionMapper.initAnswerPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public void updateAnswer(Question_anwsers answer) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        answer.setUpdated_date(dateFormat.format(date));
        questionMapper.updateAnswer(answer);
    }

    @Override
    public void createAnswer(Question_anwsers answer) {

        Question_anwsers question = getRowById(answer.getParent_id());
        if(question != null){
            answer.setQuestion_by_userid( question.getQuestion_by_userid());
            answer.setDataset_id(question.getDataset_id());
        }

        answer.setType(2);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        answer.setUpdated_date(dateFormat.format(date));
        answer.setVotes(0);
        questionMapper.createAnswer(answer);
    }

    @Override
    public void deleteAnswer(Integer id) {
        Question_anwsers question = getRowById(id);
        if(question != null){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            question.setDeleted_date(dateFormat.format(date));
            questionMapper.deleteAnswer(question);
        }

    }

    public void makeVote(Integer questionItemId, int value ){
         questionMapper.makeVote(questionItemId, value);
    }

    @Override
    public void postQuestion(Question_anwsers question, HttpSession session) {

        User userinfo = (User) session.getAttribute("user");
        if(userinfo != null){
            question.setQuestion_by_userid((int) userinfo.getId());
        }
        question.setType(1);
        question.setParent_id(0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        question.setUpdated_date(dateFormat.format(date));
        question.setVotes(0);
        questionMapper.postQuestion(question);


    }

}
