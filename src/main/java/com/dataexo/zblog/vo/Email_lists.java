package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("email_lists")
public class Email_lists implements Serializable {

    private Integer id;
   private String title;
   private String content;
   private Integer vendor_id;
   private String dataset_ids;
    private String dataset_names;

   private String email_address;
   private Integer email_nums;
   private String updated_at;

   private int couponOrEmail;

    public int getCouponOrEmail() {
        return couponOrEmail;
    }

    public void setCouponOrEmail(int couponOrEmail) {
        this.couponOrEmail = couponOrEmail;
    }

    private Integer send_flag;

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

    public Integer getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(Integer vendor_id) {
        this.vendor_id = vendor_id;
    }

    public String getDataset_ids() {
        return dataset_ids;
    }

    public void setDataset_ids(String dataset_ids) {
        this.dataset_ids = dataset_ids;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public Integer getEmail_nums() {
        return email_nums;
    }

    public void setEmail_nums(Integer email_nums) {
        this.email_nums = email_nums;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDataset_names() {
        return dataset_names;
    }

    public void setDataset_names(String dataset_names) {
        this.dataset_names = dataset_names;
    }

    public Integer getSend_flag() {
        return send_flag;
    }

    public void setSend_flag(Integer send_flag) {
        this.send_flag = send_flag;
    }
}
