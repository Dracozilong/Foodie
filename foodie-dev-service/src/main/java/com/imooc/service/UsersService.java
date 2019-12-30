package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;

public interface UsersService {

    /**
     * 根据传入的username查询用户
     * @param username
     * @return
     */
    public boolean queryUserIsExist(String username);

    /**
     * 添加用户
     * @param userBO
     * @return
     */
    public Users insertUser(UserBO userBO) ;

    /**
     * 查询用户登录
     * @param name
     * @param password
     * @return
     */
    public Users queryUserForLogin(String name,String password);
}
