package com.github.lit.support.mybatis;

import com.github.lit.support.model.Goods;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * User : liulu
 * Date : 2018/7/11 19:26
 * version $Id: GoodsMapper.java, v 0.1 Exp $
 */
public interface GoodsMapper extends BaseMapper<Goods> {


    @SelectProvider(type = GoodsSqlProvider.class, method = "findByCode")
    Goods findByCode(String code);

    @org.apache.ibatis.annotations.Select("select * from goods")
    Goods findBySql(String s);


    class GoodsSqlProvider {

        public String findByCode(ProviderContext providerContext) {
//            return "select " + baseColumns + " from goods where tenant_id = #{tenantId} and code = #{code}";

//            Select select = new Select(Goods.class);
//            select.where("tenant_id = #{tenantId} and code = #{code}");
//            return select.build();
            return "";
        }


    }


}
