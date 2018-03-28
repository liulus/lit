package com.github.lit.test.main;

import com.github.lit.commons.util.ClassUtils;
import com.github.lit.jdbc.JdbcTools;
import com.github.lit.test.base.SpringBaseTest;
import com.github.lit.test.model.Goods;
import com.github.lit.test.model.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * User : liulu
 * Date : 2018/3/24 10:48
 * version $Id: DataInit.java, v 0.1 Exp $
 */
@Slf4j
public class DataInit extends SpringBaseTest {

    @Resource
    private JdbcTools jdbcTools;

    @Test
    @Transactional
    @Rollback(false)
    public void initGoods() throws Exception {

        URL data = ClassUtils.getDefaultClassLoader().getResource("data/goods.txt");
        if (data == null) {
            log.warn("data file is null");
            return;
        }
        Path path = Paths.get(data.toURI());
        List<String> lines = Files.readAllLines(path);

        int i = 1;
        StringBuilder supplierCodeStr = new StringBuilder();
        long start = System.currentTimeMillis();
        for (String line : lines) {
            String[] goodsInfo = line.split("<>");
            Goods goods = buildGoods(goodsInfo);

            jdbcTools.insert(goods);

            String supplierCode = goodsInfo[10];
            if (supplierCodeStr.indexOf(supplierCode + ",") == -1) {
                supplierCodeStr.append(supplierCode).append(",");
                Supplier supplier = Supplier.builder()
                        .code(supplierCode)
                        .name(goodsInfo[11])
                        .address("杭州市滨江区" + i + "号")
                        .contact("张三" + i)
                        .mobile(String.valueOf(13516784215L + i++ * 1254))
                        .build();
                jdbcTools.insert(supplier);
            }
        }
        printUseTime(start);
    }

    private Goods buildGoods(String[] goodsInfo) {
        return Goods.builder()
                .code(goodsInfo[0])
                .barCode(goodsInfo[1])
                .name(goodsInfo[2])
                .specification(goodsInfo[3])
                .unit(goodsInfo[4])
                .inventory(Double.valueOf(goodsInfo[5]).intValue())
                .price(Double.valueOf(goodsInfo[6]))
                .purchaserCode(goodsInfo[7])
                .category(goodsInfo[8])
                .categoryName(goodsInfo[9])
                .supplierCode(goodsInfo[10])
                .build();
    }
}
