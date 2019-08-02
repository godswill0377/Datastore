package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.PublisherMapper;
import com.dataexo.zblog.service.PublisherService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class PublisherServiceImpl implements PublisherService {

    @Resource
    private PublisherMapper publisherMapper;

    @Override
    public List<Publisher> findAll() {
        return publisherMapper.findAll();
    }

    @Override
    public void savePublisher(Publisher Publisher) {
        publisherMapper.savePublisher(Publisher);
    }

    @Override
    public List<Publisher> loadPublisher(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return publisherMapper.loadPublisher(pager,param);
    }

    @Override
    public Publisher getPublisherById(Integer id) {
        return publisherMapper.getPublisherById(id);
    }

    @Override
    public void deletePublisher(Integer id) {
        publisherMapper.deletePublisher(id);
    }

    @Override
    public void updatePublisher(Publisher Publisher) {
        publisherMapper.updatePublisher(Publisher);
    }

    @Override
    public void initPage(Pager pager) {
        int count = publisherMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Publisher checkExist(String name){
        return publisherMapper.checkExist(name);
    }
}
