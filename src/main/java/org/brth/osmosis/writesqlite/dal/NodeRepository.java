package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NodeRepository extends EntityRepository<Node> implements AutoCloseable {
    private PreparedStatement saveEntityStatement;

    public NodeRepository(Connection connection) throws SQLException {
        super(connection);

        saveEntityStatement = connection.prepareStatement(
                "insert or ignore into nodes (id, version, user_id, tstamp, changeset_id, geom) values (?, ?, ?, ?, ?, MakePoint(?, ?, 4326))");
    }

    @Override
    protected String getEntityTableName() {
        return "nodes";
    }

    @Override
    protected String getTagsTableName() {
        return "node_tags";
    }

    @Override
    protected String getTagsIdColumnName() {
        return "node_id";
    }

    @Override
    protected void saveEntity(Node node) throws SQLException {
        saveEntityStatement.setLong(1, node.getId());
        saveEntityStatement.setInt(2, node.getVersion());
        saveEntityStatement.setInt(3, node.getUser().getId());
        saveEntityStatement.setLong(4, node.getTimestamp().getTime());
        saveEntityStatement.setLong(5, node.getChangesetId());
        saveEntityStatement.setDouble(6, node.getLatitude());
        saveEntityStatement.setDouble(7, node.getLongitude());
        saveEntityStatement.execute();
    }

    @Override
    public void close() throws SQLException {
        super.close();
        ResourceUtils.closeSilently(saveEntityStatement);
        saveEntityStatement = null;
    }
}
