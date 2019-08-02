package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_categoryMapper;
import com.dataexo.zblog.mapper.Data_salesMapper;
import com.dataexo.zblog.service.Data_categoryService;
import com.dataexo.zblog.service.Data_salesService;
import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Data_sales;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Data_salesServiceImpl implements Data_salesService {

    @Resource
    private Data_salesMapper data_salesMapper;

    @Override
    public List<Data_sales> findAll() {
        return data_salesMapper.findAll();
    }

    @Override
    public void saveData_sales(Data_sales data_sales) {
        data_salesMapper.saveData_sales(data_sales);
    }

    @Override
    public List<Data_sales> loadData_sales(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return data_salesMapper.loadData_sales(pager,param);
    }

    @Override
    public Data_sales checkExist(String name){
        return data_salesMapper.checkExist(name);
    }

    @Override
    public Data_sales getData_salesById(Integer id) {
        return data_salesMapper.getData_salesById(id);
    }

    @Override
    public void deleteData_sales(Integer id) {
        data_salesMapper.deleteData_sales(id);
    }

    @Override
    public void updateData_sales(Data_sales data_sales) {
        data_salesMapper.updateData_sales(data_sales);
    }

    @Override
    public void initPage(Pager pager) {
        int count = data_salesMapper.initPage(pager);
        pager.setTotalCount(count);
    }
    @Override
    public float getTotalPrice(Data_sales data_sales){
        return  data_salesMapper.getTotalPrice(data_sales);
    }

}
