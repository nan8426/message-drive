package com.message.drive;

import com.message.drive.constant.DateConstant;
import com.message.drive.enums.ReceiveHandleStateEnum;
import com.message.drive.enums.SendHandleStateEnum;
import com.message.drive.model.ReceiveMessage;
import com.message.drive.model.SendMessage;
import com.message.drive.receive.ReceiveExecute;
import com.message.drive.receive.ReceiveListener;
import com.message.drive.receive.ReceiveMessageService;
import com.message.drive.send.SendExecute;
import com.message.drive.send.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/3 19:10 wyx-order
 **/
@Slf4j
@Component
@EnableScheduling
public class DriveManageImpl implements DriveManage {

    @Autowired
    private SendMessageService sendMessageService;

    @Autowired
    private ReceiveMessageService receiveMessageService;

    @Lazy
    @Autowired
    private SendExecute sendExecute;

    @Lazy
    @Autowired
    private ReceiveExecute receiveExecute;

    /**
     * 驱动 - 消息队列名称（必须为每个业务系统建立一个队列）
     */
    @Value("${drive.message.queue.name}")
    private String messageQueueName;

    /**
     * 默认exchange
     */
    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    /**
     * 驱动 - 本服务routingKey
     */
    @Value("${drive.message.routingKey}")
    private String routingKey;

    @Bean
    public RabbitTemplate driveRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate driveRabbitTemplate = new RabbitTemplate(connectionFactory);
        driveRabbitTemplate.setExchange(exchange);
        driveRabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // 修改消息发送成功
                sendMessageService.updateMessageState(correlationData.getId(), SendHandleStateEnum.SEND_SUCCESS.getCode());
                log.info("drive消息确认 - 发送成功 ID{}", correlationData.getId());
            } else {
                sendMessageService.updateMessageState(correlationData.getId(), SendHandleStateEnum.SEND_FAIL.getCode());
                log.error("drive消消息确认 - 发送失败 ID{}", correlationData.getId());
            }
        });
        return driveRabbitTemplate;
    }

    @Override
    public void send(Long businessId, String businessType, String date) {
        sendExecute.execute(businessId, businessType, routingKey, date);
    }

    @Override
    public void send(Long businessId, String businessType, String routingKey, String date) {
        sendExecute.execute(businessId, businessType, routingKey, date);
    }

    @Bean
    public MessageListenerContainer openAccountListenerContainer(ConnectionFactory connectionFactory, ReceiveListener receiveListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(messageQueueName);
        container.setMessageListener(receiveListener);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

    /**
     * 同步发送记录
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void synSend() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        String endGmtCreate = new SimpleDateFormat(DateConstant.DATETIME_FORMAT).format(calendar.getTime());

        // 查询一分钟之前发送状态为待确认的
        List<Integer> sendStateList = new ArrayList<>();
        sendStateList.add(SendHandleStateEnum.SEND_FAIL.getCode());
        sendStateList.add(SendHandleStateEnum.WAITING_CONFIRM.getCode());
        List<SendMessage> sendMessagesListSelect = sendMessageService.findOverdueOrder(sendStateList, endGmtCreate);
        if (sendMessagesListSelect.isEmpty()) {
            return;
        }

        // 调用发送执行方法
        for (SendMessage sendMessage : sendMessagesListSelect) {
            sendExecute.sendMessage(sendMessage);
        }
    }

    /**
     * 同步接收记录
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void synReceive() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -1);
        String endGmtCreate = new SimpleDateFormat(DateConstant.DATETIME_FORMAT).format(calendar.getTime());

        // 查询一分钟之前发送状态为初始
        List<Integer> receiveStateList = new ArrayList<>();
        receiveStateList.add(ReceiveHandleStateEnum.INIT.getCode());
        List<ReceiveMessage> receiveMessageList = receiveMessageService.findOverdueOrder(receiveStateList, endGmtCreate);
        if (receiveMessageList.isEmpty()) {
            return;
        }

        // 调用接收执行方法
        for (ReceiveMessage receiveMessage : receiveMessageList) {
            receiveExecute.execute(receiveMessage.getMessageId(), receiveMessage.getBusinessType());
        }
    }

}
