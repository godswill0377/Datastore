package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Contact_us;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface Contact_usService {

    List<Contact_us> findAll();

    void saveContact_us(Contact_us contact_us);

    /**
     * 分页查询好友列表
     * @param pager
     * @param param
     * @return
     */
    List<Contact_us> loadContact_us(Pager pager, String param);

    Contact_us getContact_usById(Integer id);

    void deleteContact_us(Integer id);

    void updateContact_us(Contact_us contact_us);

    void initPage(Pager pager);


}
