package com.dataexo.zblog.service.impl;


import com.dataexo.zblog.mapper.InboxQuestionMapper;
import com.dataexo.zblog.service.InboxQuestionService;
import com.dataexo.zblog.vo.Pager;
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
public class InboxQuestionServiceImpl implements InboxQuestionService {

    @Resource
    private InboxQuestionMapper inboxQuestionMapper;

    @Override
    public List<Question_anwsers> loadQuestionByDatasetId(Question_anwsers question){
        return inboxQuestionMapper.loadQuestionByDatasetId(question);
    }

    @Override
    public List<Question_anwsers> loadAnswerList(Integer id){
        return inboxQuestionMapper.loadAnswerList(id);
    }

    @Override
    public Question_anwsers getRowById(Integer id){
        return inboxQuestionMapper.getRowById(id);
    }


    @Override
    public void initPage(Pager pager) {
        int count = inboxQuestionMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public List<Question_anwsers> loadQuestionList(Pager pager) {
        pager.setStart(pager.getStart());
        return inboxQuestionMapper.loadQuestionList(pager);
    }

    @Override
    public void deleteQuestion(String id) {
        inboxQuestionMapper.deleteQuestion(id);
    }

    @Override
    public void updateQuestion(Question_anwsers question_anwsers) {
        inboxQuestionMapper.updateQuestion(question_anwsers);
    }

    @Override
    public List<Question_anwsers> loadAnswerListPage(Pager<Question_anwsers> pager) {
        pager.setStart(pager.getStart());
        return inboxQuestionMapper.loadAnswerListPage(pager);
    }

    @Override
    public void initAnswerPage(Pager<Question_anwsers> pager) {
        int count = inboxQuestionMapper.initAnswerPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public void updateAnswer(Question_anwsers answer) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        answer.setUpdated_date(dateFormat.format(date));
        inboxQuestionMapper.updateAnswer(answer);
    }

    @Override
    public void createQuestion(Question_anwsers answer) {


        answer.setType(1);
        answer.setRead_flag(0);
        answer.setParent_id(0);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        answer.setUpdated_date(dateFormat.format(date));
        inboxQuestionMapper.createQuestion(answer);
    }

    @Override
    public void createAnswer(Question_anwsers answer) {

        Question_anwsers question = getRowById(answer.getParent_id());
        if(question != null){
            answer.setQuestion_by_userid( question.getQuestion_by_userid());
        }

        answer.setType(2);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        answer.setUpdated_date(dateFormat.format(date));
        answer.setVotes(0);
        inboxQuestionMapper.createAnswer(answer);
    }

    @Override
    public void deleteAnswer(Integer id) {
        Question_anwsers question = getRowById(id);
        if(question != null){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            question.setDeleted_date(dateFormat.format(date));
            inboxQuestionMapper.deleteAnswer(question);
        }

    }

    public void makeVote(Integer questionItemId, int value ){
        inboxQuestionMapper.makeVote(questionItemId, value);
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
        inboxQuestionMapper.postQuestion(question);
    }

    @Override
    public void eraseAnswerReadFlag(Integer id) {
        inboxQuestionMapper.eraseAnswerReadFlag(id);
    }

    @Override
    public int getUnreadNums(Integer id) {
       return inboxQuestionMapper.getUnreadNums(id);
    }


    /**
     * This is for admin panel function.
     * This is called to get new vendor's questions nums
     * @return
     */
    @Override
    public int getUnreadQuestion() {
        return inboxQuestionMapper.getUnreadQuestion();
    }
}
