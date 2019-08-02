package com.dataexo.zblog.service;


import com.dataexo.zblog.vo.User;

public interface ThirdPartyService {
    boolean sendAwsSes(String from , String to , String subject , String body);
    boolean addNewsToSystem(User user);
}
