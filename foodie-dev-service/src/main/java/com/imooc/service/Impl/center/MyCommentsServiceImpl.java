package com.imooc.service.Impl.center;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.ItemsCommentsMapperCustom;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.pojo.vo.MyCommentVO;
import com.imooc.service.center.MyCommentsService;
import com.imooc.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Override
    public List<OrderItems> queryPendingComment(String orderId) {

        //创建orderItems
        OrderItems orderItems = new OrderItems();

        orderItems.setOrderId(orderId);

        //根据orderId 查询订单对应的订单下的商品
        List<OrderItems> itemsList = orderItemsMapper.select(orderItems);

        return itemsList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList) {

        //1.保存评论到item_comments
         //1.1循环commentList表，设置comment_id
           commentList.stream().forEach(comment->{
               comment.setCommentId(sid.nextShort());
           });

           Map<String,Object> map = new HashMap<>();
           map.put("userId",userId);
           map.put("commentList",commentList);

           itemsCommentsMapperCustom.saveComments(map);

        //2.修改orders表中的评价状态
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setIsComment(YesOrNo.YES.code);
        ordersMapper.updateByPrimaryKeySelective(orders);

        //3.修改order_status中的评价时间
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCommentTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {

        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        PageHelper.startPage(page,pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);

        return setterPagedGrid(list,page);
    }


}
