package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;


@Alias("question_anwser")
public class Question_anwsers implements Serializable {

    private Integer id;

    private String content;
    private int dataset_id;
    private String dataset_name;
    private String question_by_name;
    private int question_by_userid;
    private int type;
    private int parent_id;
    private int votes;
    private String answer_by;
    private String deleted_date;
    private String updated_date;
    private int answer_nums;
    private List<Question_anwsers> anwsers_list;
    private int answers_num;

    private int read_flag ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getDataset_id() {
        return dataset_id;
    }

    public void setDataset_id(int dataset_id) {
        this.dataset_id = dataset_id;
    }

    public int getQuestion_by_userid() {
        return question_by_userid;
    }

    public void setQuestion_by_userid(int question_by_userid) {
        this.question_by_userid = question_by_userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getAnswer_by() {
        return answer_by;
    }

    public void setAnswer_by(String answer_by) {
        this.answer_by = answer_by;
    }

    public String getDeleted_date() {
        return deleted_date;
    }

    public void setDeleted_date(String deleted_date) {
        this.deleted_date = deleted_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public List<Question_anwsers> getAnwsers_list() {
        return anwsers_list;
    }

    public void setAnwsers_list(List<Question_anwsers> anwsers_list) {
        this.anwsers_list = anwsers_list;
    }

    public int getAnswers_num() {
        return answers_num;
    }

    public void setAnswers_num(int answers_num) {
        this.answers_num = answers_num;
    }

    public int getAnswer_nums() {
        return answer_nums;
    }

    public void setAnswer_nums(int answer_nums) {
        this.answer_nums = answer_nums;
    }

    public String getDataset_name() {
        return dataset_name;
    }

    public void setDataset_name(String dataset_name) {
        this.dataset_name = dataset_name;
    }

    public String getQuestion_by_name() {
        return question_by_name;
    }

    public void setQuestion_by_name(String question_by_name) {
        this.question_by_name = question_by_name;
    }

    public int getRead_flag() {
        return read_flag;
    }

    public void setRead_flag(int read_flag) {
        this.read_flag = read_flag;
    }
}
