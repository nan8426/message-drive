package com.message.drive.enums;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/9 14:21 wyx
 **/
public enum ReceiveHandleStateEnum {

    /**
     * 初始
     */
    INIT(0, "初始"),

    /**
     * 处理中
     */
    PROCESSING(5, "处理中"),

    /**
     * 处理成功
     */
    PROCESS_SUCCESS(10, "处理成功"),

    ;

    /**
     * 编码
     */
    private Integer code;

    /**
     * 说明
     */
    private String describe;

    ReceiveHandleStateEnum(Integer code, String describe) {
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
