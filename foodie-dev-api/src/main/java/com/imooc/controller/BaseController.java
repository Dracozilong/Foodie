package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.IMOOCJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {

    public static final String  FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 10;

    @Autowired
    private MyOrderService myOrderService;

    // 支付中心的调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";		// produce

    //微信支付 -> 支付中心 -> 天天吃货平台
    //                      |-回调通知url
    String payReturnUrl ="http://foodieshop.natapp1.cc/orders/notifyMerchantOrderPaid";

    public static final String IMG_USER_FACE_LOCATION = "D:"+File.separator+"faces";

//    public static final String IMG_USER_FACE_LOCATION = "D:\\faces";


    public IMOOCJSONResult checkUserOrders(String userId, String orderId){

        Orders order = myOrderService.queryMyorder(userId, orderId);

        if (order == null){
            IMOOCJSONResult.errorMsg("订单不存在");
        }
        return IMOOCJSONResult.ok(order);
    }
}
