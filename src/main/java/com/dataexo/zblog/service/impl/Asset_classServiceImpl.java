package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Asset_classMapper;
import com.dataexo.zblog.service.Asset_classService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Asset_class;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Asset_classServiceImpl implements Asset_classService{

    @Resource
    private Asset_classMapper asset_classMapper;


    @Override
    public List<Asset_class> findAll() {
        return asset_classMapper.findAll();
    }

    @Override
    public void saveAsset_class(Asset_class asset_class) {
        asset_classMapper.saveAsset_class(asset_class);
    }

    @Override
    public List<Asset_class> loadAsset_class(Pager<Asset_class> pager, String param) {
        pager.setStart(pager.getStart());
        return asset_classMapper.loadAsset_class(pager,param);
    }

    @Override
    public Asset_class checkExist(String name){
        return asset_classMapper.checkExist(name);
    }

    @Override
    public Asset_class getAsset_classById(Integer id) {
        return asset_classMapper.getAsset_classById(id);
    }

    @Override
    public void deleteAsset_class(Integer id) {
        asset_classMapper.deleteAsset_class(id);
    }


    @Override
    public void updateAsset_class(Asset_class asset_class) {
        asset_classMapper.updateAsset_class(asset_class);
    }


    @Override
    public void initPage(Pager pager) {
        int count = asset_classMapper.initPage(pager);
        pager.setTotalCount(count);
    }

}
