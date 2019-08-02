package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

@Alias("withdraw")
public class Withdraw {

    private Integer id;

    private int vendor_id;

    private double withdraw_amount;

    private double available_balance;

    private String description;

    private String source;

    private String source_account;

    private String source_name;

    private String order_date;

    private int status;

    private String vendor_name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public double getWithdraw_amount() {
        return withdraw_amount;
    }

    public void setWithdraw_amount(double withdraw_amount) {
        this.withdraw_amount = withdraw_amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource_account() {
        return source_account;
    }

    public void setSource_account(String source_account) {
        this.source_account = source_account;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public double getAvailable_balance() {
        return available_balance;
    }

    public void setAvailable_balance(double available_balance) {
        this.available_balance = available_balance;
    }
}
