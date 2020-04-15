package com.message.drive.dao;

import java.io.Serializable;

/**
 * @Describe DAO 抽象接口
 * @Author 袁江南
 * @Date 2019/11/4 15:29 message-drive
 **/
public interface BaseMapper<T, P extends Serializable> {

    /**
     * 根据主键ID获取数据
     * @param id
     * @return
     */
    T selectByPrimaryKey(P id);

    /**
     * 插入数据
     * @param record
     * @return
     */
    int insert(T record);

    /**
     * 选址插入
     * @param record
     * @return
     */
    int insertSelective(T record);

    /**
     * 修改数据
     * @param record
     * @return
     */
    int updateByPrimaryKey(T record);

    /**
     * 选址修改
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);

    /**
     * 删除数据
     * @param id
     * @return
     */
    int deleteByPrimaryKey(P id);
}