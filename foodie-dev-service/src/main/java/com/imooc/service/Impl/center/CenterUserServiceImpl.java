package com.imooc.service.Impl.center;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.service.center.CenterUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {

        Users users = usersMapper.selectByPrimaryKey(userId);

        users.setPassword(null);

        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {

        Users users = new Users();

        BeanUtils.copyProperties(centerUserBO,users);

        users.setId(userId);

        users.setUpdatedTime(new Date());

        usersMapper.updateByPrimaryKeySelective(users);

        //更新完后 重新查询用户信息
        return queryUserInfo(userId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public Users updateUserFace(String userId,String faceUrl){

        Users user = new Users();

        user.setId(userId);

        user.setFace(faceUrl);

        user.setUpdatedTime(new Date());

        usersMapper.updateByPrimaryKeySelective(user);

        //更新完以后重新查询
        return queryUserInfo(userId);


    }
}
