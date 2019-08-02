package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Price_model;

import java.util.List;


public interface Price_modelService {

    List<Price_model> findAll();

    void savePrice_model(Price_model price_model);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Price_model> loadPrice_model(Pager pager, String param);

    Price_model getPrice_modelById(Integer id);

    void deletePrice_model(Integer id);

    void updatePrice_model(Price_model price_model);

    void initPage(Pager pager);

    Price_model checkExist(String name);
}
