package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Withdraw;

import java.util.List;

public interface WithdrawService {

    List<Withdraw> findAll();

    void initPage(Pager pager);

    List<Withdraw> loadWithdraw (Pager pager);

    void saveWithdraw (Withdraw withdraw);

    void updateWithdraw (Withdraw withdraw);

    Withdraw getById (int id);

    Double withdrawTotalByStatus (Pager pager, int status);
}
