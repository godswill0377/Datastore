package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Data_sales;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface Data_salesService {

    List<Data_sales> findAll();

    void saveData_sales(Data_sales data_sales);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Data_sales> loadData_sales(Pager pager, String param);

    Data_sales getData_salesById(Integer id);

    void deleteData_sales(Integer id);

    void updateData_sales(Data_sales data_sales);

    void initPage(Pager pager);

    Data_sales checkExist(String name);

    float getTotalPrice(Data_sales data_sales);
}
