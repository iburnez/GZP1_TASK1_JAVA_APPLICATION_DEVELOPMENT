package model;

import Util.DBConnection;
import com.sun.glass.ui.EventLoop;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private static ObservableList<User> allUsers = FXCollections.observableArrayList();
    private int userID;
    private String userName;
    private String password;
    private int active;
    private String createdDate;
    private String createdBy;
    private String lastUpdated;
    private String lastUpdatedBy;

    /**
     * CONSTRUCTOR TO ADD TO SQL
     * @param userID sets userID
     * @param userName sets userName
     * @param password sets password
     * @param active sets active
     * @param createdDate sets createdDate
     * @param createdBy sets createdBy
     * @param lastUpdated sets lastUpdated
     * @param lastUpdatedBy sets lastUpdatedBy
     */
    public User(int userID, String userName, String password, int active, String createdDate, String createdBy, String lastUpdated, String lastUpdatedBy){
        setUserID(userID);
        setUserName(userName);
        setPassword(password);
        setActive(active);
        setCreatedDate(createdDate);
        setCreatedBy(createdBy);
        setLastUpdated(lastUpdated);
        setLastUpdatedBy(lastUpdatedBy);
    }

    /**
     * CONSTRUCTOR TO ADD TO OL                             <br>
     * Do not want to include password for security reasons.<br>
     * Do not need creation or udate info                   <br>
     * @param userID add userID to OL
     * @param userName add userName to OL
     */
    public User(int userID, String userName){
        setUserID(userID);
        setUserName(userName);
    }

    /**
     * ______________________________________________________
     *                         SETTERS
     * ------------------------------------------------------
     */

    public void setUserID(int userID){
        this.userID = userID;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setActive(int active){
        this.active = active;
    }

    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy){
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setCreatedDate(String createdDate){
        this.createdDate = createdDate;
    }

    public void setLastUpdated(String lastUpdated){
        this.lastUpdated = lastUpdated;
    }

    /**
     * ______________________________________________________
     *                         GETTERS
     * ------------------------------------------------------
     */

    public int getUserID(){
        return userID;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public int getActive(){
        return active;
    }

    public String getCreatedBy(){
        return createdBy;
    }

    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }

    public String getCreatedDate(){
        return createdDate;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    /**
     * ______________________________________________________
     *                 OBSERVABLE LIST CONTROLS
     * ------------------------------------------------------
     */

    /**
     * ADD USER TO OL
     * @param newUser user to be added to OL
     */
    public static void addAllUsers(User newUser){
        allUsers.add(newUser);
    }

    /**
     * BUILD ALLUSERS OBSERVABLE LIST                                           <br>
     * Connect to SQL DB and add User and UserID to OL with addAllUsersMethod
     */
    public static void buildAllUsers(){
        if(!(allUsers.isEmpty())){
            allUsers.clear();
        }

        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT userId, userName, active FROM U07RO2.user;");

            while(resultSet.next()){
                int userID = resultSet.getInt("userId");
                String userName = resultSet.getString("userName");
                int active = resultSet.getInt("active");

                //Only add active users to OL
                if(active != 0){
                    addAllUsers(new User(userID, userName));
                }
            }

        }catch(SQLException e){
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("User Class Error: " + e.getMessage());
        }
    }

    /**
     * GET ALL USERS
     * @return allUsers OL
     */
    public static ObservableList<User> getAllUsers(){
        return allUsers;
    }



    /**
     * ______________________________________________________
     *                      AUTO GEN ID
     * ------------------------------------------------------
     */

    /**
     *AUTOGENERATE USER ID                  <br>
     * Search MySQL Database for max UserID <br>
     * Add 1 to MaxUserID and return value  <br>
     * @return unique UserID
     */
    public static int autoGenID() throws SQLException {
        int userID = 0;

        Connection connection = DBConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.user;");

        //Search MySQL Database for max UserID
        while(resultSet.next()){
            int sqlUserID = resultSet.getInt("userId");
            if(sqlUserID > userID){
                userID = sqlUserID;
            }
        }

        //Add 1 to MaxUserID and return value
        return ++userID;
    }

}
