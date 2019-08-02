package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.method.P;

import java.util.List;

@Mapper
public interface Trans_logMapper {

    List<Trans_log> findAll();

    int initPage(Pager pager);

    List<Trans_log> loadTrans (Pager pager);

    Trans_log getTransById(@Param("pager") Pager pager,@Param("id") int id);

    void saveTrans(Trans_log trans_log);

    Double getAmountByDateRange (Pager pager);

    Double getTotalAmount (int vendor_id);

    int getTotalSales (int vendor_id);

}
