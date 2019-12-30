package com.imooc.enums;



public enum PayMethod {

    WEIXIN(1,"微信"),
    ALIPAY(2,"支付宝");

    public final Integer type;

    public final String code;

    PayMethod(Integer type, String code) {
        this.type = type;
        this.code = code;
    }
}
