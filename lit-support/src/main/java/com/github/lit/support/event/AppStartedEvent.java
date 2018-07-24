package com.github.lit.support.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * User : liulu
 * Date : 2017/8/9 22:10
 * version $Id: AppStartedEvent.java, v 0.1 Exp $
 */
@Getter
@Setter
public class AppStartedEvent {

    private ContextRefreshedEvent event;

    public ApplicationContext getApplicationContext() {
        return event == null ? null : event.getApplicationContext();
    }
}
