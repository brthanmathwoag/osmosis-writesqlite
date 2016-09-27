package org.brth.osmosis.writesqlite;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;

import java.io.File;
import java.util.Map;

public class WsTaskManagerFactory extends TaskManagerFactory {
    @Override
    protected TaskManager createTaskManagerImpl(TaskConfiguration taskConfig) {
        String databasePath = taskConfig.getDefaultArg();
        if(databasePath == null) {
            throw new Error("Output file path is required.");
        }

        boolean shouldRecreateSchema =
            getBooleanArgument(taskConfig, "recreateSchema", false) || !new File(databasePath).exists();

        return new SinkManager(
            taskConfig.getId(),
            new WsTask(databasePath, shouldRecreateSchema),
            taskConfig.getPipeArgs());
    }
}
