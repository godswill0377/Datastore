package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("data_type")
public class Data_type implements Serializable {


    private Integer id;

    private String name;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "data_type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
