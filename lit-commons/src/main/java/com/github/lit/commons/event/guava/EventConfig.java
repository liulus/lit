package com.github.lit.commons.event.guava;

import com.github.lit.commons.event.*;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.Executors;

/**
 * User : liulu
 * Date : 2017/8/3 20:47
 * version $Id: EventConfig.java, v 0.1 Exp $
 */
public class EventConfig {


    @Bean
    public EventPublisher eventPublisher() {

        AsyncEventBus asyncEventBus = new AsyncEventBus("async-default", Executors.newFixedThreadPool(10));

        return new GuavaEventPublisher(new EventBus(), asyncEventBus);
    }

    @Bean
    public PublishEventAspect publishEventAspect() {
        return new PublishEventAspect();
    }

    @EventListener
    @Event(eventClass = AppStartedEvent.class)
    public void registerEvent(ContextRefreshedEvent event) {

        ApplicationContext context = event.getApplicationContext();

        EventPublisher publisher = context.getBean(EventPublisher.class);
        Map<String, Object> eventListenerBeans = context.getBeansWithAnnotation(EventComponent.class);
        for (Object eventListener : eventListenerBeans.values()) {
            publisher.register(eventListener);
        }
    }

}
