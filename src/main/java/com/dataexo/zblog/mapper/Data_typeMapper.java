package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Data_typeMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Data_type> findAll();

    /**
     * 添加一个友情链接
     * @param data_type
     */
    void saveData_type(Data_type data_type);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Data_type> loadData_type(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Data_type getData_typeById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteData_type(Integer id);

    /**
     * 更新友链
     * @param data_type
     */
    void updateData_type(Data_type data_type);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Data_type checkExist(String name);
}
