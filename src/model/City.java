package model;

import Util.DBConnection;
import Util.TimeConverter;
import controller.Login_Controller;
import java.sql.*;
import java.time.ZonedDateTime;

public class City{
    private int cityID;
    private String cityName;
    private int countryID;
    private String createdDate;
    private String createdBy;
    private String lastUpdated;
    private String lastUpdatedBy;

    /**
     * DEFAULT CONSTRUCTOR
     * call super constructor from Country
     * set cityID and City manually
     * @param cityID     - set cityID
     * @param cityName     - set cityName
     * @param countryID     - set countryID
     * @param createdBy     - set createdBy
     * @param lastUpdatedBy - set lastUpdatedBy
     * @param createdDate   - set CreatedDate
     * @param lastUpdated   - set LastUpdatedDate
     */
    public City(int cityID, String cityName, int countryID, String createdDate, String createdBy, String lastUpdated, String lastUpdatedBy){
        setCityName(cityName);
        setCityID(cityID);
        setCountryID(countryID);
        setCreatedDate(createdDate);
        setCreatedBy(createdBy);
        setLastUpdatedBy(lastUpdatedBy);
        setLastUpdated(lastUpdated);
    }

    /**
     * ______________________________________________________
     *                         SETTERS
     * ------------------------------------------------------
     */

    public void setCityID(int cityID){
        this.cityID = cityID;
    }

    public void setCityName(String cityName){
        this.cityName = cityName;
    }

    public void setCountryID(int countryID){
        this.countryID = countryID;
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

    public int getCityID(){
        return cityID;
    }

    public String getCityName(){
        return cityName;
    }

    public int getCountryID(){
        return countryID;
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
     * AUTOGENERATE CITY ID                 <br>
     * Search MySQL Database for max CityID <br>
     * Add 1 to MaxCityID and return value  <br>
     * @return unique city ID
     */
    public static int autoGenID() throws SQLException {
        int cityID = 0;

        Connection connection = DBConnection.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.city;");

        //Search MySQL Database for max CityID
        while(resultSet.next()){
            int sqlCityID = resultSet.getInt("cityId");
            if(sqlCityID > cityID){
                cityID = sqlCityID;
            }
        }

        //Add 1 to MaxCityID and return value
        return ++cityID;
    }

    /**
     * ______________________________________________________
     *                 CITY SQL CONTROLS
     * ------------------------------------------------------
     */

    /**
     * ADD CITY TO MySQL Database                                   <br>
     * -------------------------------------------------------------<br>
     * compare parameters to existing entries in the DB             <br>
     * If params match an existing object in database               <br>
     * Return primary key for existing object                       <br>
     * If the params do not match existing object in DB             <br>
     * Add new object and return primary key for new object         <br>
     * @param cityName create or update countryName field in MySQL
     * @param countryID create or update countryID field in MySQL
     * @return return primaryKey for address in MySQL
     */
    public static int addSQLCity(String cityName, int countryID) {
        int cityID = -1;
        try {
            Connection connection = DBConnection.getConnection();
            Statement cityStatement = connection.createStatement();
            ResultSet citySet = cityStatement.executeQuery("SELECT * from U07RO2.city where city = " + "\"" + cityName + "\"" + " and countryId = " + countryID + ";");

            //If country does not exist in DB create a new address and return primary key
            if (!(citySet.next())) {
                cityID = City.autoGenID();
                String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String createdBy = Login_Controller.getUser();
                String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String lastUpdatedBy = Login_Controller.getUser();

                String cityQuery = "INSERT INTO U07RO2.city VALUES (" + cityID + "," + "\"" + cityName + "\"" + "," + countryID + "," + "\"" + createdDate + "\"" + "," + "\"" + createdBy + "\"" + "," + "\"" + lastUpdated + "\"" + "," + "\"" + lastUpdatedBy + "\"" + ");";
                PreparedStatement cityPrepStatement = connection.prepareStatement(cityQuery);
                cityPrepStatement.execute();
                cityPrepStatement.close();
                citySet.close();

            //if the country exists in DB update the fields and return primary key
            } else {
                //reset result set list to first entry
                citySet.beforeFirst();

                //Set cityID to existing  cityID found in search
                citySet.next();
                cityID = citySet.getInt("cityId");
                citySet.close();
            }
        }catch(SQLException e){
            System.out.println("Add CITY Error: " + e.getMessage());
        }
        return cityID;
    }

}
