package com.github.lit.code.parser;

import com.github.lit.code.config.Configuration;
import com.github.lit.code.context.ConfigConst;
import com.github.lit.code.context.GenerationException;
import com.github.lit.code.context.Task;
import com.github.lit.code.util.BeanUtils;
import com.oracle.javafx.jmx.json.JSONDocument;

import java.util.List;

/**
 * User : liulu
 * Date : 2018/2/7 16:41
 * version $Id: TasksParser.java, v 0.1 Exp $
 */
public class TasksParser implements ConfigParser {

    @Override
    public String getConfigKey() {
        return ConfigConst.TASKS;
    }

    @Override
    public void parser(Configuration configuration, JSONDocument jsonDocument) {
        if (!jsonDocument.isArray()) {
            throw new GenerationException("tasks 配置项必须是数组");
        }
        List<Object> array = jsonDocument.array();
        if (array == null || array.isEmpty()) {
            return;
        }
        for (Object obj : array) {
            JSONDocument subDocument = (JSONDocument) obj;
            if (subDocument.isArray()) {
                throw new GenerationException("tasks子项不能为数组!");
            }
            Task task = BeanUtils.mapToBean(subDocument.object(), Task.class);
            String _package = subDocument.getString("package");
            task.set_package(_package);
            configuration.addTask(task);
        }
    }
}
