package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Data_categoryMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Data_category> findAll();

    /**
     * 添加一个友情链接
     * @param data_category
     */
    void saveData_category(Data_category data_category);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Data_category> loadData_category(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Data_category getData_categoryById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteData_category(Integer id);

    /**
     * 更新友链
     * @param data_category
     */
    void updateData_category(Data_category data_category);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);


    /**
     *
     * @param name
     * @return
     */
    Data_category checkExist(String name);
}
