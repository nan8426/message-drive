package com.message.drive.send;

import com.message.drive.dao.SendMessageMapper;
import com.message.drive.model.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2020/1/3 11:31 wyx
 **/
@Slf4j
@Service
public class SendMessageService {

    @Resource
    private SendMessageMapper sendMessageMapper;

    public void insert(SendMessage sendMessage) {
        if (sendMessage == null || StringUtils.isEmpty(sendMessage.getMessageId())) {
            throw new RuntimeException("参数错误");
        }

        int total = sendMessageMapper.insertSelective(sendMessage);
        if (total <= 0) {
            throw new RuntimeException("系统异常");
        }
    }

    public void update(SendMessage sendMessage) {
        if (sendMessage == null || StringUtils.isEmpty(sendMessage.getMessageId())) {
            throw new RuntimeException("参数错误");
        }

        sendMessageMapper.updateByPrimaryKeySelective(sendMessage);
    }

    public SendMessage getByMessageId(String messageId) {
        if (StringUtils.isEmpty(messageId)){
            return null;
        }

        return sendMessageMapper.selectByPrimaryKey(messageId);
    }

    public List<SendMessage> findByBusinessId(Long businessId) {
        if (businessId == null){
            return Collections.emptyList();
        }

        return sendMessageMapper.findByBusinessId(businessId);
    }

    public void updateMessageState(String messageId, Integer messageState) {
        if (StringUtils.isEmpty(messageId) || messageState == null) {
            throw new RuntimeException("参数错误");
        }

        SendMessage sendMessageUpdate = new SendMessage();
        sendMessageUpdate.setMessageId(messageId);
        sendMessageUpdate.setMessageState(messageState);
        sendMessageUpdate.setGmtConfirm(new Date());

        update(sendMessageUpdate);
    }

    public List<SendMessage> findOverdueOrder(List<Integer> sendStateList, String endGmtCreate) {
        if (sendStateList.isEmpty() || StringUtils.isEmpty(endGmtCreate)){
            return Collections.emptyList();
        }

        return sendMessageMapper.findOverdueOrder(sendStateList, endGmtCreate);
    }
}
