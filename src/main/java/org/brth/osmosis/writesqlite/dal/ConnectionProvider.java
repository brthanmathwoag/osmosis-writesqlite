package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionProvider implements AutoCloseable {
    private final String databasePath;
    private final boolean useTransactions;
    private final SQLiteConfig.JournalMode journalMode;
    private final SQLiteConfig.SynchronousMode synchronousMode;
    private final int cacheSize;
    private Connection connection;

    public ConnectionProvider(
            String databasePath,
            boolean useTransactions,
            SQLiteConfig.JournalMode journalMode,
            SQLiteConfig.SynchronousMode synchronousMode,
            int cacheSize) {

        this.databasePath = databasePath;
        this.useTransactions = useTransactions;
        this.journalMode = journalMode;
        this.cacheSize = cacheSize;
        this.synchronousMode = synchronousMode;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if(connection == null) {
            openConnection();
        }
        return connection;
    }

    private void openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        SQLiteConfig config = new SQLiteConfig();
        config.enableLoadExtension(true);
        config.setJournalMode(journalMode);
        config.setSynchronous(synchronousMode);
        if (cacheSize > 0) {
            config.setCacheSize(cacheSize);
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath, config.toProperties());

        executeStatement("select load_extension('mod_spatialite')");
        if (useTransactions) {
            connection.setAutoCommit(false);
        }
    }

    public void close() throws SQLException, ClassNotFoundException {
        commitBatch();
        ResourceUtils.closeSilently(connection);
        connection = null;
    }

    public void commitBatch() throws SQLException {
        if (useTransactions) {
            connection.commit();
        }
    }

    private void executeStatement(String sql) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
