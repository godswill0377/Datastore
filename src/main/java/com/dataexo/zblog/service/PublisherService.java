package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Publisher;

import java.util.List;


public interface PublisherService {


    List<Publisher> findAll();

    void savePublisher(Publisher publisher);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Publisher> loadPublisher(Pager pager, String param);

    Publisher getPublisherById(Integer id);

    void deletePublisher(Integer id);

    void updatePublisher(Publisher publisher);

    void initPage(Pager pager);

    Publisher checkExist(String name);
}
