package com.dataexo.zblog.mapper;

import com.dataexo.zblog.vo.Pager;
import com.dataexo.zblog.vo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface UserMapper {
    /**
     * 获取用户凭证
     * @param username 账号
     * @return
     */
    User getUser(@Param("username") String username);

    /**
     * 获取所有的用户
     * @return
     */
    List<User> allUser();


    User getUserByEmail(String email);

    void insertUserInfo(User user);

    void resetPassword(User user);

    User getUserById(String id);

    User getUserByCustomerId(String customerID);

    List<User> load_userList(@Param("pager") Pager pager, Map<String, Object> param);

    int initPage(@Param("pager") Pager pager);

    User loadUserByAdminUsername(String username);

    void deleteUser(String id);

    void updateInfo(User user);

    void activeAccount(int id);

    User getUserByApiKey(String apikey);

    User checkUser(User user);

    void updateCustomerID(String customer_id,long vendor_id);

    void updatePassword(User user);

    List<User> getAllUsers(@Param("vendor_id")Integer vendor_id);

    User loadUserByVendorId(long id);

    void updateBalance(@Param("vendor_id") int vendor_id, @Param("balance") double balance);

    void deactiveAccount(int id);
}
