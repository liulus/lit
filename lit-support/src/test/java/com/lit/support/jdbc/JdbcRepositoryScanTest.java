package com.lit.support.jdbc;

import com.lit.support.configure.SpringTestConfigure;
import com.lit.support.jdbc.repository.ProductRepository;
import com.lit.support.model.SignProduct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author liulu
 * @version V1.0
 * @since 2020/9/28
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringTestConfigure.class)
public class JdbcRepositoryScanTest {


    @Resource
    private ProductRepository productRepository;

    @Test
    public void test() {
        int update = productRepository.update(new SignProduct());
        System.out.println(update);
        System.out.println("-----------------");
    }
}
