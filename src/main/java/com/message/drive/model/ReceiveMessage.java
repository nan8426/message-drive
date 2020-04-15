package com.message.drive.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * cs_receive_message
 * @author 
 */
@Data
public class ReceiveMessage implements Serializable {

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
     * 数据
     */
    private String data;

    /**
     * 接收时间
     */
    private Date gmtReceive;

    /**
     * 状态
     */
    private Integer handleState;

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