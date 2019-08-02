package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_chart;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Data_sets_series;
import com.dataexo.zblog.vo.Pager;

import java.util.List;
import java.util.Map;


public interface Data_sets_seriesService {

    List<Data_sets_series> findAll();
    void saveData_sets_series(Data_sets_series data_sets_series);


    List<Data_sets_series> loadData_sets_series(Pager pager, Map<String, Object> param);

    Data_sets_series getData_sets_seriesById(Integer id);

    void deleteData_sets_series(Integer id);

    void deleteData_sets_seriesByParent(Integer id);

    void updateData_sets_series(Data_sets_series data_sets_series);

    void initPage(Pager pager);

    int getData_sets_count(Integer id);

    void updateChart_Diagram(Data_chart chart);

    void deleteDataByCode(String s);

    List<Data_sets_series> checkExist(Data_sets_series data_sets_series);

    List<Data_sets_series> loadThree_sets_series(Pager<Data_sets> pager);

}
