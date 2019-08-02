package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Fee;
import com.dataexo.zblog.vo.Pager;

import java.util.List;

public interface FeeService {
    List<Fee> findAll();

    List<Fee> loadFee(Pager pager);

    void intiPage(Pager pager);

    Fee getFeeById(int id);

    void updateFee(Fee fee);

    void deleteFeeById(int id);
}
