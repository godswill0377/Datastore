package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_frequencyMapper;
import com.dataexo.zblog.mapper.Data_typeMapper;
import com.dataexo.zblog.service.Data_FrequencyService;
import com.dataexo.zblog.service.Data_typeService;
import com.dataexo.zblog.vo.Data_frequency;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Data_FrequencyServiceImpl implements Data_FrequencyService{

    @Resource
    private Data_frequencyMapper data_frequencyMapper;



    @Override
    public List<Data_frequency> findAll(){
        return data_frequencyMapper.findAll();
    }

    @Override
    public void saveData_frequency(Data_frequency data_frequency){
         data_frequencyMapper.saveData_frequency(data_frequency);
    }


    @Override
    public List<Data_frequency> loadData_frequency(Pager pager, String param){
        return  data_frequencyMapper.loadData_frequency(pager,param);
    }

    @Override
    public Data_frequency getData_frequencyById(Integer id){
        return  data_frequencyMapper.getData_frequencyById(id);
    }

    @Override
    public void deleteData_frequency(Integer id){
         data_frequencyMapper.deleteData_frequency(id);
    }

    @Override
    public void updateData_frequency(Data_frequency data_frequency){
        data_frequencyMapper.updateData_frequency(data_frequency);
    }

    @Override
    public void initPage(Pager pager){
        int count = data_frequencyMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Data_frequency checkExist(String name){
        return data_frequencyMapper.checkExist(name);
    }
}
