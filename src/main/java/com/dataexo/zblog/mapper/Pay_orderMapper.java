package com.dataexo.zblog.mapper;


import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Pay_order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Pay_orderMapper {


    void insertPayOrder(Pay_order pay_log);

    Pay_order getPayOrder(Integer id);

    void deletePayOrder(Integer id);
}
