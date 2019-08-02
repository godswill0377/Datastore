package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Timestamp;


@Alias("contact_us")
public class Contact_us implements Serializable {

    private Integer id;
    private String user_name;
    private String email;
    private String message;
    private String submit_timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubmit_timestamp() {
        return submit_timestamp;
    }

    public void setSubmit_timestamp(String submit_timestamp) {
        this.submit_timestamp = submit_timestamp;
    }




}
