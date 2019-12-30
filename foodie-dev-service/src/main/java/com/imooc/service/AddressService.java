package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {


    /**
     * 根据用户Id查询用户收获地址
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 新增收货地址
     * @param addressBO
     */
    public void addNewUserAddress(AddressBO addressBO);

    /**
     * 修改用户地址
     * @param addressBO
     */
    public void updateUserAddress(AddressBO addressBO);

    /**
     * 根据用userId和addressId 删除收货地址
     * @param userId
     * @param addressId
     */
    public void deleteUserAddress(String userId,String addressId);

    /**
     * 设置默认地址
     * @param userId
     * @param addressId
     */
    public void updateUserAddressToBeDefault(String userId,String addressId);

    /**
     *查询用户地址
     * @param userId
     * @param addressId
     * @return
     */
    public UserAddress queryUserAddress(String userId,String addressId);


}
