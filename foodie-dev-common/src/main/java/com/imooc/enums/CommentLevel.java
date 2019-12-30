package com.imooc.enums;

public enum  CommentLevel {

    GOOD(0,"好评"),
    NORMAL(1,"中评"),
    BAD(2,"差评");

    public final Integer code;

    public final String type;

    CommentLevel(Integer code, String type) {
        this.code = code;
        this.type = type;
    }
}
