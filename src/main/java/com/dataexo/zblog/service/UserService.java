package com.dataexo.zblog.service;


import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;

import java.util.List;
import java.util.Map;


public interface UserService {


    User loadUserByUsername(String username);
    User loadUserByApiKey(String apikey);
    User loadUserByAdminUsername(String username);
    User loadUserByEmail(String email);

    void resetPassword(User user);
    void insertUserInfo(User user);

    User loadUserById(long id);
    User loadUserByCustomId(String customerId);

    List<User> load_userList(Pager pager, Map<String, Object> param);

    List<User> getAllUsers(Integer vendor_id);


    void initPage(Pager pager);

    void deleteUser(String id);
    void updateInfo(User user);

    void updatePassword(User user);

    void activeAccount(int id);
    void deactiveAccount(int id);


    User checkUser(User user);

    void updateCustomerID(String customer_id,long vendor_id);

    User loadUserByVendorId(long id);

    void updateBalance(int vendor_id, double balance);
}
