package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_setsMapper;
import com.dataexo.zblog.service.Data_setsService;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class Data_setsServiceImpl implements Data_setsService{

    @Resource
    private Data_setsMapper data_setsMapper;


    @Override
    public List<Data_sets> findAll() {
        return data_setsMapper.findAll();
    }

    @Override
    public void saveData_sets(Data_sets Data_sets) {
        data_setsMapper.saveData_sets(Data_sets);
    }

    @Override
    public List<Data_sets> loadData_sets(Pager pager, Map<String, Object> param) {
        return data_setsMapper.loadData_sets(pager , param);
    }

    @Override
    public List<Data_sets> loadData_setsByCateid(Integer cateid ) {
        return data_setsMapper.loadData_setsByCateid(cateid);
    }

    @Override
    public List<String> loadAavailableDataset(Integer id) {
        return data_setsMapper.loadAavailableDataset(id);
    }

    @Override
    public Data_sets getData_setsById(Integer id) {
        return data_setsMapper.getData_setsById(id);
    }

    @Override
    public void deleteData_sets(Integer id) {
        data_setsMapper.deleteData_sets(id);
    }


    @Override
    public void updateData_sets(Data_sets Data_sets) {
        data_setsMapper.updateData_sets(Data_sets);
    }

    @Override
    public void increaseVisitingNum(String id) {
        data_setsMapper.increaseVisitingNum(id);
    }

    @Override
    public void increaseDownloadNum(String id) {
        data_setsMapper.increaseDownloadNum(id);
    }


    @Override
    public void initPage(Pager pager) {
        int count = data_setsMapper.initPage(pager);
        pager.setTotalCount(count);
    }


    @Override
    public void initPageByVendorId(Pager pager) {
        int count = data_setsMapper.initPageByVendorId(pager);
        pager.setTotalCount(count);
    }

    @Override
    public List<Data_sets> checkExist(Data_sets data_sets){
        return data_setsMapper.checkExist(data_sets);
    }

    @Override
    public Data_sets getData_setsByCode(String codeid){
        return data_setsMapper.getData_setsByCode(codeid);
    }

    @Override
    public Data_sets getData_setsBySchema(Data_sets data_sets){
        return data_setsMapper.getData_setsBySchema(data_sets);
    }

   public List<Data_sets> loadData_getsByvendor_id(Pager pager)
   {
       return data_setsMapper.loadData_getsByvendor_id(pager);
   }

    @Override
    public int getTotalCount(Pager pager) {
        return data_setsMapper.getTotalCount(pager);
    }


}
