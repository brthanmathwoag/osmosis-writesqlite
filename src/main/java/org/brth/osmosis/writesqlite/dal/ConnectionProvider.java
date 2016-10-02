package org.brth.osmosis.writesqlite.dal;

import org.brth.osmosis.writesqlite.ResourceUtils;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionProvider implements AutoCloseable {
    private String databasePath;
    private Connection connection;
    private boolean useTransactions;

    public ConnectionProvider(String databasePath, boolean useTransactions) {
        this.databasePath = databasePath;
        this.useTransactions = useTransactions;
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
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath, config.toProperties());

        executeStatement("select load_extension('mod_spatialite')", false);
        beginTransactionIfNeeded();
    }

    public void close() throws SQLException, ClassNotFoundException {
        commitTransactionIfNeeded();
        ResourceUtils.closeSilently(connection);
        connection = null;
    }

    public void commitAndBeginTransaction() throws SQLException {
        commitTransactionIfNeeded();
        beginTransactionIfNeeded();
    }

    private void beginTransactionIfNeeded() throws SQLException {
        executeStatement("begin", true);
    }

    private void commitTransactionIfNeeded() throws SQLException {
        executeStatement("commit", true);
    }

    private void executeStatement(String sql, boolean onlyInTransaction) throws SQLException {
        if(onlyInTransaction && !useTransactions) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }
}
