package com.message.drive.receive;

import com.alibaba.fastjson.JSONObject;
import com.message.drive.enums.ReceiveHandleStateEnum;
import com.message.drive.lock.RedisLock;
import com.message.drive.model.ReceiveMessage;
import com.message.drive.model.SendMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/9 13:16 wyx
 **/
@Slf4j
@Service
public class ReceiveListener implements ChannelAwareMessageListener {

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private ReceiveExecute receiveExecute;

    @Autowired
    private ReceiveMessageService receiveMessageService;

    /**
     * 写库锁名称
     */
    private static final String RECEIVE_WRITING_DATA_LOCK = "RECEIVE:WRITING:DATA:LOCK:";

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        log.info("消息驱动接收消息 - 数据{}", message.getBody());
        if (message == null || StringUtils.isEmpty(message.getBody())) {
            return;
        }

        // 获取发送数据
        String json = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("消息驱动接收消息 - 数据{}", json);
        SendMessage sendMessage = JSONObject.parseObject(json, SendMessage.class);
        if (sendMessage == null) {
            // 拒绝并且丢弃
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        redisLock.tryLock(RECEIVE_WRITING_DATA_LOCK, sendMessage.getMessageId(), () -> {
            // 是否接收过，已经接收过直接确认
            ReceiveMessage receiveMessageSelect = receiveMessageService.getByMessageId(sendMessage.getMessageId());
            if (receiveMessageSelect != null) {
                log.info("消息驱动接收消息 - 重复消息 ID{}", sendMessage.getMessageId());
                return;
            }

            // 消息入库
            ReceiveMessage receiveMessage = new ReceiveMessage();
            receiveMessage.setMessageId(sendMessage.getMessageId());
            receiveMessage.setBusinessType(sendMessage.getBusinessType());
            receiveMessage.setBusinessId(sendMessage.getBusinessId());
            receiveMessage.setData(sendMessage.getData());
            receiveMessage.setGmtReceive(new Date());
            receiveMessage.setHandleState(ReceiveHandleStateEnum.INIT.getCode());
            receiveMessageService.insert(receiveMessage);
            log.info("消息驱动接收消息 - 消息写入DB成功{}", receiveMessage);
        });

        // 调用业务执行
        receiveExecute.execute(sendMessage.getMessageId(), sendMessage.getBusinessType());

        // 消息确认
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
