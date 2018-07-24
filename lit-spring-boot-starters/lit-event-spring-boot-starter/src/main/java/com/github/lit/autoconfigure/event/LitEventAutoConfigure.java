package com.github.lit.autoconfigure.event;

import com.github.lit.support.event.guava.EnableGuavaEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 17:09
 */
@Configuration
@EnableGuavaEvent
@ComponentScan("com.github.lit.support")
public class LitEventAutoConfigure {



}
