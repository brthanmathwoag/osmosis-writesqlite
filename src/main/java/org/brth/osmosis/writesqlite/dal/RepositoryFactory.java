package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;

import java.sql.Connection;
import java.sql.SQLException;

public class RepositoryFactory implements AutoCloseable {
    private NodeRepository nodeRepository;
    private WayRepository wayRepository;
    private RelationRepository relationRepository;
    private BoundRepository boundRepository;

    public RepositoryFactory(Connection connection) throws SQLException {
        nodeRepository = new NodeRepository(connection);
        wayRepository = new WayRepository(connection);
        relationRepository = new RelationRepository(connection);
        boundRepository = new BoundRepository(connection);
    }

    public void close() throws SQLException {
        ResourceUtils.closeSilently(nodeRepository);
        nodeRepository = null;

        ResourceUtils.closeSilently(wayRepository);
        wayRepository = null;

        ResourceUtils.closeSilently(relationRepository);
        relationRepository = null;

        ResourceUtils.closeSilently(boundRepository);
        boundRepository = null;
    }

    public EntityRepository getRepository(EntityType entityType) {
        switch(entityType) {
            case Node:
                return nodeRepository;
            case Way:
                return wayRepository;
            case Relation:
                return relationRepository;
            case Bound:
                return boundRepository;
            default:
                throw new Error("Unsupported entity type: " + entityType);
        }
    }
}

