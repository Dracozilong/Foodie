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
public class MyOrderServiceImpl implements MyOrderService {

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

    private PagedGridResult setterPagedGrid(List<?> list, Integer page){
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page);
        grid.setRows(list);
        grid.setTotal(pageList.getPages());
        grid.setRecords(pageList.getTotal());
        return grid;
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

        orders.setIsDelete(YesOrNo.YES.code);

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
}
