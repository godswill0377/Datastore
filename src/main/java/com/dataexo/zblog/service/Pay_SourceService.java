package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Pay_sources;

import java.util.List;


public interface Pay_SourceService {

    List<Pay_sources> findAll();

    void savePay_Source(Pay_sources pay_source);

    List<Pay_sources> loadPay_Source(Pager pager);

    Pay_sources getPay_SourceById(Integer id);

    void deletePay_Source(Integer id);

    void updatePay_Source(Pay_sources pay_source);
    void setDefaultCard(Pay_sources pay_source);

}
