package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class EntityRepository<T extends Entity> implements AutoCloseable  {
    private PreparedStatement saveUserStatement;
    private PreparedStatement saveTagStatement;
    private PreparedStatement saveEntityStatement;

    public EntityRepository(Connection connection) throws SQLException {
        saveUserStatement = connection.prepareStatement(
                "insert or ignore into users (id, name) values (?, ?)");

        String tagsTableName = getTagsTableName();
        String tagsIdColumnName = getTagsIdColumnName();
        if(tagsTableName != null) {
            saveTagStatement = connection.prepareStatement(
                    String.format(
                            "insert or ignore into %s (%s, k, v) values (?, ?, ?)",
                            tagsTableName,
                            tagsIdColumnName));
        }

        String entityTableName = getEntityTableName();
        if(entityTableName != null) {
            saveEntityStatement = connection.prepareStatement(
                    String.format(
                            "insert  or ignore into %s (id, version, user_id, tstamp, changeset_id) values (?, ?, ?, ?, ?)",
                            getEntityTableName()));
        }
    }

    protected abstract String getEntityTableName();

    protected String getTagsTableName() {
        return null;
    }

    protected String getTagsIdColumnName() {
        return null;
    }

    protected void saveReferences(T entity) throws SQLException {
    }

    public void close() throws SQLException {
        ResourceUtils.closeSilently(saveUserStatement);
        saveUserStatement = null;

        ResourceUtils.closeSilently(saveTagStatement);
        saveTagStatement = null;

        ResourceUtils.closeSilently(saveEntityStatement);
        saveEntityStatement = null;
    }

    public void save(T entity) throws SQLException  {
        saveUser(entity);
        saveEntity(entity);
        saveTags(entity);
        saveReferences(entity);
    }

    protected void saveEntity(T entity) throws SQLException {
        if(saveEntityStatement == null) {
            return;
        }
        saveEntityStatement.setLong(1, entity.getId());
        saveEntityStatement.setInt(2, entity.getVersion());
        saveEntityStatement.setInt(3, entity.getUser().getId());
        saveEntityStatement.setLong(4, entity.getTimestamp().getTime());
        saveEntityStatement.setLong(5, entity.getChangesetId());
        saveEntityStatement.execute();
    }

    private void saveTags(T entity) throws SQLException {
        if(saveTagStatement == null) {
            return;
        }
        for(Tag tag : entity.getTags()) {
            saveTagStatement.setLong(1, entity.getId());
            saveTagStatement.setString(2, tag.getKey());
            saveTagStatement.setString(3, tag.getValue());
            saveTagStatement.execute();
        }
    }

    private void saveUser(T entity) throws SQLException {
        saveUserStatement.setInt(1, entity.getUser().getId());
        saveUserStatement.setString(2, entity.getUser().getName());
        saveUserStatement.execute();
    }
}
