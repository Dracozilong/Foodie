package com.imooc.service.Impl.center;

import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.mapper.OrdersMapperCustom;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.vo.MyOrderVo;
import com.imooc.pojo.vo.OrderStatusCountsVo;
import com.imooc.service.center.MyOrderService;
import com.imooc.utils.PagedGridResult;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrderServiceImpl extends BaseService implements MyOrderService {

    @Autowired
    private OrdersMapperCustom ordersMapperCustom;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;
    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();

        map.put("userId",userId);

        //分页
        PageHelper.startPage(page, pageSize);

        if (orderStatus!=null){
            map.put("orderStatus",orderStatus);
        }
        List<MyOrderVo> list = ordersMapperCustom.queryMyOrders(map);

        PagedGridResult pagedGridResult = setterPagedGrid(list, page);

        return pagedGridResult;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateDeliverOrdersStatus(String orderId) {

        //创建订单状态对象
        OrderStatus orderStatus = new OrderStatus();
        //修改订单状态
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        //修改发货时间
        orderStatus.setDeliverTime(new Date());

        //创建查询条件
        Example example = new Example(OrderStatus.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("orderId",orderId);

        criteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_DELIVER.type);

        orderStatusMapper.updateByExampleSelective(orderStatus,example);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyorder(String userId, String orderId) {

        Orders orders = new Orders();

        orders.setId(orderId);

        orders.setUserId(userId);

        orders.setIsDelete(YesOrNo.NO.code);

        return ordersMapper.selectOne(orders);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {

        OrderStatus orderStatus = new OrderStatus();

        orderStatus.setSuccessTime(new Date());

        orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);

        //创建查询条件
        Example example = new Example(OrderStatus.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("orderId",orderId);

        criteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);

        int result = orderStatusMapper.updateByExampleSelective(orderStatus, example);

        return result==1 ?true :false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean deleteOrder(String userId, String orderId) {

        Orders orders = new Orders();

        orders.setIsDelete(YesOrNo.YES.code);

        orders.setUpdatedTime(new Date());

        //创建查询条件
        Example example = new Example(Orders.class);

        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("id",orderId);

        criteria.andEqualTo("userId",userId);

        int result = ordersMapper.updateByExampleSelective(orders, example);

        return result==1 ?true :false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVo getOrderStatusCounts(String userId) {

        Map<String,Object> map = new HashMap<>();


        map.put("userId",userId);

        //查询待支付的订单
        map.put("orderStatus",OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        //查询代发货的订单
        map.put("orderStatus",OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        //查询待收货的订单
        map.put("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        //查询待评价的订单
        map.put("orderStatus",OrderStatusEnum.SUCCESS.type);
        map.put("isComment",YesOrNo.NO.code);
        int waitCommentCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        OrderStatusCountsVo countsVo = new OrderStatusCountsVo(waitPayCounts,waitDeliverCounts,waitReceiveCounts,waitCommentCounts);

        return countsVo;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getMyOrderTrend(String userId, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();

        map.put("userId",userId);

        PageHelper.startPage(page,pageSize);

        List<OrderStatus> list = ordersMapperCustom.getMyOrderTrend(map);

        return setterPagedGrid(list,page);


    }
}
