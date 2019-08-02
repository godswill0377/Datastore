package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pay_log;
import com.dataexo.zblog.vo.Pager;

import java.util.List;

public interface Pay_logService {

    void savePay_log(Pay_log pay_log);

    List<Pay_log> loadPay_log(Pager pager);

    void deletePay_log(Integer id);

    void initPage(Pager pager);

    float getTotalPrice(Pager pager);
}
