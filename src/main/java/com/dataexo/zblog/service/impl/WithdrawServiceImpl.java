package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.WithdrawMapper;
import com.dataexo.zblog.service.WithdrawService;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Withdraw;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class WithdrawServiceImpl implements WithdrawService {

    @Resource
    private WithdrawMapper withdrawMapper;

    @Override
    public List<Withdraw> findAll() {
        return withdrawMapper.findAll();
    }

    @Override
    public void initPage(Pager pager) {
        pager.setTotalCount(withdrawMapper.initPage(pager));
    }

    @Override
    public List<Withdraw> loadWithdraw(Pager pager) {
        return withdrawMapper.loadWithdraw(pager);
    }

    @Override
    public void saveWithdraw(Withdraw withdraw) {
        withdrawMapper.saveWithdraw(withdraw);
    }

    @Override
    public void updateWithdraw(Withdraw withdraw) {
        withdrawMapper.updateWithdraw(withdraw);
    }

    @Override
    public Withdraw getById(int id) {
        return withdrawMapper.getById(id);
    }

    @Override
    public Double withdrawTotalByStatus(Pager pager, int status) {
        return withdrawMapper.withdrawTotalByStatus(pager, status);
    }
}
