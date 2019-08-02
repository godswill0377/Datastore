package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_sets;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface Data_setsMapper {

    /**
     * 查询所以的友情链接
     * @return
     */

    List<Data_sets> getData_setsList(Pager pager);
    List<Data_sets> findAll();

    /**
     * 添加一个友情链接
     * @param Data_sets
     */
    void saveData_sets(Data_sets data_sets);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Data_sets> loadData_sets(@Param("pager") Pager pager, @Param("param") Map<String, Object> param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Data_sets getData_setsById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteData_sets(Integer id);

    /**
     * 更新友链
     * @param data_sets
     */
    void updateData_sets(Data_sets data_sets);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(@Param("pager") Pager pager);

    Data_sets getData_setsByCode(@Param("codeid") String codeid);

    List<Data_sets> checkExist(Data_sets data_sets);

    List<String> loadAavailableDataset(Integer id);

    List<Data_sets> loadData_setsByCateid(@Param("cateid") Integer cateid);

    Data_sets getData_setsBySchema(Data_sets data_sets);

    List<Data_sets>  loadData_getsByvendor_id(Pager pager);

    int initPageByVendorId(@Param("pager") Pager pager);

    void increaseVisitingNum(String id);

    void increaseDownloadNum(String id);

    int getTotalCount(Pager pager);
}
