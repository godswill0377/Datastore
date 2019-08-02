package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.sql.Date;


@Alias("data_sets_series")
public class Data_sets_series implements Serializable {


    private Integer id;
    private String diagram_update_time;
    private String name;
    private String source_url;

    private String filter_id;

    private Integer data_set_id;

    private String embed_url_chart;
    private String embed_url_datagrid;
    public String getEmbed_url_chart() {
        return embed_url_chart;
    }

    public void setEmbed_url_chart(String embed_url_chart) {
        this.embed_url_chart = embed_url_chart;
    }

    public String getEmbed_url_datagrid() {
        return embed_url_datagrid;
    }

    public void setEmbed_url_datagrid(String embed_url_datagrid) {
        this.embed_url_datagrid = embed_url_datagrid;
    }




    public String getFilter_condition() {
        return filter_condition;
    }

    public void setFilter_condition(String filter_condition) {
        this.filter_condition = filter_condition;
    }

    private String filter_condition;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLatest_update_date() {
        return latest_update_date;
    }

    public void setLatest_update_date(String latest_update_date) {
        this.latest_update_date = latest_update_date;
    }

    public Integer getData_update_frequency_id() {
        return data_update_frequency_id;
    }

    public void setData_update_frequency_id(Integer data_update_frequency_id) {
        this.data_update_frequency_id = data_update_frequency_id;
    }

    public String getData_update_frequency_text() {
        return data_update_frequency_text;
    }

    public void setData_update_frequency_text(String data_update_frequency_text) {
        this.data_update_frequency_text = data_update_frequency_text;
    }

    public String getChat_diagram() {
        return chat_diagram;
    }

    public void setChat_diagram(String chat_diagram) {
        this.chat_diagram = chat_diagram;
    }

    private String description;
    private String code;
    private String latest_update_date;

    private Integer data_update_frequency_id;
    private String data_update_frequency_text;
    private String chat_diagram;

    private Integer is_sample_available;

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

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public String getDiagram_update_time() {
        return diagram_update_time;
    }

    public void setDiagram_update_time(String diagram_update_time) {
        this.diagram_update_time = diagram_update_time;
    }

    public Integer getIs_sample_available() {
        return is_sample_available;
    }

    public void setIs_sample_available(Integer is_sample_available) {
        this.is_sample_available = is_sample_available;
    }

    public String getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(String filter_id) {
        this.filter_id = filter_id;
    }

    public Integer getData_set_id() {
        return data_set_id;
    }

    public void setData_set_id(Integer data_set_id) {
        this.data_set_id = data_set_id;
    }
}
