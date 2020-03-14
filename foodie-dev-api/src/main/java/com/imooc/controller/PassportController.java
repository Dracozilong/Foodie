package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RedisOperator redisOperator;
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
        //Users user = setNullProperty(userResult);

        //用户token存入redis
        UsersVO usersVO = converUsersVo(userResult);

        //5.信息存入cookie保存
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);


        //同步购物车数据
        synchShopcartData(userResult.getId(),request,response);
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
        //Users user = setNullProperty(userResult);

        UsersVO usersVO = converUsersVo(userResult);

        //信息存入cookie保存
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(usersVO),true);


        //同步购物车数据
        synchShopcartData(userResult.getId(),request,response);
        return  IMOOCJSONResult.ok();
    }


    @ApiOperation(value = "用户名登出",notes = "用户名登出",httpMethod ="POST" )
    @PostMapping("logout")
    public IMOOCJSONResult logout(@RequestParam String userId,HttpServletRequest request, HttpServletResponse response){

        //清除登录用户的cookie
        CookieUtils.deleteCookie(request,response,"user");

        //TODO 分布式会话中需要清除用户数据
        redisOperator.del(REDIS_USER_TOKEN+":"+userId);

        //用户退出登陆，清空购物车
        CookieUtils.deleteCookie(request,response,FOODIE_SHOPCART);




        return IMOOCJSONResult.ok();
    }


    /**
     * 设置前端不必要的信息
     * @param userResult
     * @return
     */
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

    /**
     *同步购物车数据
     * @param userId
     * @param request
     * @param response
     */
    private void synchShopcartData(String userId,HttpServletRequest request,HttpServletResponse response) {
        //redis没有数据，cookie没有数据，不做处理
        //              cookie有数据，把cookie中购物车数据同步到redis
        //redis中有数据，cookie没有数据，把redis中数据同步到redis中
        //              cookie有数据,如果cookie中的某个商品在redis中存在，则以cookie为主，删除redis中的数据，把cookie中的商品直接覆盖redis中
        //同步完redis的数据以后，覆盖cookie中的数据，保证cookie中的数据是最新的数据

        //获取redis中的购物车数据数据
        String shopcartRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        //获取cookie中的数据
        String shopcartCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartRedis)) {
            //redis中购物车数据为空
            if (StringUtils.isNotBlank(shopcartCookie)) {
                //cookie中存在购物车数据,同步数据到redis缓存
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartCookie);
            }
        } else {
                //redis中存在数据
                if (StringUtils.isNotBlank(shopcartCookie)) {
                    //cookie中存在数据
                    //1.商品id一样，用cookie中购物车的数量覆盖redis中的数量
                    //2.把cookie中的购物车数据添加到一个待删除的List中
                    //3.移除待删除的list
                    //4.合并redis和cookie中的数据
                    //5.更新到redis和cookie中
                    List<ShopcartBO> shopcartRedisList = JsonUtils.jsonToList(shopcartRedis, ShopcartBO.class);
                    List<ShopcartBO> shopcartCookieList = JsonUtils.jsonToList(shopcartCookie, ShopcartBO.class);
                    //创建一个待删除的集合
                    List<ShopcartBO> waitDeleted = new ArrayList<>();
                    for (ShopcartBO shopcartBO : shopcartRedisList) {
                        for (ShopcartBO bo : shopcartCookieList) {
                            //判断商品id是否一样
                            if (shopcartBO.getSpecId().equals(bo.getSpecId())){
                                //购物车redis集合用购物车cookie集合中的数量进行覆盖
                                shopcartBO.setBuyCounts(bo.getBuyCounts());
                                //把这个商品添加进待删除集合
                                waitDeleted.add(bo);
                            }
                        }
                    }
                    //从shopcartCookieList删除待删除的集合
                    shopcartCookieList.removeAll(waitDeleted);
                    //更新redis购物车集合中的数据
                    shopcartRedisList.addAll(shopcartCookieList);
                    //更新cookie和redis
                    CookieUtils.setCookie(request,response,FOODIE_SHOPCART,JsonUtils.objectToJson(shopcartRedisList),true);
                    redisOperator.set(FOODIE_SHOPCART+":"+userId,JsonUtils.objectToJson(shopcartRedisList));

                } else {
                    //cookie中不存在数据,redis同步数据到cookie
                    CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartRedis, true);
                }
            }
    }



}
