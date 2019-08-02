package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("coupon_manage")
public class Coupon_manage implements Serializable {

    private Integer id;
    private String coupon;
    private int discount;
    private int coupon_for;
    private String created_date;
    private String expiry_date;
    private String dataset_ids;
    private String dataset_names;
    private int vendor_id;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getCoupon_for() {
        return coupon_for;
    }

    public void setCoupon_for(int coupon_for) {
        this.coupon_for = coupon_for;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getDataset_ids() {
        return dataset_ids;
    }

    public void setDataset_ids(String dataset_ids) {
        this.dataset_ids = dataset_ids;
    }

    public String getDataset_names() {
        return dataset_names;
    }

    public void setDataset_names(String dataset_names) {
        this.dataset_names = dataset_names;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }
}
