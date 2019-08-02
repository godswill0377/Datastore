package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;

import java.util.List;

public interface Trans_logService {
    List<Trans_log> findAll();

    List<Trans_log> loadTrans(Pager pager);

    void intiPage(Pager pager);

    void saveTrans(Trans_log trans_log);

    int getTotalSales(int vendor_id);
}
