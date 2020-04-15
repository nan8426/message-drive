package com.message.drive.receive;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/8 20:49 wyx
 **/
public interface ReceiveHandle {

    /**
     * 获取处理器名称
     * @return
     */
    String getName();

    /**
     * 业务处理
     * @param data 业务数据
     */
    void execute(String data);

}
