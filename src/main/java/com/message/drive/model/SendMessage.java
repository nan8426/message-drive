package com.message.drive.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * cs_send_message
 * @author 
 */
@Data
public class SendMessage implements Serializable {
    /**
     * 消息类型
     */
    private String messageId;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务订单号
     */
    private Long businessId;

    /**
     * 路由
     */
    private String exchange;

    /**
     * 路由策略
     */
    private String routingKey;

    /**
     * 数据
     */
    private String data;

    /**
     * 发送时间
     */
    private Date gmtSend;

    /**
     * 确认时间
     */
    private Date gmtConfirm;

    /**
     * 状态
     */
    private Integer messageState;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;

    private static final long serialVersionUID = 1L;
}