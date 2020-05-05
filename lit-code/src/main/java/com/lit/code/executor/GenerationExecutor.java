package com.lit.code.executor;

import com.lit.code.config.Configuration;
import com.lit.code.context.Table;
import com.lit.code.context.Task;
import com.lit.code.plugin.PluginExecutor;
import com.lit.code.util.FileUtils;
import com.lit.support.util.ClassUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

/**
 * User : liulu
 * Date : 2018/2/7 15:00
 * version $Id: GenerationExecutor.java, v 0.1 Exp $
 */
public class GenerationExecutor {

    private static final Logger LOGGER = Logger.getLogger(GenerationExecutor.class.getName());

    public static void execute(Configuration configuration) {

        Table table = configuration.getTable();
        Boolean overwrite = configuration.getBooleanConstant("overwrite", true);
        Boolean onChildModule = configuration.getBooleanConstant("onChildModule");
        String rootDir = configuration.getConstant("rootDir", System.getProperty("user.dir"));
        if (onChildModule) {
            rootDir = rootDir.substring(0, rootDir.lastIndexOf(File.separator));
        }

        VelocityContext context = new VelocityContext();
        context.put("table", table);
        context.put("constant", configuration.getConstantMap());

        List<String> tableTasks = table.getTasks();
        for (String tableTask : tableTasks) {
            Task task = configuration.getTask(tableTask);
            if (task == null) {
                continue;
            }
            task.setTableName(task.processTableName(table.getName()));
            context.put(task.getName(), task);

            executePlugin(configuration, context, task);

            String targetDir = rootDir + File.separator + task.getModule();
            String filePath = targetDir + task.getFileName();

            String templateText = FileUtils.readToString(task.getTemplate());

            StringWriter stringWriter = new StringWriter();
            Velocity.evaluate(context, stringWriter, "lit-code", templateText);
            String resultText = stringWriter.toString();
            FileUtils.writeToFile(resultText, filePath, overwrite);
        }
    }


    private static void executePlugin(Configuration configuration, Context context, Task task) {

        List<String> plugins = task.getPlugins();
        if (plugins == null || plugins.isEmpty()) {
            return;
        }

        for (String plugin : plugins) {
            PluginExecutor pluginExecutor = (PluginExecutor) ClassUtils.newInstance(plugin);
            pluginExecutor.execute(configuration, context, task);
        }
    }


}
