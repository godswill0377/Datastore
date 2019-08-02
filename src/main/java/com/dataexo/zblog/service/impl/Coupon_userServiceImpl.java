package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Coupon_userMapper;
import com.dataexo.zblog.service.Coupon_userService;
import com.dataexo.zblog.vo.Coupon_user;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class Coupon_userServiceImpl implements Coupon_userService {

    @Resource
    private Coupon_userMapper coupon_userMapper;

    @Override
    public List<Coupon_user> findAll() {
        return coupon_userMapper.findAll();
    }

    @Override
    public Coupon_user getById(int id) {
        return coupon_userMapper.getById(id);
    }

    @Override
    public List<Coupon_user> getByCouponAndUserId(Pager pager) {
        return coupon_userMapper.getByCouponAndUserId(pager);
    }

    @Override
    public Coupon_user getByOrderId(Long order_id) {
        return coupon_userMapper.getByOrderId(order_id);
    }

    @Override
    public void saveCoupon_user(Coupon_user coupon_user) {
        coupon_userMapper.saveCoupon_user(coupon_user);
    }

    @Override
    public void updateCoupon_user(Coupon_user coupon_user) {
        coupon_userMapper.updateCoupon_user(coupon_user);
    }
}
