package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.PagerMapper;
import com.dataexo.zblog.service.PagerService;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;



@Service
public class PagerServiceImpl implements PagerService {

    @Resource
    private PagerMapper pagerMapper;
    @Override
    public void initPage(Pager pager) {

    }

    @Override
    public void loadCategoryPager(Pager pager,Integer categoryId) {
        int count = pagerMapper.loadCategoryPager(categoryId);
        pager.setTotalCount(count);
    }

    @Override
    public void loadTagPager(Pager pager, Integer tagId) {
        int count = pagerMapper.loadTagPager(tagId);
        pager.setTotalCount(count);
    }
}
