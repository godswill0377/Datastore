package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("inbox_notify")
public class Inbox_Notify implements Serializable {


    private Integer id;
    private String title;
    private String content;
    private String to_user_ids;
    private String created_at;
    private String deleted_at;
    private String updated_at;
    private int read_flag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getRead_flag() {
        return read_flag;
    }

    public void setRead_flag(int read_flag) {
        this.read_flag = read_flag;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getTo_user_ids() {
        return to_user_ids;
    }

    public void setTo_user_ids(String to_user_ids) {
        this.to_user_ids = to_user_ids;
    }
}
