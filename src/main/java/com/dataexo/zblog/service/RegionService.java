package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Region;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface RegionService {

    List<Region> findAll();

    void saveRegion(Region Region);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Region> loadRegion(Pager pager, String param);

    Region getRegionById(Integer id);

    void deleteRegion(Integer id);

    void updateRegion(Region region);

    void initPage(Pager pager);

    Region checkExist(String name);
}
