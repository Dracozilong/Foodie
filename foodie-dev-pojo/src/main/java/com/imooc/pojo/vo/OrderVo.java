package com.imooc.pojo.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderVo {

    private MerchantOrdersVo merchantOrdersVo;

    private String orderId;

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
