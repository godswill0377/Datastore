package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Price_model;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Price_modelMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Price_model> findAll();

    /**
     * 添加一个友情链接
     * @param price_model
     */
    void savePrice_model(Price_model price_model);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Price_model> loadPrice_model(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Price_model getPrice_modelById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deletePrice_model(Integer id);

    /**
     * 更新友链
     * @param price_model
     */
    void updatePrice_model(Price_model price_model);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Price_model checkExist(String name);
}
