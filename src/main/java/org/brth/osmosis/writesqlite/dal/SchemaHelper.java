package org.brth.osmosis.writesqlite.dal;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SchemaHelper {

    private final String SCHEMA_CREATION_SCRIPT_PATH = "scripts/00-create-schema.sql";
    private final String INDICES_CREATION_SCRIPT_PATH = "scripts/01-enable-indices.sql";

    private final Connection connection;

    public SchemaHelper(Connection connection) {
        this.connection = connection;
    }

    public void recreateSchema() throws SQLException  {
        runScript(SCHEMA_CREATION_SCRIPT_PATH);
    }

    public void createIndices() throws SQLException {
        runScript(INDICES_CREATION_SCRIPT_PATH);
    }

    private void runScript(String resourcePath) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            for(String sql : getStatementsFromResource(resourcePath)) {
                statement.execute(sql);
            }
        }
    }

    private String[] getStatementsFromResource(String resourcePath) {
        try (Scanner scriptStream = new Scanner(getClass().getClassLoader().getResourceAsStream(resourcePath))) {
            StringBuilder stringBuilder = new StringBuilder();
            while(scriptStream.hasNext()) {
                String line = scriptStream.nextLine();
                int commentStartPosition = line.indexOf("--");
                if (commentStartPosition >= 0) {
                    line = line.substring(0, commentStartPosition).trim();
                }
                if(line.length() > 0) {
                    stringBuilder.append(line);
                    stringBuilder.append(' ');
                }
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            return stringBuilder.toString().split(";");
        }
    }
}
