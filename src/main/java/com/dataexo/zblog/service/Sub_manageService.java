package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Sub_manage;

import java.util.List;

public interface Sub_manageService {

    List<Sub_manage> findAll();

    void initPage(Pager pager);

    List<Sub_manage> loadSubscriptions(Pager pager);

    void saveSubscription(Sub_manage sub_manage);

    List<Sub_manage> getSubscriptionByUserId(Long id);

    void updateSubscription (Sub_manage sub_manage);

    Sub_manage getSubscriptionByUserIdAndPlanId (Integer plan_id, Long user_id);

    Sub_manage getSubscriptionById (int id);

    void cancelSubscription (Sub_manage sub_manage);

    void initCancelPage (Pager pager);

    List<Sub_manage> loadCancelSubscription (Pager pager);

}
