package com.chatterbox.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class Base {
    public Connection connection;

    public void open(String driver, String url, String user, String password) {
        checkExistingConnection();

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new DBException("Failed to connect to JDBC URL: " + url, e);
        }
    }

    public void close() {
        try {
            if (connection == null) {
                throw new DBException("Cannot close the current connection because it is not available!");
            }
            connection.close();
        } catch (Exception e) {
            throw new DBException("Could not close connection! Must Investigate Potential Connection Leak!", e);
        }
    }

    public void checkExistingConnection() {
        if (connection != null) {
            throw new DBException("This instance of the Base class has open connection!");
        }
    }
}
