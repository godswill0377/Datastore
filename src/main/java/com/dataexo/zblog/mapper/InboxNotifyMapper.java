package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Data_type;
import com.dataexo.zblog.vo.Inbox_Notify;
import com.dataexo.zblog.vo.Pager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface InboxNotifyMapper {

    List<Inbox_Notify> findAll();

    void saveInbox_Notify(Inbox_Notify inbox_notify);

    List<Inbox_Notify> loadInbox_Notify(Pager pager);

    Inbox_Notify getInbox_NotifyById(Integer id);

    void deleteInbox_Notify(Integer id);

    void updateInbox_Notify(Inbox_Notify inbox_notify);

    int initPage(Pager pager);

    Inbox_Notify checkExist(String name);

    void eraseReadFlag(long id);

    int getUnreadNums(Pager pager);
}
