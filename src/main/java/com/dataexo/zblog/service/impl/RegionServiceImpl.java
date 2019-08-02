package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.RegionMapper;
import com.dataexo.zblog.service.RegionService;
import com.dataexo.zblog.vo.Region;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class RegionServiceImpl implements RegionService{

    @Resource
    private RegionMapper regionMapper;

    @Override
    public List<Region> findAll() {
        return regionMapper.findAll();
    }

    @Override
    public void saveRegion(Region Region) {
        regionMapper.saveRegion(Region);
    }

    @Override
    public List<Region> loadRegion(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return regionMapper.loadRegion(pager,param);
    }

    @Override
    public Region getRegionById(Integer id) {
        return regionMapper.getRegionById(id);
    }

    @Override
    public void deleteRegion(Integer id) {
        regionMapper.deleteRegion(id);
    }

    @Override
    public void updateRegion(Region Region) {
        regionMapper.updateRegion(Region);
    }

    @Override
    public void initPage(Pager pager) {
        int count = regionMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Region checkExist(String name){
          return  regionMapper.checkExist(name);
    }
}
