package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVo;
import com.imooc.pojo.vo.OrderVo;
import com.imooc.service.OrderService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static com.imooc.controller.BaseController.FOODIE_SHOPCART;

@Api(value = "订单相关",tags = {"订单相关api接口"})
@RestController
@RequestMapping("orders")
public class OrdersController  extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户创建订单",notes = "用户创建订单",httpMethod = "POST")
   @PostMapping("create")
   public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO, HttpServletRequest request, HttpServletResponse response){

        //选择支付方式
        if (submitOrderBO.getPayMethod()!= PayMethod.WEIXIN.type && submitOrderBO.getPayMethod()!=PayMethod.ALIPAY.type){
            return IMOOCJSONResult.errorMsg("不支持该支付方式");
        }

        //从redis获取购物车
        String shopcartStr = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        //判断购物车中是否存在数据
        if (StringUtils.isBlank(shopcartStr)){
            return IMOOCJSONResult.errorMsg("购物车数据存在问题");
        }
        List<ShopcartBO> shopcartBOList = JsonUtils.jsonToList(shopcartStr, ShopcartBO.class);

        //1.创建订单
        OrderVo orderVo = orderService.createOrder(shopcartBOList,submitOrderBO);
        String orderId = orderVo.getOrderId();

        //2.创建订单以后，移除购物车中已结算(已提交)的商品

        //TODO 整合Redis之后，完善购物车中的已结算商品清算，并且同步到前端的cookie
    //    CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"");


        //3.像支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVo merchantOrdersVo = orderVo.getMerchantOrdersVo();

        merchantOrdersVo.setReturnUrl(payReturnUrl);

        //设置账单金额1分钱
        merchantOrdersVo.setAmount(1);

        //创建Headers
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        //headers赋值
        httpHeaders.add("imoocUserId","7420371-511458550");

        httpHeaders.add("password","laow-03or-4lm5-4ok2");

        //创建httpEntity
        HttpEntity<MerchantOrdersVo> httpEntity = new HttpEntity<MerchantOrdersVo>(merchantOrdersVo,httpHeaders);

        ResponseEntity<IMOOCJSONResult> resultResponseEntity = restTemplate.postForEntity(paymentUrl, httpEntity, IMOOCJSONResult.class);


        //获得调用支付中心结果
        IMOOCJSONResult imoocjsonResult = resultResponseEntity.getBody();

        if (imoocjsonResult.getStatus()!=200){
            return IMOOCJSONResult.errorMsg("创建订单失败，请联系管理员");
        }


        return IMOOCJSONResult.ok(orderId);
   }

   @ApiOperation(value = "修改订单状态",notes = "修改订单状态",httpMethod = "POST")
   @PostMapping("notifyMerchantOrderPaid")
   public Integer notifyMerchantOrderPaid(@RequestParam String merchantOrderId){
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
         return HttpStatus.OK.value();
   }

   @ApiOperation(value = "查询订单状态",notes = "查询订单状态",httpMethod = "POST")
   @PostMapping("getPaidOrderInfo")
   public IMOOCJSONResult getPaidOrderInfo(@RequestParam String orderId){

       OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);

       return IMOOCJSONResult.ok(orderStatus);
   }
}
