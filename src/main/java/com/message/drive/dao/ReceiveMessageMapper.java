package com.message.drive.dao;

import com.message.drive.model.ReceiveMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ReceiveMessageMapper继承基类
 */
public interface ReceiveMessageMapper extends BaseMapper<ReceiveMessage, String> {

    /**
     * 根据业务订单和查询接收消息列表
     *
     * @param businessId
     * @return
     */
    List<ReceiveMessage> findByBusinessId(Long businessId);

    /**
     * 根据状态查询创建时间小于等于endGmtCreate
     * @param receiveStateList
     * @param endGmtCreate
     * @return
     */
    List<ReceiveMessage> findOverdueOrder(@Param(value = "receiveStateList") List<Integer> receiveStateList, @Param(value = "endGmtCreate") String endGmtCreate);
}