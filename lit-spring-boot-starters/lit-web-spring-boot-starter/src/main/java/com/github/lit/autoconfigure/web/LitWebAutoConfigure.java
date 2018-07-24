package com.github.lit.autoconfigure.web;

import com.github.lit.support.web.annotation.EnableLitWeb;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 17:31
 */
@Configuration
@EnableLitWeb
@ComponentScan("com.github.lit.support")
public class LitWebAutoConfigure {
}
