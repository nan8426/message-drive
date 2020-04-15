package com.message.drive.receive;

import com.message.drive.dao.ReceiveMessageMapper;
import com.message.drive.model.ReceiveMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/3 9:46 wyx
 **/
@Slf4j
@Service
public class ReceiveMessageService {

    @Resource
    private ReceiveMessageMapper receiveMessageMapper;

    public void insert(ReceiveMessage receiveMessage) {
        if (receiveMessage == null || StringUtils.isEmpty(receiveMessage.getMessageId())) {
            throw new RuntimeException("参数错误");
        }

        int total = receiveMessageMapper.insertSelective(receiveMessage);
        if (total <= 0) {
            throw new RuntimeException("系统异常");
        }
    }

    public void update(ReceiveMessage receiveMessage) {
        if (receiveMessage == null || StringUtils.isEmpty(receiveMessage.getMessageId())) {
            throw new RuntimeException("参数错误");
        }

        receiveMessageMapper.updateByPrimaryKeySelective(receiveMessage);
    }

    public ReceiveMessage getByMessageId(String messageId) {
        if (StringUtils.isEmpty(messageId)) {
            return null;
        }

        return receiveMessageMapper.selectByPrimaryKey(messageId);
    }

    public List<ReceiveMessage> findByBusinessId(Long businessId) {
        if (businessId == null) {
            return Collections.emptyList();
        }

        return receiveMessageMapper.findByBusinessId(businessId);
    }

    public void updateHandleState(String messageId, Integer handleState) {
        if (StringUtils.isEmpty(messageId) || handleState == null) {
            throw new RuntimeException("参数错误");
        }

        ReceiveMessage receiveMessageUpdate = new ReceiveMessage();
        receiveMessageUpdate.setMessageId(messageId);
        receiveMessageUpdate.setHandleState(handleState);

        update(receiveMessageUpdate);
    }

    public List<ReceiveMessage> findOverdueOrder(List<Integer> receiveStateList, String endGmtCreate) {
        if (receiveStateList.isEmpty() || StringUtils.isEmpty(endGmtCreate)){
            return Collections.emptyList();
        }

        return receiveMessageMapper.findOverdueOrder(receiveStateList, endGmtCreate);
    }

}
