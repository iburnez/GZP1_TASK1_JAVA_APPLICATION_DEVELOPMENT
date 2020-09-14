package model;

import Util.DBConnection;
import Util.TimeConverter;
import controller.Login_Controller;
import java.sql.*;
import java.time.ZonedDateTime;

public class Address{
    private int addressID;
    private int cityID;
    private String address1;
    private String address2;
    private String postalCode;
    private String phone;
    private String createdDate;
    private String createdBy;
    private String lastUpdated;
    private String lastUpdatedBy;

    public Address(int addressID, String address1, String address2, int cityID, String postalCode, String phone, String createdDate, String createdBy, String lastUpdated, String lastUpdatedBy){
        setAddressID(addressID);
        setAddress1(address1);
        setAddress2(address2);
        setCityID(cityID);
        setPostalCode(postalCode);
        setPhone(phone);
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

    public void setAddressID(int addressID){
        this.addressID = addressID;
    }

    public void setCityID(int cityID){
        this.cityID = cityID;
    }

    public void setAddress1(String address1){
        this.address1 = address1;
    }

    public void setAddress2(String address2){
        this.address2 = address2;
    }

    public void setPostalCode(String postalCode){
        this.postalCode = postalCode;
    }

    public void setPhone(String phone){
        this.phone = phone;
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

    public int getAddressID(){
        return addressID;
    }

    public int getCityID(){
        return cityID;
    }

    public String getAddress1(){
        return address1;
    }

    public String getAddress2(){
        return address2;
    }

    public String getPostalCode(){
        return postalCode;}

    public String getPhone(){
        return phone;
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
     * AUTO GENERATE ADDRESS ID
     * Search MySQL Database for max AddressID
     * Add 1 to MaxAddressID and return value
     * @return return the address ID generated
     */
    public static int autoGenID() {
        int addressID = 0;
        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.address;");

            while (resultSet.next()) {
                int sqlAddressID = resultSet.getInt("addressId");
                if (sqlAddressID > addressID) {
                    addressID = sqlAddressID;
                }
            }
        }catch (SQLException e){
            System.out.println("Auto Generate Address ID Error: " + e.getMessage());
        }
        ++addressID;
        return addressID;
    }

    /**
     * ______________________________________________________
     *                  ADDRESS SQL CONTROLS
     * ------------------------------------------------------
     */

    /**
     * ADD ADDRESS TO MySQL Database
     * compare parameters to existing entries in the DB
     * If params match an existing object in database
     * Return primary key for existing object
     * If the params do not match existing object in DB
     * Add new object and return primary key for new object
     * @param address1 - used to create or update address field in MySQL
     * @param address2 - used to create or update address1 field in MySQL
     * @param phone - used to create or update phone field in MySQL
     * @param postalCode - used to create or postalCode address field in MySQL
     * @param cityID - used to create or update city field in MySQL
     * @return - return primaryKey for address in MySQL
     */
    public static int addSQLAddress(String address1, String address2, String phone, String postalCode, int cityID) {
        int addressID = Address.autoGenID();
        String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
        String createdBy = Login_Controller.getUser();
        String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
        String lastUpdatedBy = Login_Controller.getUser();

        try {
            //Connect to DB and check if address exists in DB
            Connection connection = DBConnection.getConnection();

            //Insert New Address Into MySQL DB
            String addressQuery = "INSERT INTO U07RO2.address VALUES (" + addressID + "," + "\"" + address1 + "\"" + "," + "\"" + address2 + "\"" + "," + "\"" + cityID + "\"" + "," + "\"" + postalCode + "\"" + "," + "\"" + phone + "\"" + "," + "\"" + createdDate + "\"" + "," + "\"" + createdBy + "\"" + "," + "\"" + lastUpdated + "\"" + "," + "\"" + lastUpdatedBy + "\"" + ");";
            PreparedStatement addressPrepStatement = connection.prepareStatement(addressQuery);
            addressPrepStatement.execute();
            addressPrepStatement.close();
        }catch(SQLException e){
            System.out.println("Add ADDRESS Error: " + e.getMessage());
        }
        return addressID;
    }

    /**
     * UPDATE ADDRESS TO MySQL Database
     * compare parameters to existing entries in the DB
     * If params match an existing object in database
     * Return primary key for existing object
     * If the params do not match existing object in DB
     * Add new object and return primary key for new object
     * @param addressID - used to locate existing object or create new object
     * @param address1 - used to create or update address field in MySQL
     * @param address2 - used to create or update address1 field in MySQL
     * @param phone - used to create or update phone field in MySQL
     * @param postalCode - used to create or postalCode address field in MySQL
     * @param cityID - used to create or update city field in MySQL
     * @return - return primaryKey for address in MySQL
     */
    public static int updateSQLAddress(int addressID, String address1, String address2, String phone, String postalCode, int cityID) {
        try {
            //Connect to DB and check if address exists in DB
            Connection connection = DBConnection.getConnection();
            Statement addressStatement = connection.createStatement();
            ResultSet addressSet = addressStatement.executeQuery("SELECT * from U07RO2.address where addressId = " + addressID + ";");

            //if the address exists in DB update the fields and return primary key
            if (addressSet.next()) {
                String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String lastUpdatedBy = Login_Controller.getUser();

                //Insert New Address Into MySQL DB
                String addressQuery = "UPDATE U07RO2.address SET address = " + "\"" + address1 + "\"" + ", address2 = " + "\"" + address2 + "\"" + ", cityId = " + "\"" + cityID + "\"" + ", postalCode = " + "\"" + postalCode + "\"" + ", phone = " + "\"" + phone + "\"" + ", lastUpdate = "+ "\"" + lastUpdated + "\"" + ", lastUpdateBy = " + "\"" + lastUpdatedBy + "\"" + " WHERE addressId = " + addressID + ";";
                PreparedStatement addressPrepStatement = connection.prepareStatement(addressQuery);
                addressPrepStatement.execute();
                addressPrepStatement.close();
                addressStatement.close();

            //If address does not exist in DB create a new address and return primary key
            } else {
                //reset result set list to first entry
                addressID = Address.autoGenID();
                String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String createdBy = Login_Controller.getUser();
                String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String lastUpdatedBy = Login_Controller.getUser();

                //Insert New Address Into MySQL DB
                String addressQuery = "INSERT INTO U07RO2.address VALUES (" + addressID + "," + "\"" + address1 + "\"" + "," + "\"" + address2 + "\"" + "," + "\"" + cityID + "\"" + "," + "\"" + postalCode + "\"" + "," + "\"" + phone + "\"" + "," + "\"" + createdDate + "\"" + "," + "\"" + createdBy + "\"" + "," + "\"" + lastUpdated + "\"" + "," + "\"" + lastUpdatedBy + "\"" + ");";
                PreparedStatement addressPrepStatement = connection.prepareStatement(addressQuery);
                addressPrepStatement.execute();
                addressPrepStatement.close();
                addressStatement.close();
            }
        }catch(SQLException e){
            System.out.println("Update ADDRESS Error: " + e.getMessage());
        }
        return addressID;
    }
}
