package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Pay_SourceMapper;
import com.dataexo.zblog.service.Pay_SourceService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_sources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class Pay_SourceServiceImpl implements Pay_SourceService{

    @Resource
    private Pay_SourceMapper pay_sourceMapper;

    @Override
    public List<Pay_sources> findAll() {
        return pay_sourceMapper.findAll();
    }

    @Override
    public void savePay_Source(Pay_sources pay_source) {
         pay_sourceMapper.savePay_Source(pay_source);
    }

    @Override
    public List<Pay_sources> loadPay_Source(Pager pager) {
        return pay_sourceMapper.loadPay_Source(pager);
    }

    @Override
    public Pay_sources getPay_SourceById(Integer id) {
        return pay_sourceMapper.getPay_SourceById(id);
    }

    @Override
    public void deletePay_Source(Integer id) {
         pay_sourceMapper.deletePay_Source(id);
    }

    @Override
    public void updatePay_Source(Pay_sources pay_source) {
         pay_sourceMapper.updatePay_Source(pay_source);
    }

    @Override
    public void setDefaultCard(Pay_sources pay_source) {
        pay_sourceMapper.setDefaultCard(pay_source);
    }



}
