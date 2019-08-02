package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.VendorMarketingMapper;
import com.dataexo.zblog.service.VendorMarketingService;
import com.dataexo.zblog.vo.Email_lists;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class VendorMarketingServiceImpl implements VendorMarketingService {

    @Resource
    private VendorMarketingMapper vendorMarketingMapper;


    @Override
    public List<Email_lists> findAll() {
        return vendorMarketingMapper.findAll();
    }

    @Override
    public void saveEmail_lists(Email_lists email_list) {
        if(!email_list.getDataset_ids().equals("")){
            email_list.setDataset_ids(email_list.getDataset_ids() + ",");
        }
        if(!email_list.getEmail_address().equals("")){
            email_list.setEmail_address(email_list.getEmail_address() + ",");
        }
         vendorMarketingMapper.saveEmail_lists(email_list);
    }

    @Override
    public List<Email_lists> loadEmail_lists(Pager pager) {
        pager.setStart(pager.getStart());
        return vendorMarketingMapper.loadEmail_lists(pager);
    }

    @Override
    public Email_lists getEmail_listsById(Integer id) {
        return vendorMarketingMapper.getEmail_listsById(id);
    }

    @Override
    public void deleteEmail_lists(Integer id) {
        vendorMarketingMapper.deleteEmail_lists(id);
    }

    @Override
    public void updateEmail_lists(Email_lists email_list) {
        vendorMarketingMapper.updateEmail_lists(email_list);
    }

    @Override
    public void initPage(Pager pager) {
        int count = vendorMarketingMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Email_lists checkExist(String name){

            return vendorMarketingMapper.checkExist(name);
    }

    @Override
    public void initCouponPage(Pager pager) {
        pager.setTotalCount(vendorMarketingMapper.initCouponPage(pager));
    }

    @Override
    public List<Email_lists> loadCouponEmail(Pager pager) {
        return vendorMarketingMapper.loadCouponEmail(pager);
    }
}
