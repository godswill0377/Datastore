package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_chart;
import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Data_sets_series;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface Data_sets_seriesMapper {

    /**
     * 查询所以的友情链接
     * @return
     */

    List<Data_sets_series> getData_sets_seriesList(Pager pager);
    List<Data_sets_series> findAll();

    /**
     * 添加一个友情链接
     * @param Data_sets_series
     */
    void saveData_sets_series(Data_sets_series data_sets_series);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Data_sets_series> loadData_sets_series(@Param("pager") Pager pager, @Param("param") Map<String, Object> param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Data_sets_series getData_sets_seriesById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteData_sets_series(Integer id);

    /**
     * 更新友链
     * @param Data_sets_series
     */
    void updateData_sets_series(Data_sets_series data_sets_series);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(@Param("pager") Pager pager);

    int getData_sets_count(Integer id);

    void updateChart_Diagram(Data_chart chart);

    void deleteDataByCode(String code);

    List<Data_sets_series> checkExist(Data_sets_series data_sets_series);

    List<Data_sets_series> loadThree_sets_series(Pager<Data_sets> pager);

    void deleteData_sets_seriesByParent(Integer id);

}
