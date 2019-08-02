package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface PlanMapper {

    /**
     * 查询所以的友情链接
     * @return
     */
    List<Plan> findAll();

    /**
     * 添加一个友情链接
     * @param plan
     */
    void savePlan(Plan plan);

    /**
     * 分页查询好友列表
     * @param pager 分页条件
     * @param param
     * @return
     */
    List<Plan> loadPlan(@Param("pager") Pager pager, @Param("param") String param);
    /**
     * 通过id获取友情链接
     * @param id
     * @return
     */
    Plan getPlanById(Integer id);

    /**
     * 删除一条友链
     * @param id
     */
    void deletePlan(Integer id);

    /**
     * 更新友链
     * @param plan
     */
    void updatePlan(Plan plan);

    /**
     * 获取友链数量
     * @param pager
     * @return
     */
    int initPage(Pager pager);

    Plan checkExist(Plan plan);

    Plan getPlanByPlanId(String planId);

    List<Plan> getPlanByVendorId(Integer vendor_id);

    int initUnsubscribedPage (Pager pager);

    List<Plan> getUnsubscribedPlanByUserId (Pager pager);

}
