package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Token;

/**
 * This class is for token management service and this will be used when the user is forgotten their password.
 * @Date 2017/6/20
 * version 1.0
 */

public interface TokenService {
    Token getByToken(String token);
    void insertToken(Token token);
}
