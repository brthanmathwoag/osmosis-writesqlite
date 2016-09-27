package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WayRepository extends EntityRepository<Way> {
    private PreparedStatement saveReferencesStatement;

    public WayRepository(Connection connection) throws SQLException {
        super(connection);

        saveReferencesStatement = connection.prepareStatement(
                "insert into way_nodes (way_id, node_id, sequence_id) values (?, ?, ?)");
    }

    @Override
    protected String getEntityTableName() {
        return "ways";
    }

    @Override
    protected String getTagsTableName() {
        return "way_tags";
    }

    @Override
    protected String getTagsIdColumnName() {
        return "way_id";
    }

    @Override
    protected void saveReferences(Way way) throws SQLException {
        int sequenceId = 0;
        for (WayNode wayNode : way.getWayNodes()) {
            saveReferencesStatement.setLong(1, way.getId());
            saveReferencesStatement.setLong(2, wayNode.getNodeId());
            saveReferencesStatement.setInt(3, sequenceId++);
            saveReferencesStatement.execute();
        }
    }

    @Override
    public void close() throws SQLException {
        super.close();
        ResourceUtils.closeSilently(saveReferencesStatement);
        saveReferencesStatement = null;
    }
}