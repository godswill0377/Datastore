package com.dataexo.zblog.service;


import com.dataexo.zblog.vo.Data_frequency;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Question_anwsers;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface QuestionService {


    List<Question_anwsers> loadQuestionByDatasetId(Question_anwsers question);

    List<Question_anwsers> loadAnswerList(Integer id);

    void initPage(Pager<Question_anwsers> pager);

    Question_anwsers getRowById(Integer id);

    List<Question_anwsers> loadQuestionList(Pager<Question_anwsers> pager);

    void makeVote(Integer questionItemId, int votes );

    void postQuestion(Question_anwsers question, HttpSession session);

    void deleteQuestion(String id);

    void updateQuestion(Question_anwsers question_anwsers);

    List<Question_anwsers> loadAnswerListPage(Pager<Question_anwsers> pager);

    void initAnswerPage(Pager<Question_anwsers> pager);

    void updateAnswer(Question_anwsers question);

    void createAnswer(Question_anwsers question);

    void deleteAnswer(Integer id);
}
