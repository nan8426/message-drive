package com.message.drive.dao;

import com.message.drive.model.SendMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SendMessageMapper继承基类
 */
public interface SendMessageMapper extends BaseMapper<SendMessage, String> {

    /**
     * 根据业务订单号查询列表
     *
     * @param businessId
     * @return
     */
    List<SendMessage> findByBusinessId(Long businessId);

    /**
     * 根据状态查询创建时间小于等于endGmtCreate
     * @param sendStateList
     * @param endGmtCreate
     * @return
     */
    List<SendMessage> findOverdueOrder(@Param(value = "sendStateList") List<Integer> sendStateList, @Param(value = "endGmtCreate") String endGmtCreate);
}