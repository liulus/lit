package com.lit.code.parser;

import com.lit.code.config.Configuration;
import com.lit.code.context.ConfigConst;
import com.lit.code.context.GenerationException;
import com.lit.code.context.Task;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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
    public void parser(Configuration configuration, JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            throw new GenerationException("tasks 配置项必须是数组");
        }

        Gson gson = new Gson();
        for (JsonElement subElement : jsonElement.getAsJsonArray()) {
            if (subElement.isJsonArray()) {
                throw new GenerationException("tasks子项不能为数组!");
            }
            Task task = gson.fromJson(subElement.toString(), Task.class);
            String _Package = subElement.getAsJsonObject().get("package").getAsString();
            task.set_package(_Package);
            configuration.addTask(task);
        }
    }
}