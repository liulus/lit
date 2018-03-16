package com.github.lit.test.base;

import lombok.extern.slf4j.Slf4j;

/**
 * User : liulu
 * Date : 2017-3-6 21:00
 * version $Id: BaseTest.java, v 0.1 Exp $
 */
@Slf4j
public class BaseTest {

    public void printUseTime (Long start) {
        log.info("\n use time : {} \n", System.currentTimeMillis() - start);
    }
}
