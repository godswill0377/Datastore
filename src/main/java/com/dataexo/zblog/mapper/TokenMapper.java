package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Token;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface TokenMapper {


    Token getByToken(String token);

    void insertToken(Token token);
}
