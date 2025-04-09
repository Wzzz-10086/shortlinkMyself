package com.wzzz.shortlink.admin.enums;

import com.wzzz.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCode implements IErrorCode {


    USER_NULL("B00200", "用户记录不存在"),
    USER_EXIST("B00201", "用户记录已存在"),

    USER_REGISTER_FAIL("B00202", "用户注册失败"),

    USER_LOGIN_ERROR("B00203", "用户登录失败"),

    USER_ALREADY_LOGIN("B00204", "用户已登录"),
    USER_NOT_LOGIN("B00205","用户未登录或者token不存在" ),
    FLOW_LIMIT_ERROR("A000300", "当前系统繁忙，请稍后再试");


    private final String code;

    private final String message;

    UserErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
