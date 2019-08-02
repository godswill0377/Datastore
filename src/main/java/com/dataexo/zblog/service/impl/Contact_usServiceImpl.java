package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Contact_usMapper;
import com.dataexo.zblog.service.Contact_usService;
import com.dataexo.zblog.vo.Contact_us;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class Contact_usServiceImpl implements Contact_usService {

    @Resource
    private Contact_usMapper contact_usMapper;

    @Override
    public List<Contact_us> findAll() {
        return contact_usMapper.findAll();
    }

    @Override
    public void saveContact_us(Contact_us contact_us) {
        contact_usMapper.saveContact_us(contact_us);
    }

    @Override
    public List<Contact_us> loadContact_us(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return contact_usMapper.loadContact_us(pager,param);
    }

    @Override
    public Contact_us getContact_usById(Integer id) {
        return contact_usMapper.getContact_usById(id);
    }

    @Override
    public void deleteContact_us(Integer id) {
        contact_usMapper.deleteContact_us(id);
    }

    @Override
    public void updateContact_us(Contact_us contact_us) {
        contact_usMapper.updateContact_us(contact_us);
    }

    @Override
    public void initPage(Pager pager) {
        int count = contact_usMapper.initPage(pager);
        pager.setTotalCount(count);
    }

}
