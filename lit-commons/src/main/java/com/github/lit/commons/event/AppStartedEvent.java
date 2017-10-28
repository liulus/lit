package com.github.lit.commons.event;

import lombok.Getter;
import lombok.Setter;
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
}
