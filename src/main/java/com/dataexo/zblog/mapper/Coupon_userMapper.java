package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Coupon_user;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Coupon_userMapper {

    List<Coupon_user> findAll();

    Coupon_user getById (int id);

    Coupon_user getByOrderId(Long order_id);

    List<Coupon_user> getByCouponAndUserId(Pager pager);

    void saveCoupon_user (Coupon_user coupon_user);

    void updateCoupon_user(Coupon_user coupon_user);

}
