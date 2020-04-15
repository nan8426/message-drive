package com.message.drive.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @Describe 功能描述
 * @Author 袁江南
 * @Date 2019/12/24 15:41 message-drive
 **/
@Slf4j
@Service
public class RedisLock {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 分布式锁实现
     * @param lockName 锁名称
     * @param businessId 业务ID
     * @param handle 业务处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void lock(String lockName, Object businessId, VoidHandle handle) {
        RLock rLock = getLock(lockName, businessId);
        try {
            rLock.lock();
            log.info("业务ID{}，获取锁成功", businessId);
            handle.execute();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 带返回值分布式锁实现
     * @param lockName 锁名称
     * @param businessId 业务ID
     * @param handle 业务处理
     * @param <T> 返回值
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> T lock(String lockName, Object businessId, ReturnHandle<T> handle) {
        RLock rLock = getLock(lockName, businessId);
        try {
            rLock.lock();
            log.info("业务ID{}，获取锁成功", businessId);
            return handle.execute();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 分布式锁实现
     * @param lockName 锁名称
     * @param businessId 业务ID
     * @param handle 业务处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void tryLock(String lockName, Object businessId, VoidHandle handle) {
        RLock rLock = getLock(lockName, businessId);
        if (!rLock.tryLock()) {
            log.info("业务ID{}，获取锁失败，返回", businessId);
            return;
        }

        try {
            log.info("业务ID{}，获取锁成功", businessId);
            handle.execute();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 带返回值分布式锁实现
     * @param lockName 锁名称
     * @param businessId 业务ID
     * @param handle 业务处理
     * @param <T> 返回值
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> T tryLock(String lockName, Object businessId, ReturnHandle<T> handle) {
        RLock rLock = getLock(lockName, businessId);
        if (!rLock.tryLock()) {
            log.info("业务ID{}，获取锁失败，返回null", businessId);
            return null;
        }

        try {
            log.info("业务ID{}，获取锁成功", businessId);
            return handle.execute();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 分布式锁实现
     * @param lockName 锁名称
     * @param businessId 业务ID
     * @param handle 业务处理
     */
    @Transactional(rollbackFor = Exception.class)
    public void tryLockException(String lockName, Object businessId, VoidHandle handle) {
        RLock rLock = getLock(lockName, businessId);
        if (!rLock.tryLock()) {
            log.info("业务ID{}，获取锁失败，抛异常处理", businessId);
            throw new RuntimeException("处理中");
        }

        try {
            log.info("业务ID{}，获取锁成功", businessId);
            handle.execute();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 带返回值分布式锁实现
     * @param lockName 锁名称
     * @param businessId 业务ID
     * @param handle 业务处理
     * @param <T> 返回值
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> T tryLockException(String lockName, Object businessId, ReturnHandle<T> handle) {
        RLock rLock = getLock(lockName, businessId);
        if (!rLock.tryLock()) {
            log.info("业务ID{}，获取锁失败，抛异常处理", businessId);
            throw new RuntimeException("处理中");
        }

        try {
            log.info("业务ID{}，获取锁成功", businessId);
            return handle.execute();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 获取锁
     * @param lockName
     * @param businessId
     * @return
     */
    private RLock getLock(String lockName, Object businessId) {
        log.info("获取分布式锁lockName:{},businessId:{}", lockName, businessId);
        if (StringUtils.isEmpty(lockName)) {
            throw new RuntimeException("分布式锁KEY为空");
        }
        if (StringUtils.isEmpty(businessId)) {
            throw new RuntimeException("业务ID为空");
        }

        String lockKey = lockName + businessId.toString();
        return redissonClient.getLock(lockKey);
    }

}
