package com.imooc.service;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVo;

import java.util.List;

public interface OrderService {

    /**
     * 创建用户订单
     * @param submitOrderBO
     */
    public OrderVo createOrder(List<ShopcartBO> shopcartBOList, SubmitOrderBO submitOrderBO);


    /**
     * 修改订单状态
     * @param orderId
     * @param OrderStatus
     */
    public  void  updateOrderStatus(String orderId,Integer orderStatus);


    /**
     * 查询orderId状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 查询未支付订单
     */
    public void closeOrder();


}
