package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Pay_sources;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface Pay_SourceMapper {


    List<Pay_sources> findAll();

    void savePay_Source(Pay_sources pay_source);

    List<Pay_sources> loadPay_Source(Pager pager);

    Pay_sources getPay_SourceById(Integer id);

    void deletePay_Source(Integer id);

    void updatePay_Source(Pay_sources pay_source);

    void setDefaultCard(Pay_sources pay_source);
}
