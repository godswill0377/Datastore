package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_categoryMapper;
import com.dataexo.zblog.service.Data_categoryService;
import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Data_categoryServiceImpl implements Data_categoryService{

    @Resource
    private Data_categoryMapper data_categoryMapper;

    @Override
    public List<Data_category> findAll() {
        return data_categoryMapper.findAll();
    }

    @Override
    public void saveData_category(Data_category data_category) {
        data_categoryMapper.saveData_category(data_category);
    }

    @Override
    public List<Data_category> loadData_category(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return data_categoryMapper.loadData_category(pager,param);
    }

    @Override
    public Data_category checkExist(String name){
        return data_categoryMapper.checkExist(name);
    }

    @Override
    public Data_category getData_categoryById(Integer id) {
        return data_categoryMapper.getData_categoryById(id);
    }

    @Override
    public void deleteData_category(Integer id) {
        data_categoryMapper.deleteData_category(id);
    }

    @Override
    public void updateData_category(Data_category data_category) {
        data_categoryMapper.updateData_category(data_category);
    }

    @Override
    public void initPage(Pager pager) {
        int count = data_categoryMapper.initPage(pager);
        pager.setTotalCount(count);
    }

}
