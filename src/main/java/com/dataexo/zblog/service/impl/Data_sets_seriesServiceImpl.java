package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_setsMapper;
import com.dataexo.zblog.mapper.Data_sets_seriesMapper;
import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.service.Data_sets_seriesService;
import com.dataexo.zblog.vo.Data_chart;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Data_sets_series;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class Data_sets_seriesServiceImpl implements Data_sets_seriesService {

    @Resource
    private Data_sets_seriesMapper data_sets_seriesMapper;


    @Override
    public List<Data_sets_series> findAll() {
        return data_sets_seriesMapper.findAll();
    }

    @Override
    public void saveData_sets_series(Data_sets_series data_sets_series) {
        data_sets_seriesMapper.saveData_sets_series(data_sets_series);
    }

    @Override
    public List<Data_sets_series> loadData_sets_series(Pager pager, Map<String, Object> param) {
        return data_sets_seriesMapper.loadData_sets_series(pager , param);
    }

    @Override
    public Data_sets_series getData_sets_seriesById(Integer id) {
        return data_sets_seriesMapper.getData_sets_seriesById(id);
    }

    @Override
    public void deleteData_sets_series(Integer id) {
        data_sets_seriesMapper.deleteData_sets_series(id);
    }

    @Override
    public void deleteData_sets_seriesByParent(Integer id) {
        data_sets_seriesMapper.deleteData_sets_seriesByParent(id);
    }



    @Override
    public void deleteDataByCode(String code) {
        data_sets_seriesMapper.deleteDataByCode(code);
    }


    @Override
    public void updateChart_Diagram(Data_chart chart){
        data_sets_seriesMapper.updateChart_Diagram(chart);
    }

    @Override
    public void updateData_sets_series(Data_sets_series data_sets_series) {
        data_sets_seriesMapper.updateData_sets_series(data_sets_series);
    }

    @Override
    public void initPage(Pager pager) {
        int count = data_sets_seriesMapper.initPage(pager);
        pager.setTotalCount(count);
    }
    @Override
    public int getData_sets_count(Integer id){
        return data_sets_seriesMapper.getData_sets_count(id);
    }

    @Override
    public List<Data_sets_series> checkExist(Data_sets_series data_sets_series){
        return data_sets_seriesMapper.checkExist(data_sets_series);
    }
    @Override
    public List<Data_sets_series> loadThree_sets_series(Pager<Data_sets> pager){
        return data_sets_seriesMapper.loadThree_sets_series(pager);
    }

}
