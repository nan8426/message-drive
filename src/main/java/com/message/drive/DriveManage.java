package com.message.drive;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/3 19:07 wyx-order
 **/
public interface DriveManage {

    /**
     * 发送消息
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param date 业务数据
     */
    void send(Long businessId, String businessType, String date);

    /**
     * 发送消息
     * @param businessId 业务ID
     * @param businessType 业务类型
     * @param routingKey 路由key
     * @param date 业务数据
     */
    void send(Long businessId, String businessType, String routingKey, String date);

}
