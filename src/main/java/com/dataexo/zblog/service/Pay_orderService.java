package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Pay_order;

import java.util.List;

public interface Pay_orderService {

    void insertPayOrder(Pay_order pay_log);
    Pay_order getPayOrder(Integer id);
    void deletePayOrder(Integer id);

}
