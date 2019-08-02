package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("data_set_filters")
public class Data_set_filters implements Serializable {


    private Integer id;

    private String column_name;

    private Integer data_set_id;

    private String filter_value;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public Integer getData_set_id() {
        return data_set_id;
    }

    public void setData_set_id(Integer data_set_id) {
        this.data_set_id = data_set_id;
    }

    public String getFilter_value() {
        return filter_value;
    }

    public void setFilter_value(String filter_value) {
        this.filter_value = filter_value;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    private String comparator;

}
