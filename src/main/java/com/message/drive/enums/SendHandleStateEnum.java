package com.message.drive.enums;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/9 14:22 wyx
 **/
public enum SendHandleStateEnum {

    /**
     * 初始
     */
    INIT(0, "初始"),

    /**
     * 待确认
     */
    WAITING_CONFIRM(5, "待确认"),

    /**
     * 发送成功
     */
    SEND_SUCCESS(10, "发送成功"),

    /**
     * 发送失败
     */
    SEND_FAIL(15, "发送失败"),

    ;

    /**
     * 编码
     */
    private Integer code;

    /**
     * 说明
     */
    private String describe;

    SendHandleStateEnum(Integer code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

}
