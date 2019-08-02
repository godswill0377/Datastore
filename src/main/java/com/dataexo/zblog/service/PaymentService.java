package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Trans_log;

import java.util.List;

public interface PaymentService {

    List<Trans_log> loadPayment(Pager pager);

    void intiPage(Pager pager);

    Trans_log getPaymentById(Pager pager , int id);

    Double getAmountByDateRange(Pager pager);

    Double getTotalAmount (int vendor_id);
}
