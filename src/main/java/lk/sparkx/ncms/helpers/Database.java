package lk.sparkx.ncms.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connection
 */
public class Database {
    /**
     * Initialize a database connection
     *
     * @return Connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection open() throws SQLException, ClassNotFoundException {
        final String dbDriver = "com.mysql.cj.jdbc.Driver";
        final String dbURL = "jdbc:mysql://localhost:8080/";
        final String dbName = "ncms_db";
        final String dbUsername = "root";
        final String dbPassword = "";

        Class.forName(dbDriver);
        Connection con = DriverManager.getConnection(dbURL + dbName, dbUsername, dbPassword);
        return con;
    }
}
