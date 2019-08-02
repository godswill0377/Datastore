package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Withdraw;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WithdrawMapper {
    List<Withdraw> findAll();

    int initPage(Pager pager);

    List<Withdraw> loadWithdraw (Pager pager);

    void saveWithdraw (Withdraw withdraw);

    void updateWithdraw (Withdraw withdraw);

    Withdraw getById (int id);

    Double withdrawTotalByStatus(@Param("pager") Pager pager, @Param("status") int status);

}
