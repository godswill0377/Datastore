package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("data_sales")
public class Data_sales implements Serializable {


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getData_set_name() {
        return data_set_name;
    }

    public void setData_set_name(String data_set_name) {
        this.data_set_name = data_set_name;
    }

    public Integer getData_set_id() {
        return data_set_id;
    }

    public void setData_set_id(Integer data_set_id) {
        this.data_set_id = data_set_id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    private Integer id;

    private Integer userid;
    private String username;

    private String data_set_name;

    private Integer data_set_id;
    private float amount;
    private Integer pay_time;

    @Override
    public String toString() {
        return "data_sales{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    public Integer getPay_time() {
        return pay_time;
    }

    public void setPay_time(Integer pay_time) {
        this.pay_time = pay_time;
    }
}
