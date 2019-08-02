package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.FeeMapper;
import com.dataexo.zblog.service.FeeService;
import com.dataexo.zblog.vo.Fee;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {

    @Resource
    private FeeMapper feeMapper;

    @Override
    public List<Fee> findAll() {
        return feeMapper.findAll();
    }

    @Override
    public List<Fee> loadFee(Pager pager) {
        pager.setStart(pager.getStart());
        return feeMapper.loadFee(pager);
    }

    @Override
    public void intiPage(Pager pager) {
        int count = feeMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public void updateFee(Fee fee) {
        feeMapper.updateFee(fee);
    }

    @Override
    public void deleteFeeById(int id) {
        feeMapper.deleteFeeById(id);
    }

    @Override
    public Fee getFeeById(int id) {
        return feeMapper.getFeeById(id);
    }
}
