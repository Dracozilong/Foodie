package com.imooc.controller;


import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UsersService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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

        return "login";
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
        createCookie(REDIS_USER_TICKET,redisUserTicket,response);

        //全局票据绑定用户id，并且存入redis
        redisOperator.set(REDIS_USER_TICKET+":"+redisUserTicket,users.getId());

        //创建临时票据
        String redisTemTicket = creatTemTicket();

        //return "redirect"+returnUrl+"?temTicket="+redisTemTicket;

        return  "login";
    }

    /**
     * 创建临时票据
      * @return
     */
      private String creatTemTicket(){

          String redisTemTicket = UUID.randomUUID().toString().trim();

          try {
              redisOperator.set(REDIS_TEM_TICKET+":",MD5Utils.getMD5Str(redisTemTicket),600);
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

    @GetMapping("/setSession")
    public Object setSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "new user");
        session.setMaxInactiveInterval(3600);
        session.getAttribute("userInfo");
//        session.removeAttribute("userInfo");
        return "ok";
    }


}
