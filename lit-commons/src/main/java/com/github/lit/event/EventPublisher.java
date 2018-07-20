package com.github.lit.event;

/**
 * User : liulu
 * Date : 2017/8/3 20:27
 * version $Id: EventPublisher.java, v 0.1 Exp $
 */
public interface EventPublisher {

    /**
     * 注册事件监听
     *
     * @param event 事件
     */
    void register(Object event);

    /**
     * 注销事件监听
     *
     * @param event 事件
     */
    void unregister(Object event);

    /**
     * 发布事件
     *
     * @param event 事件
     */
    void publish(Object event);

    /**
     * 异步发布事件
     *
     * @param event 事件
     */
    void asyncPublish(Object event);

}
