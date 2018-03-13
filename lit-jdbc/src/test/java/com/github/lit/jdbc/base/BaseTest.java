package com.github.lit.jdbc.base;

import com.github.lit.jdbc.config.SpringConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User : liulu
 * Date : 2017-3-6 21:00
 * version $Id: BaseTest.java, v 0.1 Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
@Slf4j
public class BaseTest {

    public void printUseTime (Long start) {
        log.info("\n use time : {} \n", System.currentTimeMillis() - start);
    }
}
