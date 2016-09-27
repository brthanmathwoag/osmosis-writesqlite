package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RelationRepository extends EntityRepository<Relation> implements AutoCloseable {
    private PreparedStatement saveReferencesStatement;

    public RelationRepository(Connection connection) throws SQLException {
        super(connection);

        saveReferencesStatement = connection.prepareStatement(
                "insert into relation_members (relation_id, member_id, member_type, member_role, sequence_id) values (?, ?, ?, ?, ?)");
    }

    @Override
    protected String getEntityTableName() {
        return "relations";
    }

    @Override
    protected String getTagsTableName() {
        return "relation_tags";
    }

    @Override
    protected String getTagsIdColumnName() {
        return "relation_id";
    }

    @Override
    protected void saveReferences(Relation relation) throws SQLException {
        int sequenceId = 0;
        for (RelationMember relationMember : relation.getMembers()) {
            saveReferencesStatement.setLong(1, relation.getId());
            saveReferencesStatement.setLong(2, relationMember.getMemberId());
            saveReferencesStatement.setString(3, relationMember.getMemberType().toString());
            saveReferencesStatement.setString(4, relationMember.getMemberRole());
            saveReferencesStatement.setInt(5, sequenceId++);
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