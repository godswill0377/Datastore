package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Trans_logMapper;
import com.dataexo.zblog.service.PaymentService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private Trans_logMapper trans_logMapper;

    @Override
    public List<Trans_log> loadPayment(Pager pager) {
        pager.setStart(pager.getStart());
        return trans_logMapper.loadTrans(pager);
    }

    @Override
    public void intiPage(Pager pager) {
        int count = trans_logMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Trans_log getPaymentById(Pager pager, int id) {
        return trans_logMapper.getTransById(pager, id);
    }

    @Override
    public Double getAmountByDateRange(Pager pager) {
        return trans_logMapper.getAmountByDateRange(pager) == null ? 0.00 : trans_logMapper.getAmountByDateRange(pager);
    }

    @Override
    public Double getTotalAmount(int vendor_id) {
        return trans_logMapper.getTotalAmount(vendor_id);
    }
}
