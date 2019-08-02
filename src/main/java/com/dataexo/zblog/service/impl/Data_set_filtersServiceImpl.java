package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_set_filtersMapper;
import com.dataexo.zblog.mapper.RegionMapper;
import com.dataexo.zblog.service.Data_set_filtersService;
import com.dataexo.zblog.service.RegionService;
import com.dataexo.zblog.vo.Data_set_filters;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Region;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Data_set_filtersServiceImpl implements Data_set_filtersService{

    @Resource
    private Data_set_filtersMapper data_set_filtersMapper;

    @Override
    public List<Data_set_filters> findAll() {
        return data_set_filtersMapper.findAll();
    }

    @Override
    public void saveFilter(Data_set_filters filters) {
        data_set_filtersMapper.saveFilter(filters);
    }

    @Override
    public List<Data_set_filters> loadFilter(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return data_set_filtersMapper.loadFilter(pager,param);
    }

    @Override
    public Data_set_filters getFilterById(Integer id) {
        return data_set_filtersMapper.getFilterById(id);
    }

    @Override
    public void deleteFilter(Integer id) {
        data_set_filtersMapper.deleteFilter(id);
    }

    @Override
    public void updateFilter(Data_set_filters filters) {
        data_set_filtersMapper.updateFilter(filters);
    }

    @Override
    public void initPage(Pager pager) {
        int count = data_set_filtersMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Data_set_filters checkExist(String name){
          return  data_set_filtersMapper.checkExist(name);
    }
}
