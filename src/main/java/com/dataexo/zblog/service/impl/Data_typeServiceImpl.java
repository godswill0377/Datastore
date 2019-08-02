package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_typeMapper;
import com.dataexo.zblog.service.Data_typeService;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Data_typeServiceImpl implements Data_typeService{

    @Resource
    private Data_typeMapper data_typeMapper;


    @Override
    public List<Data_type> findAll() {
        return data_typeMapper.findAll();
    }

    @Override
    public void saveData_type(Data_type data_type) {
        data_typeMapper.saveData_type(data_type);
    }

    @Override
    public List<Data_type> loadData_type(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return data_typeMapper.loadData_type(pager,param);
    }

    @Override
    public Data_type getData_typeById(Integer id) {
        return data_typeMapper.getData_typeById(id);
    }

    @Override
    public void deleteData_type(Integer id) {
        data_typeMapper.deleteData_type(id);
    }

    @Override
    public void updateData_type(Data_type Data_type) {
        data_typeMapper.updateData_type(Data_type);
    }

    @Override
    public void initPage(Pager pager) {
        int count = data_typeMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Data_type checkExist(String name){

            return data_typeMapper.checkExist(name);
    }
}
