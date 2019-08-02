package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;


@Mapper
public interface Pay_logMapper {


    void savePay_log(Pay_log pay_log);

    List<Pay_log> loadPay_log(Pager pager);

    void deletePay_log(Integer id);

    int initPage(Pager pager);

    float getTotalPrice(Pager pager);
}
