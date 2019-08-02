package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Trans_logMapper;
import com.dataexo.zblog.service.Trans_logService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class Trans_logServiceImpl implements Trans_logService {
    @Resource
    private Trans_logMapper trans_logMapper;

    @Override
    public List<Trans_log> findAll() {
        return trans_logMapper.findAll();
    }

    @Override
    public List<Trans_log> loadTrans(Pager pager) {
        pager.setStart(pager.getStart());
        return trans_logMapper.loadTrans(pager);
    }

    @Override
    public void intiPage(Pager pager) {
        int count = trans_logMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public void saveTrans(Trans_log trans_log) {
        trans_logMapper.saveTrans(trans_log);
    }

    @Override
    public int getTotalSales(int vendor_id) {
        return trans_logMapper.getTotalSales(vendor_id);
    }
}
