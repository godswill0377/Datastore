package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;


@Alias("membership")
public class Membership {

    private Integer id;
    private Double m_single;
    private Double m_enterprise;
    private Double y_single;
    private Double y_enterprise;
    private Double m_single_value;
    private Double m_enterprise_value;
    private Double y_single_value;
    private Double y_enterprise_value;
    private int m_single_id;
    private int m_enterprise_id;
    private int y_single_id;
    private int y_enterprise_id;


    public Integer getId() {
        return id;
    }

    public int getM_single_id() {
        return m_single_id;
    }

    public void setM_single_id(int m_single_id) {
        this.m_single_id = m_single_id;
    }

    public int getM_enterprise_id() {
        return m_enterprise_id;
    }

    public void setM_enterprise_id(int m_enterprise_id) {
        this.m_enterprise_id = m_enterprise_id;
    }

    public int getY_single_id() {
        return y_single_id;
    }

    public void setY_single_id(int y_single_id) {
        this.y_single_id = y_single_id;
    }

    public int getY_enterprise_id() {
        return y_enterprise_id;
    }

    public void setY_enterprise_id(int y_enterprise_id) {
        this.y_enterprise_id = y_enterprise_id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getM_single() {
        return m_single;
    }

    public void setM_single(Double m_single) {
        this.m_single = m_single;
    }

    public Double getM_enterprise() {
        return m_enterprise;
    }

    public void setM_enterprise(Double m_enterprise) {
        this.m_enterprise = m_enterprise;
    }

    public Double getY_single() {
        return y_single;
    }

    public void setY_single(Double y_single) {
        this.y_single = y_single;
    }

    public Double getY_enterprise() {
        return y_enterprise;
    }

    public void setY_enterprise(Double y_enterprise) {
        this.y_enterprise = y_enterprise;
    }

    public Double getM_single_value() {
        return m_single_value;
    }

    public void setM_single_value(Double m_single_value) {
        this.m_single_value = m_single_value;
    }

    public Double getM_enterprise_value() {
        return m_enterprise_value;
    }

    public void setM_enterprise_value(Double m_enterprise_value) {
        this.m_enterprise_value = m_enterprise_value;
    }

    public Double getY_single_value() {
        return y_single_value;
    }

    public void setY_single_value(Double y_single_value) {
        this.y_single_value = y_single_value;
    }

    public Double getY_enterprise_value() {
        return y_enterprise_value;
    }

    public void setY_enterprise_value(Double y_enterprise_value) {
        this.y_enterprise_value = y_enterprise_value;
    }
}
