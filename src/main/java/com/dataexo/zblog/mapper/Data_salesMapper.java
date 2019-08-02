package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_category;
import com.dataexo.zblog.vo.Data_sales;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Data_salesMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Data_sales> findAll();

    /**
     * 添加一个友情链接
     * @param data_sales
     */
    void saveData_sales(Data_sales data_sales);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Data_sales> loadData_sales(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Data_sales getData_salesById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteData_sales(Integer id);

    /**
     * 更新友链
     * @param data_sales
     */
    void updateData_sales(Data_sales data_sales);

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
    Data_sales checkExist(String name);

    float getTotalPrice(Data_sales data_sales);
}
