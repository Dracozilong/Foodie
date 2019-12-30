package com.imooc.service.center;

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
}
