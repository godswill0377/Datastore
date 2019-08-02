package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Contact_us;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface Contact_usMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Contact_us> findAll();

    /**
     * 添加一个友情链接
     * @param contact_us
     */
    void saveContact_us(Contact_us contact_us);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Contact_us> loadContact_us(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Contact_us getContact_usById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deleteContact_us(Integer id);

    /**
     * 更新友链
     * @param contact_us
     */
    void updateContact_us(Contact_us contact_us);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);
}
