package org.brth.osmosis.writesqlite;

import org.openstreetmap.osmosis.core.plugin.PluginLoader;
import org.openstreetmap.osmosis.core.pipeline.common.TaskManagerFactory;

import java.util.HashMap;
import java.util.Map;

public class WsPluginLoader implements PluginLoader {
    @Override
    public Map<String, TaskManagerFactory> loadTaskFactories() {
        HashMap<String, TaskManagerFactory> map = new HashMap<String, TaskManagerFactory>();
        WsTaskManagerFactory factory = new WsTaskManagerFactory();
        map.put("write-sqlite", factory);
        return map;
    }
}
