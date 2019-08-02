package com.dataexo.zblog.service.impl;

import com.dataexo.zblog.mapper.UserMapper;
import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;
import com.dataexo.zblog.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void initPage(Pager pager) {
        int count = userMapper.initPage(pager);
        pager.setTotalCount(count);
    }

    @Override
    public void deleteUser(String id){
        userMapper.deleteUser(id);
    }

    @Override
    public List<User> load_userList(Pager pager, Map<String, Object> param) {
        return userMapper.load_userList(pager , param);
    }

    @Override
    public List<User> getAllUsers(Integer vendor_id) {
        return userMapper.getAllUsers(vendor_id);
    }

    @Override
    public User loadUserByAdminUsername(String username) {
        return userMapper.loadUserByAdminUsername(username);
    }

    @Override
    public User loadUserByUsername(String username) {
        return userMapper.getUser(username);
    }

    @Override
    public User loadUserByEmail(String email) {  return userMapper.getUserByEmail(email);   }

    @Override
    public User loadUserByApiKey(String apikey) {  return userMapper.getUserByApiKey(apikey);   }


    @Override
    public void insertUserInfo(User user) {  userMapper.insertUserInfo(user);   }

    @Override
    public void resetPassword(User user){
        userMapper.resetPassword(user);
    }

    @Override
    public User loadUserById(long id){
        return userMapper.getUserById(id+"");
    }

    @Override
    public User loadUserByCustomId(String customerId) {
        return userMapper.getUserByCustomerId(customerId);
    }

    @Override
    public void updatePassword(User user){
        userMapper.updatePassword(user);
    }

    @Override
    public void updateInfo(User user){
         userMapper.updateInfo(user);
    }

    @Override
    public void activeAccount(int id){
        userMapper.activeAccount(id);
    }

    @Override
    public void deactiveAccount(int id){
        userMapper.deactiveAccount(id);
    }


    @Override
    public User checkUser(User user){
        return userMapper.checkUser(user);
    }

   public void  updateCustomerID(String customer_id,long vendor_id)
   {
       userMapper.updateCustomerID(customer_id,vendor_id);
   }

    @Override
    public User loadUserByVendorId(long id) {
        return userMapper.loadUserByVendorId(id);
    }

    @Override
    public void updateBalance(int vendor_id, double balance) {
        userMapper.updateBalance(vendor_id, balance);
    }

}


