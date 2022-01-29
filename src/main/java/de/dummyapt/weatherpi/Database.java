package de.dummyapt.weatherpi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String SERVER = "weatherpi";
    private static final String PORT = "3306";
    private static final String USER = "java";
    private static final String PASS = "G1M1RU";
    private static final String DB = "weatherpi";
    private static Connection dbConnection;

    private Database() {
        throw new IllegalStateException("Utility class");
    }

    static Connection getConnection() throws SQLException {
        if (dbConnection == null)
            dbConnection = DriverManager.getConnection("jdbc:mariadb://" + SERVER + ":" + PORT + "/" + DB, USER, PASS);
        return dbConnection;
    }
}
