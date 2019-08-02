package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Publisher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface PublisherMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Publisher> findAll();

    /**
     * 添加一个友情链接
     * @param publisher
     */
    void savePublisher(Publisher publisher);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Publisher> loadPublisher(@Param("pager") Pager pager, @Param("param") String param);

    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Publisher getPublisherById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deletePublisher(Integer id);

    /**
     * 更新友链
     * @param publisher
     */
    void updatePublisher(Publisher publisher);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Publisher checkExist(String name);
}
