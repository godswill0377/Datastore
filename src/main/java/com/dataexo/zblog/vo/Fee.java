package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("fee")
public class Fee implements Serializable {

    private Integer id;

    private String fee_name;

    private int fee_percent;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFee_name() {
        return fee_name;
    }

    public void setFee_name(String fee_name) {
        this.fee_name = fee_name;
    }

    public int getFee_percent() {
        return fee_percent;
    }

    public void setFee_percent(int fee_percent) {
        this.fee_percent = fee_percent;
    }
}
