package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_typeMapper;
import com.dataexo.zblog.mapper.Pay_logMapper;
import com.dataexo.zblog.service.Data_typeService;
import com.dataexo.zblog.service.Pay_logService;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Pay_logServiceImpl implements Pay_logService{

    @Resource
    private Pay_logMapper pay_logMapper;

    @Override
    public void savePay_log(Pay_log pay_log) {
         pay_logMapper.savePay_log(pay_log);
    }

    @Override
    public  List<Pay_log> loadPay_log(Pager pager) {
        return pay_logMapper.loadPay_log(pager);
    }

    @Override
    public  void deletePay_log(Integer id) {
         pay_logMapper.deletePay_log(id);
    }

    @Override
    public float getTotalPrice(Pager pager){
        return pay_logMapper.getTotalPrice(pager);
    }

    @Override
    public   void initPage(Pager pager){

        int count = pay_logMapper.initPage(pager);
        pager.setTotalCount(count);

    }
}
