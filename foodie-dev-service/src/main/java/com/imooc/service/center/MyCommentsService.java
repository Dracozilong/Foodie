package com.imooc.service.center;

import com.imooc.pojo.OrderItems;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyCommentsService {

    /**
     * 根据订单Id查询商品进行评价
     * @param orderId
     * @return
     */
    public List<OrderItems>  queryPendingComment(String orderId);


    /**
     * 保存用户商品评价
     * @param userId
     * @param orderId
     * @param commentList
     */
    public void saveComments(String userId, String orderId, List<OrderItemsCommentBO> commentList);


    public PagedGridResult queryMyComments(String userId,Integer page,Integer pageSize);

}
