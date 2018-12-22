package com.github.lit.support.mybatis;

import com.github.lit.support.util.SerializedFunction;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author liulu
 * @version : v1.0
 * date : 7/24/18 09:54
 */
public interface BaseMapper<E> {

    /**
     * 新增一条记录
     *
     * @param entity 实体
     * @return 受影响记录
     */
    @InsertProvider(type = BaseSqlProvider.class, method = "insert")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int insert(E entity);

    /**
     * 更新一条记录
     *
     * @param entity entity
     * @return 受影响记录
     */
    @UpdateProvider(type = BaseSqlProvider.class, method = "update")
    int update(E entity);

    /**
     * 删除一条记录
     *
     * @param id id
     * @return 受影响记录
     */
    @DeleteProvider(type = BaseSqlProvider.class, method = "delete")
    int delete(Long id);

    /**
     * 根据id查询
     *
     * @param id id
     * @return Entity
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectById")
    E selectById(Long id);

    /**
     * 根据属性查询一条记录
     *
     * @param function property
     * @param value    value
     * @param <R>      R
     * @return Entity
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectByProperty")
    <R> E selectByProperty(@Param("property") SerializedFunction<E, R> function, @Param("value") Object value);

    /**
     * 根据属性查询记录列表
     *
     * @param function property
     * @param value    value
     * @param <R>      R
     * @return Entity
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectByProperty")
    <R> List<E> selectListByProperty(@Param("property") SerializedFunction<E, R> function, @Param("value") Object value);

    /**
     * 根据查询条件查询记录
     *
     * @param condition   condition
     * @param <Condition> Condition
     * @return List Entity
     */
    @SelectProvider(type = BaseSqlProvider.class, method = "selectList")
    <Condition> List<E> selectList(Condition condition);


}
