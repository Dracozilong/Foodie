package com.imooc.interceptor;

import com.imooc.controller.BaseController;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class UserTokenInterceptor extends BaseController implements HandlerInterceptor {

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 在进入controller之前 ，进行拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //获取headers中的UserId和UserToken
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        //判断userId和userToken是否为空
        if (StringUtils.isNotBlank(userId)&&StringUtils.isNotBlank(userToken)) {
            //从redis中获取userId的用户token
            String redisUserToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(redisUserToken)) {
                //System.out.println("请登陆");
                returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录"));
                return false;
            } else {
                if (!redisUserToken.equals(userToken)) {
                    //System.out.println("异地登陆，请重新登陆");
                    returnErrorResponse(response,IMOOCJSONResult.errorMsg("异地登陆，请重新登陆"));
                    return false;
                }
            }
        } else {
            //System.out.println("请登陆");
            returnErrorResponse(response,IMOOCJSONResult.errorMsg("请登录"));

            return false;
        }
        return true;
        //返回false 无法进入controller，返回true，进入拦截器
    }

    /**
     * 通过拦截器，进入controller ，但是在视图渲染之前
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 通过拦截器，进入controller，在视图渲染之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    public void returnErrorResponse(HttpServletResponse response, IMOOCJSONResult imoocjsonResult){

        OutputStream out =null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out=response.getOutputStream();
            out.write(JsonUtils.objectToJson(imoocjsonResult).getBytes("utf-8"));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
           if (out!=null){
               try {
                   out.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }
}
