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

    public ConnectionProvider(String databasePath) {
        this.databasePath = databasePath;
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

        try (Statement statement = connection.createStatement()) {
            statement.execute("select load_extension('mod_spatialite')");
            statement.execute("begin");
        }
    }

    public void close() throws SQLException, ClassNotFoundException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("commit");
        }
        ResourceUtils.closeSilently(connection);
        connection = null;
    }
}
