package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Sub_manageMapper;
import com.dataexo.zblog.service.Sub_manageService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Sub_manage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class Sub_manageServiceImpl implements Sub_manageService {

    // for subscription of user
    @Resource
    private Sub_manageMapper sub_manageMapper;

    @Override
    public List<Sub_manage> findAll() {
        return sub_manageMapper.findAll();
    }

    @Override
    public void initPage(Pager pager) {
        pager.setTotalCount(sub_manageMapper.initPage(pager));
    }

    @Override
    public List<Sub_manage> loadSubscriptions(Pager pager) {
        pager.setStart(pager.getStart());
        return sub_manageMapper.loadSubscriptions(pager);
    }

    @Override
    public void saveSubscription(Sub_manage sub_manage) {
        sub_manageMapper.saveSubscription(sub_manage);
    }

    @Override
    public List<Sub_manage> getSubscriptionByUserId(Long id) {
        return sub_manageMapper.getSubscriptionByUserId(id);
    }

    @Override
    public void updateSubscription(Sub_manage sub_manage) {
        sub_manageMapper.updateSubscription(sub_manage);
    }

    @Override
    public Sub_manage getSubscriptionByUserIdAndPlanId(Integer plan_id, Long user_id) {
        return sub_manageMapper.getSubscriptionByUserIdAndPlanId(plan_id, user_id);
    }

    @Override
    public Sub_manage getSubscriptionById(int id) {
        return sub_manageMapper.getSubscriptionById(id);
    }

    @Override
    public void cancelSubscription(Sub_manage sub_manage) {
        sub_manageMapper.cancelSubscription(sub_manage);
    }

    @Override
    public void initCancelPage(Pager pager) {
        pager.setTotalCount(sub_manageMapper.initCancelPage(pager));
    }

    @Override
    public List<Sub_manage> loadCancelSubscription(Pager pager) {
        pager.setStart(pager.getStart());
        return sub_manageMapper.loadCancelSubscription(pager);
    }
}
