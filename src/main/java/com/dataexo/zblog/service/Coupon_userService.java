package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Coupon_user;
import com.dataexo.zblog.vo.Pager;

import java.util.List;

public interface Coupon_userService {
    List<Coupon_user> findAll();

    Coupon_user getById (int id);

    List<Coupon_user> getByCouponAndUserId (Pager pager);

    Coupon_user getByOrderId (Long order_id);

    void saveCoupon_user (Coupon_user coupon_user);

    void updateCoupon_user(Coupon_user coupon_user);
}
