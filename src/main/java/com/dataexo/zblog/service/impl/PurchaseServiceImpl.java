package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.PurchaseMapper;
import com.dataexo.zblog.service.PurchaseService;
import com.dataexo.zblog.vo.Purchase;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService{

    @Resource
    private PurchaseMapper purchaseMapper;

    @Override
    public Purchase selectPurchase(Purchase purchase){
       return purchaseMapper.selectPurchase(purchase);
    }

    @Override
    public void insertPurchase(Purchase purchase){
        purchaseMapper.insertPurchase(purchase);
    }

    @Override
    public Purchase checkExist(Purchase purchase){
        return purchaseMapper.checkExist(purchase);
    }

    @Override
    public void deletePurchase(Purchase purchase){
         purchaseMapper.deletePurchase(purchase);
    }

    @Override
    public void initPage(Pager pager){
        int count =  purchaseMapper.initPage( pager);
        pager.setTotalPageNum(count / pager.getLimit() + 1);
        pager.setTotalCount(count);

    }

    @Override
    public List<Purchase> loadPurchase (Pager pager) {
        return  purchaseMapper.loadPurchase(pager);
    }

}
