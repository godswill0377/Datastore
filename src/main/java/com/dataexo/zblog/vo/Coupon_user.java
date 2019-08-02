package com.dataexo.zblog.vo;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Alias("coupon_user")
public class Coupon_user implements Serializable {

    private Integer id;

    private int coupon_id;

    private Long user_id;

    private Long order_id;

    private int used_flag;

    private int discount;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(int coupon_id) {
        this.coupon_id = coupon_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Long order_id) {
        this.order_id = order_id;
    }

    public int getUsed_flag() {
        return used_flag;
    }

    public void setUsed_flag(int used_flag) {
        this.used_flag = used_flag;
    }
}
