package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Price_modelMapper;
import com.dataexo.zblog.service.Price_modelService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Price_model;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Price_modelServiceImpl implements Price_modelService{

    @Resource
    private Price_modelMapper price_modelMapper;


    @Override
    public List<Price_model> findAll() {
        return price_modelMapper.findAll();
    }

    @Override
    public void savePrice_model(Price_model price_model) {
        price_modelMapper.savePrice_model(price_model);
    }

    @Override
    public List<Price_model> loadPrice_model(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return price_modelMapper.loadPrice_model(pager,param);
    }

    @Override
    public Price_model getPrice_modelById(Integer id) {
        return price_modelMapper.getPrice_modelById(id);
    }

    @Override
    public void deletePrice_model(Integer id) {
        price_modelMapper.deletePrice_model(id);
    }

    @Override
    public void updatePrice_model(Price_model price_model) {
        price_modelMapper.updatePrice_model(price_model);
    }

    @Override
    public void initPage(Pager pager) {
        int count = price_modelMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Price_model checkExist(String name){
        return price_modelMapper.checkExist(name);
    }
}
