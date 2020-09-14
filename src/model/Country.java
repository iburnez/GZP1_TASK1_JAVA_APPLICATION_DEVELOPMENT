package model;

import Util.DBConnection;
import Util.TimeConverter;
import controller.Login_Controller;
import java.sql.*;
import java.time.ZonedDateTime;

public class Country {
    private int countryID;
    private String countryName;
    private String createdDate;
    private String createdBy;
    private String lastUpdated;
    private String lastUpdatedBy;

    /**
     * DEFAULT CONSTRUCTOR
     * @param countryID - set countryID
     * @param countryName - set countryName
     * @param createdBy - set createdBy
     * @param lastUpdatedBy - set lastUpdatedBy
     * @param createdDate - set CreatedDate
     * @param lastUpdated - set LastUpdatedDate
     */
    public Country(int countryID, String countryName, String createdDate, String createdBy, String lastUpdated, String lastUpdatedBy){
        setCountryName(countryName);
        setCountryID(countryID);
        setCreatedDate(createdDate);
        setCreatedBy(createdBy);
        setLastUpdated(lastUpdated);
        setLastUpdatedBy(lastUpdatedBy);
    }

    /**
     * ______________________________________________________
     *                         SETTERS
     * ------------------------------------------------------
     */

    public void setCountryID(int countryID){
        this.countryID = countryID;
    }

    public void setCountryName(String countryName){
        this.countryName = countryName;
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

    public int getCountryID(){
        return countryID;
    }

    public String getCountryName(){
        return countryName;
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
     *                      AUTO GEN ID
     * ------------------------------------------------------
     */

    /**
     * AUTOGENERATE COUNTRY ID                  <br>
     * Search MySQL Database for max CountryID  <br>
     * Add 1 to MaxCountryID and return value   <br>
     * @return unique country ID
     */
    public static int autoGenID() throws SQLException {
        int countryID = 0;

        Connection connection = DBConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.country;");

        //Search MySQL Database for max CountryID
        while(resultSet.next()){
            int sqlCountryID = resultSet.getInt("countryId");
            if(sqlCountryID > countryID){
                countryID = sqlCountryID;
            }
        }

        //Add 1 to MaxCountryID and return value
        return ++countryID;
    }

    /**
     * ______________________________________________________
     *                ADD / UPDATE COUNTRY SQL
     * ------------------------------------------------------
     */

    /**
     * ADD COUNTRY TO MySQL Database                                    <br>
     * -----------------------------------------------------------------<br>
     * compare parameters to existing entries in the DB                 <br>
     * If params match an existing object in database                   <br>
     * Return primary key for existing object                           <br>
     * If the params do not match existing object in DB                 <br>
     * Add new object and return primary key for new object             <br>
     * @param countryName create or update countryName field in MySQL
     * @return return primaryKey for address in MySQL
     */
    public static int addSQLCountry(String countryName) {
        int countryID = -1;
        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM U07RO2.country where country = " + "\"" + countryName + "\"" + ";");

            //If country does not exist in DB create a new address and return primary key
            if (!(resultSet.next())) {
                countryID = Country.autoGenID();
                String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String createdBy = Login_Controller.getUser();
                String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String lastUpdatedBy = Login_Controller.getUser();

                //Insert New Country Into MySQL DB
                String countryQuery = "INSERT INTO U07RO2.country VALUES (" + countryID + "," + "\"" + countryName + "\"" + "," + "\"" + createdDate + "\"" + "," + "\"" + createdBy + "\"" + "," + "\"" + lastUpdated + "\"" + "," + "\"" + lastUpdatedBy + "\"" + ");";
                PreparedStatement countryPrepStatement = connection.prepareStatement(countryQuery);
                countryPrepStatement.execute();
                countryPrepStatement.close();
                resultSet.close();

            //if the country exists in DB update the fields and return primary key
            } else {
                //reset result set list to first entry
                resultSet.beforeFirst();

                //Set countryID to existing  countryID found in search
                resultSet.next();
                countryID = resultSet.getInt("countryId");
                resultSet.close();
            }
        }catch (SQLException e){
            System.out.println("Add COUNTRY Error: " + e.getMessage());
        }
        return countryID;
    }

}
