package org.brth.osmosis.writesqlite;

import org.openstreetmap.osmosis.core.pipeline.common.TaskConfiguration;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManager;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;
import org.openstreetmap.osmosis.core.pipeline.v0_6.SinkManager;
import org.sqlite.SQLiteConfig;

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

        long batchSize = getIntegerArgument(taskConfig, "batchSize", -1);

        SQLiteConfig.JournalMode journalMode = SQLiteConfig.JournalMode.valueOf(
                getStringArgument(taskConfig, "journalMode", "DELETE"));

        SQLiteConfig.SynchronousMode synchronousMode = SQLiteConfig.SynchronousMode.valueOf(
                getStringArgument(taskConfig, "synchronousMode", "FULL"));

        int cacheSize = getIntegerArgument(taskConfig, "cacheSize", 0);

        return new SinkManager(
            taskConfig.getId(),
            new WsTask(
                databasePath,
                shouldRecreateSchema,
                batchSize,
                journalMode,
                synchronousMode,
                cacheSize),
            taskConfig.getPipeArgs());
    }

}
