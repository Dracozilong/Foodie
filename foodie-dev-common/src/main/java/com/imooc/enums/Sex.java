package com.imooc.enums;

public enum Sex {
    women(0,"女"),
    man(1,"男"),
    secret(2,"保密");

    //编号 枚举的成员变量不能设置为private ，应该是public final
    public final Integer code;

    //代表中文 枚举的成员变量不能设置为private ，应该是public final
    public final String sex;

    Sex(Integer code, String sex) {
        this.code = code;
        this.sex = sex;
    }

}
