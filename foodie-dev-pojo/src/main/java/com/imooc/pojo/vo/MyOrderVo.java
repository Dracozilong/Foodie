package com.imooc.pojo.vo;

import java.util.Date;
import java.util.List;

/**
 * 用户中心-查询我的订单
 */
public class MyOrderVo {

    private String orderId;

    private Date createTime;

    private Integer payMethod;

    private Integer realPayAmount;

    private Integer postAmount;

    private Integer isComment;

    private Integer orderStatus;

    private List<MySubOrderItemVo> subOrderItemList;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(Integer payMethod) {
        this.payMethod = payMethod;
    }

    public Integer getRealPayAmount() {
        return realPayAmount;
    }

    public void setRealPayAmount(Integer realPayAmount) {
        this.realPayAmount = realPayAmount;
    }

    public Integer getPostAmount() {
        return postAmount;
    }

    public void setPostAmount(Integer postAmount) {
        this.postAmount = postAmount;
    }

    public Integer getIsComment() {
        return isComment;
    }

    public void setIsComment(Integer isComment) {
        this.isComment = isComment;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<MySubOrderItemVo> getSubOrderItemList() {
        return subOrderItemList;
    }

    public void setSubOrderItemList(List<MySubOrderItemVo> subOrderItemList) {
        this.subOrderItemList = subOrderItemList;
    }
}
