package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("trans_log")
public class Trans_log implements Serializable {
    private Integer id;
    private int trans_type;
    private double amount;
    private double raw_amount;

    private String date;
    private int status;
    private String description;
    private int vendor_id;
    private String vendor_name;
    private int datasets_id;

    public int getDatasets_id() {
        return datasets_id;
    }

    public void setDatasets_id(int datasets_id) {
        this.datasets_id = datasets_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(int trans_type) {
        this.trans_type = trans_type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }

    public double getRaw_amount() {
        return raw_amount;
    }

    public void setRaw_amount(double raw_amount) {
        this.raw_amount = raw_amount;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }
}
