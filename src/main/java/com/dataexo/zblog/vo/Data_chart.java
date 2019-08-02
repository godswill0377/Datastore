package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("data_chart")
public class Data_chart implements Serializable {

    private Integer id;

    private String data;
private String update_date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }
}
