package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;

@Alias("customer_review")
public class Customer_reviews implements Serializable {

    private Integer id;
    private String content;
    private Integer stars;
    private Integer customer_id;

    private Integer dataset_id;
    private String dataset_name;

    private String updated_date;
    private String deleted_date;
    private String additional_imgs;
    private int helpful_num;
    private String customer_name;
    private String title;

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Integer getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(Integer customer_id) {
        this.customer_id = customer_id;
    }

    public Integer getDataset_id() {
        return dataset_id;
    }

    public void setDataset_id(Integer dataset_id) {
        this.dataset_id = dataset_id;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getDeleted_date() {
        return deleted_date;
    }

    public void setDeleted_date(String deleted_date) {
        this.deleted_date = deleted_date;
    }

    public String getAdditional_imgs() {
        return additional_imgs;
    }

    public void setAdditional_imgs(String additional_imgs) {
        this.additional_imgs = additional_imgs;
    }

    public int getHelpful_num() {
        return helpful_num;
    }

    public void setHelpful_num(int helpful_num) {
        this.helpful_num = helpful_num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataset_name() {
        return dataset_name;
    }

    public void setDataset_name(String dataset_name) {
        this.dataset_name = dataset_name;
    }
}
