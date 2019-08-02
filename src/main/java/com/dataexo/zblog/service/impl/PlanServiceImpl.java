package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.PlanMapper;
import com.dataexo.zblog.mapper.PublisherMapper;
import com.dataexo.zblog.service.PlanService;
import com.dataexo.zblog.service.PublisherService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Plan;
import com.dataexo.zblog.vo.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    @Resource
    private PlanMapper planMapper;

    @Override
    public List<Plan> findAll() {
        return planMapper.findAll();
    }

    @Override
    public void savePlan(Plan plan) {
        planMapper.savePlan(plan);
    }

    @Override
    public List<Plan> loadPlan(Pager pager, String param) {
        pager.setStart(pager.getStart());
        return planMapper.loadPlan(pager,param);
    }

    @Override
    public Plan getPlanById(Integer id) {
        return planMapper.getPlanById(id);
    }

    @Override
    public void deletePlan(Integer id) {
        planMapper.deletePlan(id);
    }

    @Override
    public void updatePlan(Plan plan) {
        planMapper.updatePlan(plan);
    }

    @Override
    public void initPage(Pager pager) {
        int count = planMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Plan checkExist(Plan plan){
        return planMapper.checkExist(plan);
    }

    @Override
    public Plan getPlanByPlanId(String planId){
        return planMapper.getPlanByPlanId(planId);
    }

    @Override
    public List<Plan> getPlanByVendorId(Integer vendor_id) {
        return planMapper.getPlanByVendorId(vendor_id);
    }

    @Override
    public List<Plan> getUnsubscribedPlanByUserId(Pager pager) {
        pager.setStart(pager.getStart());
        return planMapper.getUnsubscribedPlanByUserId(pager);
    }

    @Override
    public void initUnsubscribedPage(Pager pager) {
        int count = planMapper.initUnsubscribedPage(pager);
        pager.setTotalCount(count);
    }

}
