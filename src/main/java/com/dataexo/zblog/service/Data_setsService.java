package com.dataexo.zblog.service;

import com.dataexo.zblog.mapper.Data_setsMapper;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Pager;

import java.util.List;
import java.util.Map;


public interface Data_setsService {

    List<Data_sets> findAll();

    void saveData_sets(Data_sets data_sets);

    /**
     * 分页查询好友列表
     *
     * @param pager
     * @param param
     * @return
     */
    List<Data_sets> loadData_sets(Pager pager, Map<String, Object> param);

    List<Data_sets> loadData_setsByCateid(Integer cateid);

    List<String> loadAavailableDataset(Integer id);

    Data_sets getData_setsById(Integer id);

    void deleteData_sets(Integer id);

    void updateData_sets(Data_sets data_sets);

    void increaseVisitingNum(String id);

    void increaseDownloadNum(String id);

    void initPage(Pager pager);

    void initPageByVendorId(Pager pager);

    Data_sets getData_setsByCode(String codeid);

    List<Data_sets> checkExist(Data_sets data_sets);

    Data_sets getData_setsBySchema(Data_sets data_sets);

    List<Data_sets> loadData_getsByvendor_id(Pager pager);

    int getTotalCount(Pager pager);
}

