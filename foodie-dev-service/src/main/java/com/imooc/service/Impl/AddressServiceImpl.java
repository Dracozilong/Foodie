package com.imooc.service.Impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {

        UserAddress userAddress = new UserAddress();

        userAddress.setUserId(userId);

        List<UserAddress> addressList = userAddressMapper.select(userAddress);

        return addressList;
    }

    /**
     * 新增收货地址
     * @param addressBO
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {

        //判断用户是否有用户地址
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());

        //创建一个默认收货地址变量
        Integer isDefault =0;

        //设置全局主键
        String addressId = sid.nextShort();

        if (addressList ==null || addressList.size()==0 ||addressList.isEmpty()){
           //设定为默认收货地址
            isDefault=1;
        }

        //userAddress类赋值
        UserAddress userAddress =new UserAddress();
        BeanUtils.copyProperties(addressBO,userAddress);
        userAddress.setIsDefault(isDefault);
        userAddress.setId(addressId);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {

        String addressId = addressBO.getAddressId();

        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO,userAddress);
        userAddress.setId(addressId);
        userAddress.setUpdatedTime(new Date());
        userAddressMapper.updateByPrimaryKeySelective(userAddress);

    }


    @Override
    public void deleteUserAddress(String userId, String addressId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        userAddress.setUserId(userId);
        userAddressMapper.delete(userAddress);
    }


    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        //1.查找默认地址，设置为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNo.YES.code);
        List<UserAddress> list = userAddressMapper.select(queryAddress);
        list.stream().forEach(ua->
        {ua.setIsDefault(YesOrNo.NO.code);userAddressMapper.updateByPrimaryKeySelective(ua);});

        //2.查找不默认地址，设置为默认
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setId(addressId);
        defaultAddress.setUserId(userId);
        defaultAddress.setIsDefault(YesOrNo.YES.code);
        userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
    }


    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setId(addressId);
        UserAddress address = userAddressMapper.selectOne(userAddress);
        return  address;
    }
}
