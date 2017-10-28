package com.github.lit.jdbc;

import com.github.lit.jdbc.model.StatementContext;

/**
 * User : liulu
 * Date : 2017/6/4 9:57
 * version $Id: StatementExecutor.java, v 0.1 Exp $
 */
public interface StatementExecutor {


    /**
     * 执行语句
     *
     * @param context 执行语句需要的内容
     * @return 查询结果
     */
    Object execute(StatementContext context);


}
