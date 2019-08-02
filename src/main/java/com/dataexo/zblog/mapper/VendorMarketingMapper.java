package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Email_lists;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.auth.Email;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface VendorMarketingMapper {

    List<Email_lists> findAll();

    void saveEmail_lists(Email_lists data_type);

    List<Email_lists> loadEmail_lists(Pager pager);

    Email_lists getEmail_listsById(Integer id);

    void deleteEmail_lists(Integer id);

    void updateEmail_lists(Email_lists email_list);

    int initPage(Pager pager);

    Email_lists checkExist(String name);

    int initCouponPage (Pager pager);

    List<Email_lists> loadCouponEmail (Pager pager);
}
