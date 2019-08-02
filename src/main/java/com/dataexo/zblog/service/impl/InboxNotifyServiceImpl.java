package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.InboxNotifyMapper;
import com.dataexo.zblog.service.InboxNotifyService;
import com.dataexo.zblog.service.InboxNotifyService;
import com.dataexo.zblog.vo.Inbox_Notify;
import com.dataexo.zblog.vo.Pager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class InboxNotifyServiceImpl implements InboxNotifyService{

    @Resource
    private InboxNotifyMapper inbox_NotifyMapper;


    @Override
    public List<Inbox_Notify> findAll() {
        return inbox_NotifyMapper.findAll();
    }


    @Override
    public void saveInbox_Notify(Inbox_Notify inbox_Notify) {
        inbox_NotifyMapper.saveInbox_Notify(inbox_Notify);
    }

    @Override
    public List<Inbox_Notify> loadInbox_Notify(Pager pager) {
        pager.setStart(pager.getStart());
        return inbox_NotifyMapper.loadInbox_Notify(pager);
    }

    @Override
    public Inbox_Notify getInbox_NotifyById(Integer id) {
        return inbox_NotifyMapper.getInbox_NotifyById(id);
    }

    @Override
    public void deleteInbox_Notify(Integer id) {
        inbox_NotifyMapper.deleteInbox_Notify(id);
    }

    @Override
    public void updateInbox_Notify(Inbox_Notify inbox_Notify) {
        inbox_NotifyMapper.updateInbox_Notify(inbox_Notify);
    }

    @Override
    public void eraseReadFlag(long id) {
        inbox_NotifyMapper.eraseReadFlag(id);
    }


    @Override
    public void initPage(Pager pager) {
        int count = inbox_NotifyMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public Inbox_Notify checkExist(String name){

            return inbox_NotifyMapper.checkExist(name);
    }

    @Override
    public int getUnreadNums(Pager pager) {
        return inbox_NotifyMapper.getUnreadNums(pager);
    }

}
