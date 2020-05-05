package com.lit.code.plugin;

import com.lit.code.config.Configuration;
import com.lit.code.context.Task;
import org.apache.velocity.context.Context;

import java.util.UUID;

/**
 * User : liulu
 * Date : 2018/3/16 13:37
 * version $Id: SerialVersionUIDPlugin.java, v 0.1 Exp $
 */
public class SerialVersionUIDPlugin implements PluginExecutor {

    @Override
    public void execute(Configuration configuration, Context context, Task task) {
        long serialVersionUID = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        context.put("serialVersionUID", -serialVersionUID);
    }

}
