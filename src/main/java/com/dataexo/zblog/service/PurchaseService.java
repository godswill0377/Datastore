package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Purchase;

import java.util.List;


public interface PurchaseService {

    void insertPurchase(Purchase purchase);
    Purchase checkExist(Purchase purchase);
    void deletePurchase(Purchase purchase);
    Purchase selectPurchase(Purchase purchase);
    List<Purchase> loadPurchase(Pager pager);

    void initPage(Pager pager);
}
