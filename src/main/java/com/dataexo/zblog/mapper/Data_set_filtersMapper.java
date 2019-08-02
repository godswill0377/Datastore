package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_set_filters;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Region;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Data_set_filtersMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Data_set_filters> findAll();

    /**
     * 添加一个友情链接
     * @param filters
     */
    void saveFilter(Data_set_filters filters);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Data_set_filters> loadFilter(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Data_set_filters getFilterById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteFilter(Integer id);

    /**
     * 更新友链
     * @param filters
     */
    void updateFilter(Data_set_filters filters);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Data_set_filters checkExist(String name);
}
