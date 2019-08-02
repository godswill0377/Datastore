package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Fee;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FeeMapper {

    List<Fee> findAll();

    int initPage(Pager pager);

    List<Fee> loadFee (Pager pager);

    Fee getFeeById(int id);

    void updateFee(Fee fee);

    void deleteFeeById(int id);

}
