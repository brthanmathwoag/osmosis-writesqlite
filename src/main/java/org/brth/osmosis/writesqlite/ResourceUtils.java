package org.brth.osmosis.writesqlite;

public class ResourceUtils {
    public static void closeSilently(AutoCloseable autoCloseable) {
        try {
            if(autoCloseable != null) {
                autoCloseable.close();
            }
        }
        catch(Exception exception) {
            ;
        }
    }
}
