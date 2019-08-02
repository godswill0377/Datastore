package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Coupon_manage;
import com.dataexo.zblog.vo.Pager;

import java.util.List;

public interface Coupon_manageService {
    void initPage (Pager pager);

    List<Coupon_manage> findAll();

    Coupon_manage getById(int id);

    Coupon_manage getByCoupon(String coupon);

    List<Coupon_manage> loadCoupon (Pager pager);

    void saveCoupon (Coupon_manage coupon_manage);

    void updateCoupon (Coupon_manage coupon_manage);
}
