package com.github.lit.test.base;

import com.github.lit.test.config.SpringConfig;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User : liulu
 * Date : 2018/3/16 13:53
 * version $Id: SpringBaseTest.java, v 0.1 Exp $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class SpringBaseTest extends BaseTest {
}
