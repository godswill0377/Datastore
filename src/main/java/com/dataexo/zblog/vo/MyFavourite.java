package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("favourite")
public class MyFavourite implements Serializable {


    private long id;
    private long userid;
    private long series_id;

    private String series_name;
    private String series_description;

    private Integer cateid;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getSeries_id() {
        return series_id;
    }

    public void setSeries_id(long series_id) {
        this.series_id = series_id;
    }

    public String getSeries_name() {
        return series_name;
    }

    public void setSeries_name(String series_name) {
        this.series_name = series_name;
    }

    public String getSeries_description() {
        return series_description;
    }

    public void setSeries_description(String series_description) {
        this.series_description = series_description;
    }

    public Integer getCateid() {
        return cateid;
    }

    public void setCateid(Integer cateid) {
        this.cateid = cateid;
    }
}
