package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Asset_class;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Asset_classMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Asset_class> findAll();

    /**
     * 添加一个友情链接
     * @param asset_class
     */
    void saveAsset_class(Asset_class asset_class);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Asset_class> loadAsset_class(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Asset_class getAsset_classById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteAsset_class(Integer id);

    /**
     * 更新友链
     * @param asset_class
     */
    void updateAsset_class(Asset_class asset_class);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Asset_class checkExist(String name);
}
