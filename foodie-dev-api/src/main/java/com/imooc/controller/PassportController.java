package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UsersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UsersService usersService;

    /**
     * 查询用户
     * @param username
     * @return
     */
    @ApiOperation(value = "用户名是否存在",notes = "用户名是否存在",httpMethod ="GET" )
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){
       //1.判断username是否为空
        if (StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }
        //2.查找注册的用户名是否存在
        boolean flag = usersService.queryUserIsExist(username);
        if (flag){
            return IMOOCJSONResult.errorMsg("用户名已经存在");
        }
        //3.请求成功，用户名没有重复
        return IMOOCJSONResult.ok();
    }

    /**
     * 用户注册
     * @param userBO
     * @return
     */
    @ApiOperation(value = "用户名注册",notes = "用户名注册",httpMethod ="POST" )
    @PostMapping("/register")
    public IMOOCJSONResult register(@RequestBody UserBO userBO,HttpServletRequest request, HttpServletResponse response){

        //获取用户名
        String username =userBO.getUsername();

        //获取密码
        String password = userBO.getPassword();

        //获取二次输入的密码
        String confirmPassword = userBO.getConfirmPassword();

        //0.判断用户名和密码是否为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)){
            return IMOOCJSONResult.errorMsg("用户名或者密码为空");
        }
        //1.密码的位数不小于6位
         if(password.length()<6){
             return IMOOCJSONResult.errorMsg("密码长度小于6位");
         }
        //2.两次输入的密码是否一致
         if (password.equals(confirmPassword)==false){
             return IMOOCJSONResult.errorMsg("两次密码输入不一致");
         }
        //3.注册
        Users userResult = usersService.insertUser(userBO);

        //4.设置前端必要信息
        Users user = setNullProperty(userResult);

        //5.信息存入cookie保存
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(user),true);

        //TODO  生成用户token存入redis存入redis会话
        //TODO  同步购物车数据

        return IMOOCJSONResult.ok();
    }

    /**
     * 用户登录
     * @param userBO
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户名登录",notes = "用户名登录",httpMethod ="POST" )
    @PostMapping("login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //获取用户名
        String username =userBO.getUsername();

        //获取密码
        String password = userBO.getPassword();

        //0.判断用户名和密码是否为空
        if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return IMOOCJSONResult.errorMsg("用户名或者密码为空");
        }

        //登录
         Users userResult = usersService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        //查询出来的用户为空
        if (userResult == null){
            return IMOOCJSONResult.errorMsg("用户名或者密码不正确");
        }

        //设置前端必要信息
        Users user = setNullProperty(userResult);

        //信息存入cookie保存
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(user),true);

        //TODO  生成用户token存入redis存入redis会话
        //TODO  同步购物车数据
        return  IMOOCJSONResult.ok(user);
    }


    @ApiOperation(value = "用户名登出",notes = "用户名登出",httpMethod ="POST" )
    @PostMapping("logout")
    public IMOOCJSONResult logout(@RequestParam String userId,HttpServletRequest request, HttpServletResponse response){

        //清除登录用户的cookie
        CookieUtils.deleteCookie(request,response,"user");

        //TODO 用户退出登录，需要清除购物车

        //TODO 分布式会话中需要清除用户数据


        return IMOOCJSONResult.ok();
    }


    private Users setNullProperty(Users userResult){

        //设置密码为空
        userResult.setPassword(null);

        //设置邮箱为空
        userResult.setEmail(null);

        //设置手机号为空
        userResult.setMobile(null);

        //设置生日为空
        userResult.setBirthday(null);

        //设置更新时间
        userResult.setUpdatedTime(null);

        //设置创建时间
        userResult.setCreatedTime(null);

        return userResult;
    }

}
