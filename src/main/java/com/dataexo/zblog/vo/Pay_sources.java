package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("pay_sources")
public class Pay_sources implements Serializable {


    private Integer id;
    private String customer_id;
    private Integer user_id;
    private String last_4_digits;
    private String card_type;
    private int is_default;
    private String update_at;
    private String card_number;
    private String card_expiry;
    private String card_cvc;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getLast_4_digits() {
        return last_4_digits;
    }

    public void setLast_4_digits(String last_4_digits) {
        this.last_4_digits = last_4_digits;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public int getIs_default() {
        return is_default;
    }

    public void setIs_default(int is_default) {
        this.is_default = is_default;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_expiry() {
        return card_expiry;
    }

    public void setCard_expiry(String card_expiry) {
        this.card_expiry = card_expiry;
    }

    public String getCard_cvc() {
        return card_cvc;
    }

    public void setCard_cvc(String card_cvc) {
        this.card_cvc = card_cvc;
    }
}
