package org.brth.osmosis.writesqlite;

import org.brth.osmosis.writesqlite.dal.ConnectionProvider;
import org.brth.osmosis.writesqlite.dal.EntityRepository;
import org.brth.osmosis.writesqlite.dal.RepositoryFactory;
import org.brth.osmosis.writesqlite.dal.SchemaHelper;
import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.Map;

public class WsTask implements Sink {
    private final boolean shouldRecreateSchema;
    private final long batchSize;

    private ConnectionProvider connectionProvider = null;
    private SchemaHelper schemaHelper = null;
    private RepositoryFactory repositoryFactory = null;

    private long currentIndex;

    public WsTask(
            String databasePath,
            boolean shouldRecreateSchema,
            long batchSize,
            SQLiteConfig.JournalMode journalMode,
            SQLiteConfig.SynchronousMode synchronousMode,
            int cacheSize) {

        this.shouldRecreateSchema = shouldRecreateSchema;
        this.batchSize = batchSize;

        connectionProvider = new ConnectionProvider(
            databasePath,
            batchSize != 0,
            journalMode,
            synchronousMode,
            cacheSize);
    }

    @Override
    public void process(EntityContainer entityContainer) {
        try {
            Entity entity = entityContainer.getEntity();
            EntityRepository repository = repositoryFactory.getRepository(entity.getType());
            repository.save(entity);
            if(batchSize > 0 && ++currentIndex == batchSize) {
                currentIndex = 0;
                connectionProvider.commitBatch();
            }
        } catch (SQLException exception) {
            throw new Error(exception);
        }
    }

    @Override
    public void initialize(Map<String, Object> metaData) {
        try {

            Connection connection = connectionProvider.getConnection();
            schemaHelper = new SchemaHelper(connection);

            if(shouldRecreateSchema) {
                schemaHelper.recreateSchema();
            }

            repositoryFactory = new RepositoryFactory(connection);
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }



    @Override
    public void complete() {
        try {
            if (shouldRecreateSchema) {
                schemaHelper.createIndices();
            }
        } catch (SQLException exception) {
            throw new Error(exception);
        }
    }

    private void closeConnection() {
        ResourceUtils.closeSilently(repositoryFactory);
        repositoryFactory = null;
        ResourceUtils.closeSilently(connectionProvider);
        connectionProvider = null;
    }

    @Override
    public void release() {
        closeConnection();
    }
}
