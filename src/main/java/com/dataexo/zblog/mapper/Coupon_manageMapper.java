package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Coupon_manage;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Coupon_manageMapper {

    Integer initPage (Pager pager);

    List<Coupon_manage> findAll ();

    List<Coupon_manage> loadCoupon (Pager pager);

    Coupon_manage getById(int id);

    Coupon_manage getByCoupon (String coupon);

    List<Coupon_manage> getByVendorId (int vendor_id);

    void saveCoupon (Coupon_manage coupon_manage);

    void updateCoupon (Coupon_manage coupon_manage);
}
