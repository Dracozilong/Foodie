package com.imooc.mapper;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.vo.MyOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OrdersMapperCustom {

    /**
     * 查询用户订单
     * @param map
     * @return
     */
    public List<MyOrderVo> queryMyOrders(@Param("paramsMap")Map<String,Object> map );

    public int getMyOrderStatusCounts(@Param("paramsMap") Map<String ,Object> map);

    public List<OrderStatus> getMyOrderTrend(@Param("paramsMap") Map<String ,Object> map);
}
