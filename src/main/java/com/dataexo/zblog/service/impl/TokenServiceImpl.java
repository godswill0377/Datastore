package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.Data_typeMapper;
import com.dataexo.zblog.mapper.TokenMapper;
import com.dataexo.zblog.service.Data_typeService;
import com.dataexo.zblog.service.TokenService;
import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.Token;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    @Resource
    private TokenMapper tokenMapper;

    @Override
    public Token getByToken(String token)
    {
       return tokenMapper.getByToken(token);
    }

   @Override
    public  void insertToken(Token token)
     {
        tokenMapper.insertToken(token);
   }
}
