package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Alias("pay_order")
public class Pay_order implements Serializable {

    private long id;
    private Float amount;
    private String dataset_ids;
    private String membership_id;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDataset_ids() {
        return dataset_ids;
    }

    public void setDataset_ids(String dataset_ids) {
        this.dataset_ids = dataset_ids;
    }

    public String getMembership_id() {
        return membership_id;
    }

    public void setMembership_id(String membership_id) {
        this.membership_id = membership_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}