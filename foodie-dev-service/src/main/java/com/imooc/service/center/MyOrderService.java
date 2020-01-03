package com.imooc.service.center;

import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.OrderStatusCountsVo;
import com.imooc.utils.PagedGridResult;

public interface MyOrderService {

    /**
     * 查询用户自己的订单
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId,Integer orderStatus,Integer page,Integer pageSize);


    /**
     * 根据订单Id 修改订单发货状态
     * @param orderId
     */
    public void updateDeliverOrdersStatus(String orderId);


    /**
     * 查询我的订单
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyorder(String userId,String orderId);

    /**
     * 更新订单状态 确认收货
     * @param orderId
     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单(逻辑删除)
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String userId,String orderId);

    /**
     * 查询用户订单数
     * @param userId
     */
    public OrderStatusCountsVo getOrderStatusCounts(String userId);

    /**
     * 获得分页的订单动向
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult getMyOrderTrend(String userId,Integer page,Integer pageSize);
}
