package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Coupon_manageMapper;
import com.dataexo.zblog.service.Coupon_manageService;
import com.dataexo.zblog.vo.Coupon_manage;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class Coupon_manageServiceImpl implements Coupon_manageService {

    @Resource
    private Coupon_manageMapper coupon_manageMapper;
    @Override
    public void initPage(Pager pager) {
        Integer count = coupon_manageMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public List<Coupon_manage> findAll() {
        return coupon_manageMapper.findAll();
    }

    @Override
    public Coupon_manage getById(int id) {
        return coupon_manageMapper.getById(id);
    }

    @Override
    public Coupon_manage getByCoupon(String coupon) {
        return coupon_manageMapper.getByCoupon(coupon);
    }


    @Override
    public List<Coupon_manage> loadCoupon(Pager pager) {
        return coupon_manageMapper.loadCoupon(pager);
    }

    @Override
    public void saveCoupon(Coupon_manage coupon_manage) {
        coupon_manageMapper.saveCoupon(coupon_manage);
    }

    @Override
    public void updateCoupon(Coupon_manage coupon_manage) {
        coupon_manageMapper.updateCoupon(coupon_manage);
    }
}
