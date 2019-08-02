package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Asset_class;

import java.util.List;


public interface Asset_classService {

    List<Asset_class> findAll();

    void saveAsset_class(Asset_class asset_class);

    Asset_class checkExist(String name);
    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Asset_class> loadAsset_class(Pager<Asset_class> pager, String param);

    Asset_class getAsset_classById(Integer id);

    void deleteAsset_class(Integer id);

    void updateAsset_class(Asset_class asset_class);

    void initPage(Pager pager);
}
