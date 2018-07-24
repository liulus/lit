package com.github.lit.support.event;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * User : liulu
 * Date : 2017/8/3 20:22
 * version $Id: EventComponent.java, v 0.1 Exp $
 */
@Retention( RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
@Documented
@Component
public @interface EventComponent {
}
