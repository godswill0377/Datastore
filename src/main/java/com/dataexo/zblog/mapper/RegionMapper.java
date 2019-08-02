package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Region;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface RegionMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Region> findAll();

    /**
     * 添加一个友情链接
     * @param Region
     */
    void saveRegion(Region region);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Region> loadRegion(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Region getRegionById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteRegion(Integer id);

    /**
     * 更新友链
     * @param Region
     */
    void updateRegion(Region region);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Region checkExist(String name);
}
