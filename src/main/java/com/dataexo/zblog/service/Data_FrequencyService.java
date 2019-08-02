package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_frequency;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface Data_FrequencyService {

    List<Data_frequency> findAll();

    void saveData_frequency(Data_frequency data_frequency);


    List<Data_frequency> loadData_frequency(Pager pager, String param);

    Data_frequency getData_frequencyById(Integer id);

    void deleteData_frequency(Integer id);

    void updateData_frequency(Data_frequency data_frequency);

    void initPage(Pager pager);

    Data_frequency checkExist(String name);
}
