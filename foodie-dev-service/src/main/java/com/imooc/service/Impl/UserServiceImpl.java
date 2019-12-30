package com.imooc.service.Impl;

import com.imooc.enums.Sex;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UsersService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUserIsExist(String username) {
        //实例化
        Example example = new Example(Users.class);
        //拼接查询条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        //usermapper进行查询
        Users result = usersMapper.selectOneByExample(example);
        return result == null ? false:true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users insertUser(UserBO userBO)  {

        //创建新用户
        Users users =new Users();

        //设置用户ID
        users.setId(sid.nextShort());

        //设置用户名
        users.setUsername(userBO.getUsername());

        //采用MD5的方式设置密码
        try {
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //设置昵称
        users.setNickname(userBO.getUsername());

        //设置性别
        users.setSex(Sex.man.code);

        //设置出生日期
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));

        //设置创建时间
        users.setCreatedTime(new Date());

        //设置更新时间
        users.setUpdatedTime(new Date());

        //设置头像
        users.setFace(USER_FACE);

        usersMapper.insert(users);

        return users;
    }

    /**
     * 查询用户用作登录
     * @param username
     * @param password
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin( String username,  String password) {

        //实例化
        Example example =new Example(Users.class);

        //拼接查询条件
        Example.Criteria criteria = example.createCriteria();

        //and username=""
        criteria.andEqualTo("username",username);

        //and password=''
        criteria.andEqualTo("password",password);

        Users user = usersMapper.selectOneByExample(example);

        return user;

    }
}
