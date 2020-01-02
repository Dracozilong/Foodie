package com.imooc.service.center;

import com.imooc.pojo.OrderItems;

import java.util.List;

public interface MyCommentsService {

    /**
     * 根据订单Id查询商品进行评价
     * @param orderId
     * @return
     */
    public List<OrderItems>  queryPendingComment(String orderId);
}
