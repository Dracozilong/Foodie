package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.util.UUID;

@Controller
public class BaseController {

    public static final String  FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 10;

    public static final String  REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private MyOrderService myOrderService;

    @Autowired
    private RedisOperator redisOperator;

    // 支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce

    //微信支付 -> 支付中心 -> 天天吃货平台
    //                      |-回调通知url
    //String payReturnUrl ="http://api.zqiush.com:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";

    //项目采用nginx前后端分离以后，要去除端口号，交给nginx进行转发
    //String payReturnUrl ="http://api.zqiush.com/foodie-dev-api/orders/notifyMerchantOrderPaid";

    //本地调试回调地址
     String  payReturnUrl="http://foodieshop.natapp1.cc/orders/notifyMerchantOrderPaid";


    public static final String IMG_USER_FACE_LOCATION = "D:"+File.separator+"faces";

//    public static final String IMG_USER_FACE_LOCATION = "D:\\faces";


    public IMOOCJSONResult checkUserOrders(String userId, String orderId){

        Orders order = myOrderService.queryMyorder(userId, orderId);

        if (order == null){
            IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(order);
    }

    /**
     * 用户token存入redis
     * @param users
     * @return
     */
    public UsersVO converUsersVo(Users users){
        //生成UUID存入redis
        String uniquetoken = UUID.randomUUID().toString().trim();

        UsersVO usersVO = new UsersVO();

        BeanUtils.copyProperties(users,usersVO);

        usersVO.setUserUniqueToken(uniquetoken);

        //存入redis
        redisOperator.set(REDIS_USER_TOKEN+":"+users.getId(),uniquetoken);

        return usersVO;
    }
}
