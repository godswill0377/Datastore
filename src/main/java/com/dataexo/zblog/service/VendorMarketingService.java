package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Email_lists;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.auth.Email;

import java.util.List;


public interface VendorMarketingService {

    List<Email_lists> findAll();

    void saveEmail_lists(Email_lists email_lists);

    /**
     * 分页查询好友列表
     * @param pager
     * @return
     */
    List<Email_lists> loadEmail_lists(Pager pager);

    Email_lists getEmail_listsById(Integer id);

    void deleteEmail_lists(Integer id);

    void updateEmail_lists(Email_lists email_lists);

    void initPage(Pager pager);

    Email_lists checkExist(String name);

    void initCouponPage (Pager pager);

    List<Email_lists> loadCouponEmail (Pager pager);
}
