package org.brth.osmosis.writesqlite.dal;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class SchemaHelper {

    private final String SCHEMA_CREATION_SCRIPT_PATH = "scripts/00-create-schema.sql";
    private final String INDICES_CREATION_SCRIPT_PATH = "scripts/01-enable-indices.sql";

    private final String databasePath;

    public SchemaHelper(String databasePath) {
        this.databasePath = databasePath;
    }

    public void recreateSchema() throws IOException, InterruptedException  {
        runSpatialite(SCHEMA_CREATION_SCRIPT_PATH);
    }

    public void createIndices() throws IOException, InterruptedException {
        runSpatialite(INDICES_CREATION_SCRIPT_PATH);
    }

    private void runSpatialite(String scriptPath) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(new String[] { "spatialite", databasePath });

        try (
                Scanner processOutput = new Scanner(process.getErrorStream());
                PrintWriter processInput = new PrintWriter(process.getOutputStream());
                Scanner scriptStream = new Scanner(getClass().getClassLoader().getResourceAsStream(scriptPath))
        ) {
            while (scriptStream.hasNextLine()) {
                String line = scriptStream.nextLine();
                processInput.write(line + "\n");
            }
            processInput.close();
            int exitCode = process.waitFor();
            while(processOutput.hasNextLine()) {
                System.err.println(processOutput.nextLine());
            }

            if(exitCode != 0) {
                throw new Error("Process exited with an error: " + exitCode);
            }
        }
    }
}
