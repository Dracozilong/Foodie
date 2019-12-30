package com.imooc.service.Impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVo;
import com.imooc.pojo.vo.OrderVo;
import com.imooc.service.AddressService;
import com.imooc.service.Itemservice;
import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import org.aspectj.weaver.ast.Or;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private AddressService addressService;

    @Autowired
    private Itemservice itemservice;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;



    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderVo createOrder(SubmitOrderBO submitOrderBO) {

        //获取userId
        String userId = submitOrderBO.getUserId();

        //获取addressId
        String addressId = submitOrderBO.getAddressId();

        //获取产品规格
        String itemSpecIds = submitOrderBO.getItemSpecIds();

        //获取留言信息
        String leftMsg = submitOrderBO.getLeftMsg();

        //获取支付方式
        Integer payMethod = submitOrderBO.getPayMethod();

        //包邮费用为0
        Integer postAmount =0;

        //根据Sid生成订单Id
        String orderId = sid.nextShort();

        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);

        //1. 创建订单Order
        Orders orders =new Orders();
        orders.setId(orderId);
        orders.setLeftMsg(leftMsg);
        orders.setPostAmount(postAmount);
        orders.setUserId(userId);
        orders.setCreatedTime(new Date());
        orders.setUpdatedTime(new Date());
        orders.setPayMethod(payMethod);
        orders.setReceiverName(userAddress.getReceiver());
        orders.setReceiverMobile(userAddress.getMobile());
        orders.setReceiverAddress(userAddress.getProvince()+" "+userAddress.getCity()+" " +userAddress.getDistrict()+""+userAddress.getDetail());
        orders.setIsComment(YesOrNo.NO.code);
        orders.setIsDelete(YesOrNo.NO.code);

        //2.循环根据itemSpecIds保存订单商品信息表
        String[] itemSpecIdArr = itemSpecIds.split(",");

        //总价格
         Integer  totalAmount =0;

        //商品实际支付价格累计
        Integer realPayAmount =0;

        //TODO 整合redis后，商品购买数量重新从redis的购物车中获取
        int buyCounts = 1 ;

        for (String itemSpecId : itemSpecIdArr) {

            //2.1根据itemSpecId查询商品价格,计算价格并保存
            ItemsSpec itemsSpec = itemservice.queryItemsBySpecId(itemSpecId);
            totalAmount+=itemsSpec.getPriceNormal()*buyCounts;
            realPayAmount+=itemsSpec.getPriceDiscount()*buyCounts;

            //2.2根据item_Id查询产品主图,查询商品信息
            String itemId = itemsSpec.getItemId();
            Items item = itemservice.queryItemById(itemId);
            String imgUrl = itemservice.queryItemImg(itemId);

            //设置orderItems的主键
            String orderItemsId = sid.nextShort();

            //2.3根据order_id 保存商品详细信息
            OrderItems orderDetails = new OrderItems();
            orderDetails.setId(orderItemsId);
            orderDetails.setOrderId(orderId);
            orderDetails.setItemId(itemId);
            orderDetails.setItemName(item.getItemName());
            orderDetails.setItemImg(imgUrl);
            orderDetails.setBuyCounts(buyCounts);
            orderDetails.setItemSpecId(itemSpecId);
            orderDetails.setItemSpecName(itemsSpec.getName());
            orderDetails.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(orderDetails);

            //2.4在用户提交订单以后 ，规格表中需要扣除库存
            itemservice.decreaseItemSpecStock(itemSpecId,buyCounts);
        }

        orders.setTotalAmount(totalAmount);
        orders.setRealPayAmount(realPayAmount);

        ordersMapper.insert(orders);

        //3.保存订单状态表
        OrderStatus orderStatus =new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        orderStatus.setCreatedTime(new Date());

        orderStatusMapper.insert(orderStatus);

        //4.生成商户订单
        MerchantOrdersVo merchantOrdersVo = new MerchantOrdersVo();
        merchantOrdersVo.setMerchantOrderId(orderId);
        merchantOrdersVo.setMerchantUserId(userId);
        merchantOrdersVo.setAmount(realPayAmount+postAmount);
        merchantOrdersVo.setPayMethod(payMethod);


        //orderVo
        OrderVo orderVo = new OrderVo();

        orderVo.setOrderId(orderId);
        orderVo.setMerchantOrdersVo(merchantOrdersVo);

        return orderVo;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {

        //修改订单状态
        OrderStatus payOrderStatus =new OrderStatus();
        payOrderStatus.setOrderId(orderId);
        payOrderStatus.setOrderStatus(orderStatus);
        payOrderStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(payOrderStatus);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderStatusInfo(String orderId) {
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(orderId);
        return orderStatus;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {

        //查询订单状态为10的订单
        OrderStatus orderStatus = new OrderStatus();

        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);

        List<OrderStatus> statusList = orderStatusMapper.select(orderStatus);

        //对未支付订单进行循环，查询创建时间
        statusList.stream().forEach(os->{
            Date date = new Date();
            Date createdTime = os.getCreatedTime();
            if(DateUtil.daysBetween(createdTime,date)>=1){
                //修改订单状态为取消订单,超过一天关闭订单
               docloseOrder(os.getOrderId());
            }
        });

    }

    @Transactional(propagation = Propagation.REQUIRED)
    void docloseOrder(String orderId){

        //修改订单状态
        OrderStatus orderStatus = new OrderStatus();

        orderStatus.setOrderId(orderId);

        orderStatus.setCloseTime(new Date());

        orderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);

        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }
}
