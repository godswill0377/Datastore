package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Sub_manage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Sub_manageMapper {

    List<Sub_manage> findAll();

    int initPage(Pager pager);

    List<Sub_manage> loadSubscriptions (Pager pager);

    Sub_manage getSubscriptionById (int id);

    List<Sub_manage> getSubscriptionByUserId (Long id);

    Sub_manage getSubscriptionByUserIdAndPlanId (@Param("plan_id") Integer plan_id, @Param("user_id") Long user_id);

    void saveSubscription (Sub_manage sub_manage);

    void updateSubscription (Sub_manage sub_manage);

    void cancelSubscription (Sub_manage sub_manage);

    int initCancelPage (Pager pager);

    List<Sub_manage> loadCancelSubscription (Pager pager);

}
