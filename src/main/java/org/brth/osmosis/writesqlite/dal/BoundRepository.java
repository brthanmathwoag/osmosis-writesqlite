package org.brth.osmosis.writesqlite.dal;

import org.openstreetmap.osmosis.core.domain.v0_6.Bound;

import java.sql.Connection;
import java.sql.SQLException;

public class BoundRepository extends EntityRepository<Bound> implements AutoCloseable {

    public BoundRepository(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    protected String getEntityTableName() {
        return null;
    }

    @Override
    public void close() throws SQLException {
        super.close();
    }

    @Override
    public void save(Bound bound) throws SQLException {
    }
}
