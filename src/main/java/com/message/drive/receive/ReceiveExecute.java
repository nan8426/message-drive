package com.message.drive.receive;

import com.message.drive.enums.ReceiveHandleStateEnum;
import com.message.drive.lock.RedisLock;
import com.message.drive.model.ReceiveMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Describe 业务执行器
 * @Author 袁江南
 * @Date 2020/1/9 14:15 wyx
 **/
@Slf4j
@Service
public class ReceiveExecute {

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private ReceiveMessageService receiveMessageService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 接收处理类
     */
    private volatile Map<String, ReceiveHandle> handleMap;

    /**
     * 处理锁名称
     */
    private static final String RECEIVE_EXECUTE_LOCK = "RECEIVE:EXECUTE:LOCK:";

    /**
     * 通过类型获取接收处理类
     *
     * @param businessType
     * @return
     */
    private ReceiveHandle getReceiveHandle(String businessType) {
        if (CollectionUtils.isEmpty(handleMap)) {
            synchronized (this) {
                if (CollectionUtils.isEmpty(handleMap)) {
                    // 获取所有实现类型
                    Map<String, ReceiveHandle> beans = applicationContext.getBeansOfType(ReceiveHandle.class);
                    if (CollectionUtils.isEmpty(beans)) {
                        return null;
                    }

                    // 初始化map
                    handleMap = beans.values().stream().collect(Collectors.toMap(ReceiveHandle::getName, x -> x));
                }
            }
        }
        return handleMap.get(businessType);
    }

    /**
     * 业务处理操作
     *
     * @param messageId
     * @param businessType
     */
    @Transactional(rollbackFor = Exception.class)
    public void execute(String messageId, String businessType) {
        log.info("消息驱动开始业务处理 参数 messageId{} businessType{}", messageId, businessType);
        // 验证参数
        if (StringUtils.isEmpty(messageId) || StringUtils.isEmpty(businessType)) {
            return;
        }

        // 开始调用处理
        ReceiveHandle receiveHandle = getReceiveHandle(businessType);
        if (receiveHandle == null) {
            return;
        }

        redisLock.tryLock(RECEIVE_EXECUTE_LOCK, messageId, () -> {
            // 必须是待处理状态
            ReceiveMessage receiveMessageSelect = receiveMessageService.getByMessageId(messageId);
            if (receiveMessageSelect == null) {
                throw new RuntimeException("消息" + messageId + "不存在");
            }
            log.info("消息驱动业务处理 messageId{}当前状态{}", messageId, receiveMessageSelect.getHandleState());
            // 定时任务会查初始状态数据并调用此方执行
            if (!ReceiveHandleStateEnum.INIT.getCode().equals(receiveMessageSelect.getHandleState())) {
                return;
            }
            // 修改消息处理中
            receiveMessageService.updateHandleState(receiveMessageSelect.getMessageId(), ReceiveHandleStateEnum.PROCESSING.getCode());
            log.info("驱动开始业务处理 参数{}", receiveMessageSelect);

            // 处理业务
            receiveHandle.execute(receiveMessageSelect.getData());

            log.info("驱动结束业务处理 参数{}", receiveMessageSelect);

            // 修改消息处理成功
            receiveMessageService.updateHandleState(receiveMessageSelect.getMessageId(), ReceiveHandleStateEnum.PROCESS_SUCCESS.getCode());
        });
        log.info("消息驱动结束业务处理");
    }

}
