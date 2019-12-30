package com.imooc.enums;

public enum YesOrNo {

    NO(0,"否"),
    YES(1,"是");

    //编号 枚举的成员变量不能设置为private ，应该是public final
    public final Integer code;

    //代表中文 枚举的成员变量不能设置为private ，应该是public final
    public final String type;

    YesOrNo(Integer code, String type) {
        this.code = code;
        this.type = type;
    }
}
