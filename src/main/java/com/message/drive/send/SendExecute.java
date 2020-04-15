package com.message.drive.send;

import com.alibaba.fastjson.JSONObject;
import com.message.drive.util.StringUtil;
import com.message.drive.enums.SendHandleStateEnum;
import com.message.drive.model.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/8 20:49 wyx
 **/
@Slf4j
@Component
public class SendExecute {

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    @Qualifier("driveRabbitTemplate")
    private RabbitTemplate driveRabbitTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void execute(Long businessId, String businessType, String routingKey, String date) {
        String messageId = StringUtil.randomGUID();
        SendMessage message = new SendMessage();
        message.setMessageId(messageId);
        message.setBusinessId(businessId);
        message.setBusinessType(businessType);
        message.setData(date);
        message.setExchange(driveRabbitTemplate.getExchange());
        message.setRoutingKey(routingKey);
        message.setGmtSend(new Date());
        message.setMessageState(SendHandleStateEnum.WAITING_CONFIRM.getCode());
        sendMessageService.insert(message);

        log.info("开始异步发送消息{}", message);
        sendMessage(message);
    }

    /**
     * 发送消息（这里用public只是为了事务）
     *
     * @param message
     */
    @Async("sendPool")
    public void sendMessage(SendMessage message) {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(message.getMessageId());
        driveRabbitTemplate.convertAndSend(message.getRoutingKey(), (Object) JSONObject.toJSONString(message), correlationData);
        log.info("异步消息发送成功{}", message);
    }

}
