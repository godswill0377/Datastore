package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface Data_categoryService {

    List<Data_category> findAll();

    void saveData_category(Data_category data_category);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Data_category> loadData_category(Pager pager, String param);

    Data_category getData_categoryById(Integer id);

    void deleteData_category(Integer id);

    void updateData_category(Data_category data_category);

    void initPage(Pager pager);

    Data_category checkExist(String name);
}
