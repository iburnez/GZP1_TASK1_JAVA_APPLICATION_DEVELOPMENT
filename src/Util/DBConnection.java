package Util;

import javafx.scene.control.Alert;
import java.sql.*;

/**
 * _____________________________________________________ <br>
 *              DATABASE CONNECTION METHOD <br>
 * ----------------------------------------------------- <br>
 * This method is responsible for the following: <br>
 * Opening a connection to the MySQL Database <br>
 * Closing the connection to the mySQL Database <br>
 */
public class DBConnection {

    /**
     * Database Connection Variables            <br>
     * Used to create database connection URL   <br>
     */
    private static final String port = "3306";
    private static final String dbName = "U07RO2";
    private static final String userName = "U07RO2";
    private static final String password = "53689108866";
    private static final String dbURL = "jdbc:mysql://3.227.166.251:3306/" +
                                        userName + "?useSSL=false";
    private static final String dbDriver = "com.mysql.jdbc.Driver";
    private static Connection connection;

    /**
     * OPEN CONNECTION TO MySQL DATABASE                                <br>
     * Create new connection with WGU_C195_PA database                  <br>
     * Handle ClassNotFoundException & SQLException by displaying alert
     * message with error message.                                      <br>
     * @return connection create connection to db and return connection to calling method.
     */
    public static Connection openConnection() {
        try {
            Class.forName(dbDriver);
            connection = DriverManager.getConnection(dbURL, dbName, password);

        } catch(ClassNotFoundException | SQLException e) {
            Alert failAlert = new Alert(Alert.AlertType.ERROR);
            failAlert.setTitle("Failed To Connect");
            failAlert.setHeaderText("Could not connect to database");
            failAlert.setContentText("Check Database connection settings: " + e.getMessage());
            failAlert.show();
        }
        return connection;
    }

    /**
     * CLOSE CONNECTION TO MySQL DATABASE                               <br>
     * Close connection to WGU_C195_PA database                         <br>
     * Handle ClassNotFoundException & SQLException by displaying alert
     * message with error message                                       <br>
     * @return connection closed
     */
    public static Connection closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed: " + connection);
        }catch (SQLException e) {
            Alert failAlert = new Alert(Alert.AlertType.ERROR);
            failAlert.setTitle("Failed To Close Connection");
            failAlert.setHeaderText("Could not close connection to database");
            failAlert.setContentText("Check Database connection settings: " + e.getMessage());
            failAlert.show();
        }
        return connection;
    }

    /**
     * GET CONNECTION
     * @return connection
     */
    public static Connection getConnection(){
        return connection;
    }
}
