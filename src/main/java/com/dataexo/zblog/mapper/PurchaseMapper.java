package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Purchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface PurchaseMapper {


    void insertPurchase(Purchase purchase);

    void deletePurchase(Purchase purchase);

    Purchase checkExist(Purchase purchase);

    Purchase selectPurchase(Purchase purchase);

    int initPage(Pager pager);

    List<Purchase> loadPurchase(Pager pager);
}
