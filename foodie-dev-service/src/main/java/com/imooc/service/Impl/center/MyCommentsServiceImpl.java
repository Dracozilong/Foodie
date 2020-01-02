package com.imooc.service.Impl.center;

import com.imooc.mapper.OrderItemsMapper;
import com.imooc.pojo.OrderItems;
import com.imooc.service.center.MyCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyCommentsServiceImpl implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Override
    public List<OrderItems> queryPendingComment(String orderId) {

        //创建orderItems
        OrderItems orderItems = new OrderItems();

        orderItems.setOrderId(orderId);

        //根据orderId 查询订单对应的订单下的商品
        List<OrderItems> itemsList = orderItemsMapper.select(orderItems);

        return itemsList;
    }
}
