package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Plan;

import java.util.List;


public interface PlanService {

    List<Plan> findAll();

    void savePlan(Plan plan);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Plan> loadPlan(Pager pager, String param);

    Plan getPlanById(Integer id);

    void deletePlan(Integer id);

    void updatePlan(Plan plan);

    void initPage(Pager pager);

    Plan checkExist(Plan plan);

    Plan getPlanByPlanId(String planId);

    List<Plan> getPlanByVendorId(Integer vendor_id);

    List<Plan> getUnsubscribedPlanByUserId (Pager pager);

    void initUnsubscribedPage (Pager pager);

}
