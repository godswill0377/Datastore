package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Publisher;
import com.dataexo.zblog.vo.Question_anwsers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface QuestionMapper {

    List<Question_anwsers> loadQuestionByDatasetId(Question_anwsers question);

    List<Question_anwsers> loadAnswerList(Integer id);

    int initPage(Pager pager);

    Question_anwsers getRowById(Integer id);

    List<Question_anwsers> loadQuestionList(Pager pager);

    /**
     * make a vote for the answer
     * @param questionItemId
     * @param votes
     * @return
     */
    void makeVote(@Param("questionItemId") Integer questionItemId, @Param("votes") int votes);

    void postQuestion(Question_anwsers question);

    void deleteQuestion(String id);

    void updateQuestion(Question_anwsers question_anwsers);

    List<Question_anwsers> loadAnswerListPage(Pager<Question_anwsers> pager);

    int initAnswerPage(Pager<Question_anwsers> pager);

    void updateAnswer(Question_anwsers question);

    void createAnswer(Question_anwsers answer);

    void deleteAnswer(Question_anwsers answer);
}
