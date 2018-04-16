package com.github.lit.commons.event.guava;

import com.github.lit.commons.event.EventPublisher;
import com.github.lit.commons.exception.BizException;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User : liulu
 * Date : 2017/8/3 20:29
 * version $Id: GuavaEventPublisher.java, v 0.1 Exp $
 */
@NoArgsConstructor
public class GuavaEventPublisher implements EventPublisher {

    @Getter
    @Setter
    private EventBus eventBus;

    @Getter
    @Setter
    private AsyncEventBus asyncEventBus;

    public GuavaEventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public GuavaEventPublisher(EventBus eventBus, AsyncEventBus asyncEventBus) {
        this.eventBus = eventBus;
        this.asyncEventBus = asyncEventBus;
    }

    @Override
    public void register(Object event) {
        eventBus.register(event);
        asyncEventBus.register(event);
    }

    @Override
    public void unregister(Object event) {
        eventBus.unregister(event);
        asyncEventBus.unregister(event);
    }

    @Override
    public void publish(Object event) {
        eventBus.post(event);
        // 处理自定义异常
        BizException bizException = EventExceptionHandler.getBizException();
        if (bizException != null) {
            throw bizException;
        }
        EventExceptionHandler.clearBizException();
    }

    @Override
    public void asyncPublish(Object event) {
        asyncEventBus.post(event);
    }
}
