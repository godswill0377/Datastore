package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface Data_typeService {

    List<Data_type> findAll();

    void saveData_type(Data_type data_type);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Data_type> loadData_type(Pager pager, String param);

    Data_type getData_typeById(Integer id);

    void deleteData_type(Integer id);

    void updateData_type(Data_type data_type);

    void initPage(Pager pager);

    Data_type checkExist(String name);
}
