package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("plan")
public class Plan implements Serializable {

    private Integer id;
    private String plan_id;
    private String plan_name;
    private String frequency;
    private Double real_price;
    private Double vr_price;
    private int vendor_id;
    private String vendor_name;

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Double getReal_price() {
        return real_price;
    }

    public void setReal_price(Double real_price) {
        this.real_price = real_price;
    }

    public Double getVr_price() {
        return vr_price;
    }

    public void setVr_price(Double vr_price) {
        this.vr_price = vr_price;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }
}
