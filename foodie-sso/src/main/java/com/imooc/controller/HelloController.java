package com.imooc.controller;


import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

//@Controller
@Controller
public class HelloController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String  REDIS_USER_TOKEN = "redis_user_token";

    public static final String  REDIS_USER_TICKET = "redis_user_ticket";

    public static final String  REDIS_TEM_TICKET = "redis_tem_ticket";

    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";



    @GetMapping("/login")
    public String login(String returnUrl, Model model,HttpServletRequest request, HttpServletResponse response) {

        model.addAttribute("returnUrl",returnUrl);

        //获取cookie中的全局会话
        String redis_user_ticket = getCookie(request, COOKIE_USER_TICKET);

        boolean isVerify = verifyUserTikcet(redis_user_ticket);

        if (isVerify){
            String temTicket = creatTemTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + temTicket;
        }

        return "login";
    }

    private boolean verifyUserTikcet(String userTicket){

        //判空
        if (StringUtils.isBlank(userTicket)){
            return false;
        }

        //根据userTicket获取用户id 以及用户会话
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)){
            return  false;
        }
        String user_token = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);

        if (StringUtils.isBlank(user_token)){
            return false;
        }
        return true;
    }

    @GetMapping("/hello")
    @ResponseBody
    public Object hello(){
        return "hello world~";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username,String password,String returnUrl,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {

        model.addAttribute("returnUrl",returnUrl);

        //判断用户名和密码是否为空
        if (StringUtils.isBlank(username) && StringUtils.isBlank(password)){
             model.addAttribute("errmsg","用户名或密码为空");
             return "login";
        }

        //查询用户
        Users users = usersService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        //判断查询出来的用户是否存在
        if (users == null){
            model.addAttribute("errmsg","用户名或者密码错误");
            return "login";
        }

        //把查询出来的用户存入redis
        //生成UUID存入redis
        String uniquetoken = UUID.randomUUID().toString().trim();

        UsersVO usersVO = new UsersVO();

        BeanUtils.copyProperties(users,usersVO);

        usersVO.setUserUniqueToken(uniquetoken);

        //存入redis
        redisOperator.set(REDIS_USER_TOKEN+":"+users.getId(), JsonUtils.objectToJson(usersVO));

        //生成用户全局票据
        String redisUserTicket = UUID.randomUUID().toString().trim();

        //全局票据存入cookie
        createCookie(COOKIE_USER_TICKET,redisUserTicket,response);

        //全局票据绑定用户id，并且存入redis
        redisOperator.set(REDIS_USER_TICKET+":"+redisUserTicket,users.getId());

        //创建临时票据
        String redisTemTicket = creatTemTicket();

        return "redirect:" + returnUrl + "?tmpTicket=" + redisTemTicket;

        //return  "login";
    }

    /**
     * 创建临时票据
      * @return
     */
      private String creatTemTicket(){

          String redisTemTicket = UUID.randomUUID().toString().trim();

          try {
              redisOperator.set(REDIS_TEM_TICKET+":"+redisTemTicket,MD5Utils.getMD5Str(redisTemTicket),600);
          } catch (Exception e) {
              e.printStackTrace();
          }
          return redisTemTicket;
      }


    /**
     * 把全局票据存入cookie
     */
    private void createCookie(String key,String val,HttpServletResponse response){

        Cookie cookie = new Cookie(key,val);

        cookie.setDomain("sso.com");
        cookie.setPath("/");

        response.addCookie(cookie);
      }


    /**
     * 删除cookie中的全局门票
     * @param key
     * @param val
     * @param response
     */
    private void deleteCookie(String key,HttpServletResponse response){

        Cookie cookie = new Cookie(key,null);

        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
//        session.removeAttribute("userInfo");
        return "ok";
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public IMOOCJSONResult verifyTmpTicket(String tmpTicket,HttpServletResponse response,HttpServletRequest request) throws Exception {

        //获取redis中的临时票据，进行判空
        String redis_temTicket = redisOperator.get(REDIS_TEM_TICKET + ":" + tmpTicket);

        if (StringUtils.isBlank(redis_temTicket)){
            return IMOOCJSONResult.errorTokenMsg("用户临时票据不存在");
        }
        //同传入进来temTicket进行比较 ，相同删除redis中的temTicket，不相同则返回错误信息
        if (redis_temTicket.equals(MD5Utils.getMD5Str(tmpTicket))){
            redisOperator.del(REDIS_TEM_TICKET+":"+tmpTicket);
        }else {
            return IMOOCJSONResult.errorTokenMsg("用户临时票据错误");
        }

        //获取cookie中的全局票据
        String cookie_user_ticket = getCookie(request, COOKIE_USER_TICKET);

        if (StringUtils.isBlank(cookie_user_ticket)){
            return IMOOCJSONResult.errorMsg("用户票据异常");
        }

        //获取redis中的user_ticket
        String user_id = redisOperator.get(REDIS_USER_TICKET + ":" + cookie_user_ticket);

        if (StringUtils.isBlank(user_id)){
            return IMOOCJSONResult.errorMsg("用户票据异常");
        }

        //根据redis_user_ticket的值获取对应的用户会话
        String user_token = redisOperator.get(REDIS_USER_TOKEN + ":" + user_id);

        return IMOOCJSONResult.ok(JsonUtils.jsonToPojo(user_token,UsersVO.class));
    }


    /**
     * 获取cookie
     * @param request
     * @param cookieName
     * @return
     */
    public String getCookie(HttpServletRequest request ,String cookieName){

        //获取cookie
        Cookie[] cookies = request.getCookies();
        //判空
        String cookieValue = null;
        if (cookies ==null || StringUtils.isBlank(cookieName)){
            return null;
        }else {
            //循环cookie集合 获取对应cookieName的cookie值
            for (int i= 0;i<cookies.length;i++){
              if (cookieName.equals(cookies[i].getName())){
                  cookieValue=cookies[i].getValue();
              }
            }
        }
        return  cookieValue;
    }

    @PostMapping("/logout")
    @ResponseBody
    public IMOOCJSONResult logout(String userId,HttpServletRequest request,HttpServletResponse response){

        //获取cas中全局门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        //删除redis和cookie中的全局门票
        deleteCookie(COOKIE_USER_TICKET,response);
        redisOperator.del(REDIS_USER_TICKET+":"+userTicket);

        //删除用户全局会话
        redisOperator.del(REDIS_USER_TOKEN+":"+userId) ;

        return IMOOCJSONResult.ok();


    }
}
