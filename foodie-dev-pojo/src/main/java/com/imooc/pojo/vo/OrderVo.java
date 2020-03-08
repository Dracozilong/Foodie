package com.imooc.pojo.vo;

import com.imooc.pojo.bo.ShopcartBO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderVo {

    private MerchantOrdersVo merchantOrdersVo;

    private String orderId;

    private List<ShopcartBO> shopcartBOList;

    public List<ShopcartBO> getShopcartBOList() {
        return shopcartBOList;
    }

    public void setShopcartBOList(List<ShopcartBO> shopcartBOList) {
        this.shopcartBOList = shopcartBOList;
    }

    public MerchantOrdersVo getMerchantOrdersVo() {
        return merchantOrdersVo;
    }

    public void setMerchantOrdersVo(MerchantOrdersVo merchantOrdersVo) {
        this.merchantOrdersVo = merchantOrdersVo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
