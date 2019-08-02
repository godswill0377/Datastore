package com.dataexo.zblog.service;

import com.dataexo.zblog.vo.Inbox_Notify;
import com.dataexo.zblog.vo.Pager;

import java.util.List;


public interface InboxNotifyService {

    List<Inbox_Notify> findAll();

    void saveInbox_Notify(Inbox_Notify inbox_Notify);

    /**
     * 分页查询好友列表
     * @param pager
     * @return
     */
    List<Inbox_Notify> loadInbox_Notify(Pager pager);

    Inbox_Notify getInbox_NotifyById(Integer id);

    void deleteInbox_Notify(Integer id);

    void updateInbox_Notify(Inbox_Notify inbox_Notify);

    void initPage(Pager pager);

    Inbox_Notify checkExist(String name);

    void eraseReadFlag(long id);
    int getUnreadNums(Pager pager);
}
