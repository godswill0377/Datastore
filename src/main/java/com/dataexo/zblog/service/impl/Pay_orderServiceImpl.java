package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Pay_logMapper;
import com.dataexo.zblog.mapper.Pay_orderMapper;
import com.dataexo.zblog.service.Pay_logService;
import com.dataexo.zblog.service.Pay_orderService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Pay_order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Pay_orderServiceImpl implements Pay_orderService {

    @Resource
    private Pay_orderMapper pay_orderMapper;

    @Override
    public void insertPayOrder(Pay_order pay_order) {
        pay_orderMapper.insertPayOrder(pay_order);
    }

    @Override
    public Pay_order getPayOrder(Integer id) {
        return  pay_orderMapper.getPayOrder(id);
    }

    @Override
    public void deletePayOrder(Integer id) {
        pay_orderMapper.deletePayOrder(id);
    }

}
