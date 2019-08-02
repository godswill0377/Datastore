package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_set_filters;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Region;

import java.util.List;


public interface Data_set_filtersService {

    List<Data_set_filters> findAll();

    void saveFilter(Data_set_filters filters);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Data_set_filters> loadFilter(Pager pager, String param);

    Data_set_filters getFilterById(Integer id);

    void deleteFilter(Integer id);

    void updateFilter(Data_set_filters filters);

    void initPage(Pager pager);

    Data_set_filters checkExist(String name);
}
