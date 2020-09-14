package model;

import Util.DBConnection;
import Util.LogFile;
import Util.TimeConverter;
import controller.Login_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer {
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private int customerID;
    private String customerName;
    private int addressID;
    private int active;
    private String createdDate;
    private String createdBy;
    private String lastUpdated;
    private String lastUpdatedBy;

    public Customer(int customerID, String customerName, int addressID, int active, String createdDate, String createdBy, String lastUpdated, String lastUpdatedBy){
        setCustomerID(customerID);
        setCustomerName(customerName);
        setAddressID(addressID);
        setActive(active);
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

    public void setCustomerID(int customerID){
        this.customerID = customerID;
    }

    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }

    public void setAddressID(int addressID){
        this.addressID = addressID;
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

    public int getCustomerID(){
        return customerID;
    }

    public String getCustomerName(){
        return customerName;
    }

    public int getAddressID(){
        return addressID;
    }

    public int getActive(){
        return active;
    }

    public String getCreatedDate(){
        return TimeConverter.utcToZone(createdDate);
    }

    public String getCreatedBy(){
        return createdBy;
    }

    public String getLastUpdated() {
        return TimeConverter.utcToZone(lastUpdated);
    }

    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }

    public static Customer getCustomerByID(int customerID){
        Customer customer = null;
        for(int i = 0; i < getAllCustomers().size(); i++){
            if(getAllCustomers().get(i).getCustomerID() == customerID)
                customer = getAllCustomers().get(i);
        }
        return customer;
    }

    /**
     * ______________________________________________________
     *          CUSTOMERS OBSERVABLE LIST CONTROLS
     * ------------------------------------------------------
     */

    /**
     * GET ALL CUSTOMER OL
     * @return return allCustomer OL
     */
    public static ObservableList<Customer> getAllCustomers(){
        return allCustomers;
    }

    /**
     * BUILD ALL CUSTOMER LIST OBSERVABLE LIST FROM MySQL DB <br>
     * ---------------------------------------------------------------------------- |<br>
     * Check if list has entries and clear or duplicates will be created on each    |<br>
     * screen load.                                                                 |<br>
     * Create a connection to the database                                          |<br>
     * Select all customers from appointment table                                  |<br>
     * Loop through results and use each field entry to populate Customer Object    |<br>
     * Save each customers object to Observable list                                |<br>
     * If an SQL exception display error in console                                 |<br>
     */
    public static void buildAllCustomers(){
        /**
         * Clear customer list each build to prevent duplicate entries.
         */
        if(!(allCustomers.isEmpty())){
            allCustomers.clear();
        }

        /**
         * Get open connection
         */
        Connection connection = DBConnection.getConnection();

        try {
            /**
             * Create and execute select statement for customer table of DB
             */
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM U07RO2.customer;");

            /**
             * Loop through result set and save fields to new variables.
             * Call customer constructor to create new object using variables
             * Save new object to allCustomer OL
             */
            while(resultSet.next()){

                int customerID = resultSet.getInt("customerId");
                String customerName = resultSet.getString("customerName");
                int addressID = resultSet.getInt("addressId");
                int active = resultSet.getInt("active");
                String createdDate = resultSet.getString("createDate");
                String createdBy = resultSet.getString("createdBy");
                String lastUpdated = resultSet.getString("lastUpdate");
                String lastUpdatedBy = resultSet.getString("lastUpdateBy");

                /**
                 * Create new Customer Object by calling constructor
                 * Add new Customer object to allCustomers OL
                 */
                allCustomers.add(new Customer(customerID, customerName, addressID, active, createdDate, createdBy, lastUpdated, lastUpdatedBy));
            }//end while statement
            /**
             * CATCH CLAUSE - catch SQLException and display error message in console.
             */
        }catch(SQLException e){
            System.out.println("Error: " + e.getMessage());
        }//end catch statement
    }

    /**
     * ______________________________________________________
     *                      AUTO GEN ID
     * ------------------------------------------------------
     */

    /**
     * AUTO GENERATE CUSTOMER ID                <br>
     * Search MySQL Database for max CustomerID <br>
     * Add 1 to MaxCustID and return value      <br>
     * @return return the customer ID generated
     */
    public static int autoGenID() {

        int customerID = 0;

        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.customer;");

            //Search MySQL Database for max CustomerID
            while (resultSet.next()) {
                int sqlCustomerID = resultSet.getInt("customerId");
                if (sqlCustomerID > customerID) {
                    customerID = sqlCustomerID;
                }
            }
        }catch (SQLException e){
            System.out.println("Auto Generate Customer ID error: " + e.getMessage());
        }

        //Add 1 to MaxCustID and return value
        return ++customerID;
    }

    /**
     * ______________________________________________________
     *                 CUSTOMER SQL CONTROLS
     * ------------------------------------------------------
     */

    /**
     * ADD CUSTOMER TO MySQL Database                                                   <br>
     * ---------------------------------------------------------------------------------<br>
     * compare parameters to existing entries in the DB                                 <br>
     * If params match an existing object in database                                   <br>
     * Return primary key for existing object                                           <br>
     * If the params do not match existing object in DB                                 <br>
     * Add new object and return primary key for new object                             <br>
     * @param customerName - used to located existing customer or create new customer
     * @param addressID - used to link to existing address in DB
     * @param active - marks the customer as active or inactive
     */
    public static void addSQLCustomer(String customerName, int addressID, int active) {
        try {
            int customerID;
            Connection connection = DBConnection.getConnection();
            Statement customerStatement = connection.createStatement();
            ResultSet customerSet = customerStatement.executeQuery("SELECT * from U07RO2.customer where customerName = " + "\"" + customerName + "\"" + "AND addressId = " + addressID + ";");

            if (!(customerSet.next())) {
                customerID = Customer.autoGenID();
                String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String createdBy = Login_Controller.getUser();
                String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
                String lastUpdatedBy = Login_Controller.getUser();

                //Insert New Customer Into MySQL DB
                String customerQuery = "INSERT INTO U07RO2.customer VALUES (" + customerID + ", " + "\"" + customerName + "\"" + ", " + addressID + ", " + active + ", " + "\"" + createdDate + "\"" + ", " + "\"" + createdBy + "\"" + ", " + "\"" + lastUpdated + "\"" + ", " + "\"" + lastUpdatedBy + "\"" + ");";
                PreparedStatement customerPrepStatement = connection.prepareStatement(customerQuery);
                customerPrepStatement.execute();

                //Log activity to logfile
                LogFile.writeToLog(Login_Controller.getUser(), " Add New Customer Successful: Customer ID ", String.valueOf(customerID));
            }

        }catch (SQLException e){
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * UPDATE CUSTOMER TO MySQL Database                                                <br>
     * ---------------------------------------------------------------------------------<br>
     * compare parameters to existing entries in the DB                                 <br>
     * If params match an existing object in database                                   <br>
     * Return primary key for existing object                                           <br>
     * If the params do not match existing object in DB                                 <br>
     * Add new object and return primary key for new object                             <br>
     * @param customerID - used to located existing customer or create new customer
     * @param customerName - used to located existing customer or create new customer
     * @param addressID - used to link to existing address in DB
     * @param active - marks the customer as active or inactive
     */
    public static void updateSQLCustomer(int customerID, String customerName, int addressID, int active) {
        String customerQuery;
        PreparedStatement customerPrepStatement;
        String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
        String createdBy = Login_Controller.getUser();
        String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
        String lastUpdatedBy = Login_Controller.getUser();

        try {
            Connection connection = DBConnection.getConnection();
            Statement customerStatement = connection.createStatement();
            ResultSet customerSet = customerStatement.executeQuery("SELECT * from U07RO2.customer where customerId = " + customerID + ";");

            if (customerSet.next()) {
                //Update Customer In MySQL DB
                customerQuery = "UPDATE U07RO2.customer SET customerName = " + "\"" + customerName + "\"" + ", addressId = " + addressID + ", active = " + active + ", lastUpdate = " + "\"" + lastUpdated + "\"" + ", lastUpdateBy = " + "\"" + lastUpdatedBy + "\"" + " WHERE customerId = " + customerID + ";";
                //Log activity to logfile
                LogFile.writeToLog(Login_Controller.getUser(), " Update Customer Successful: Customer ID ", String.valueOf(customerID));
            } else {
                customerID = Customer.autoGenID();
                //Insert New Customer Into MySQL DB
                customerQuery = "INSERT INTO U07RO2.customer VALUES (" + customerID + ", " + "\"" + customerName + "\"" + ", " + addressID + ", " + active + ", " + "\"" + createdDate + "\"" + ", " + "\"" + createdBy + "\"" + ", " + "\"" + lastUpdated + "\"" + ", " + "\"" + lastUpdatedBy + "\"" + ");";
                //Log activity to logfile
                LogFile.writeToLog(Login_Controller.getUser(), "Add New Customer Successful: Customer ID ", String.valueOf(customerID));
            }

            customerPrepStatement = connection.prepareStatement(customerQuery);
            customerPrepStatement.execute();
            customerPrepStatement.close();
            customerStatement.close();

        }catch (SQLException e){
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * DELETE CUSTOMER FROM SQL             <br>
     * -------------------------------------<br>
     * If customer is found in MySQL delete <br>
     * If cannot delete display error       <br>
     * @param customerID - used to locate and attempt to delete customer from MySQL database
     */
    public static void deleteSQLCustomer(int customerID){
     try {
         Connection connection = DBConnection.getConnection();
         Statement statement = connection.createStatement();
         statement.executeUpdate("DELETE from U07RO2.customer where customerId = " + customerID + ";");

         //Log activity to logfile
         LogFile.writeToLog(Login_Controller.getUser(), "Deleted Customer Successful: Customer ID ", String.valueOf(customerID));

     }catch(SQLException e){
         Alert noneSelected = new Alert(Alert.AlertType.ERROR);
         noneSelected.setTitle("ERROR");
         noneSelected.setHeaderText("Could Not Delete Customer: ");
         noneSelected.setContentText("Cannot delete customers who have scheduled appointments.\n" + e.getMessage());
         noneSelected.show();

         //Log activity to logfile
         LogFile.writeToLog(Login_Controller.getUser(), "Deleted Customer Failed: Customer ID ", String.valueOf(customerID));
     }
    }
}
