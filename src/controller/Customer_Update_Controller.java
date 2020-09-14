package controller;

import Util.DBConnection;
import Util.LogFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Address;
import model.City;
import model.Country;
import model.Customer;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Customer_Update_Controller implements Initializable {

    Stage stage;
    Parent scene;

    /**
     * _____________________________________________________________________
     *                            FXML id selectors
     * ---------------------------------------------------------------------
     * selectors used to read or set form fields
     */

    @FXML
    private Button appointmentsMenuBtn;
    @FXML
    private Button customersMenuBtn1;
    @FXML
    private Button reportsMenuBtn;
    @FXML
    private TextField custIDTxt;
    @FXML
    private TextField custNameTxt;
    @FXML
    private TextField phoneTxt;
    @FXML
    private TextField address1Txt;
    @FXML
    private TextField address2Txt;
    @FXML
    private TextField postalCodeTxt;
    @FXML
    private TextField cityTxt;
    @FXML
    private TextField countryTxt;
    @FXML
    private RadioButton activeYesRBtn;
    @FXML
    private RadioButton activeNoRBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button quitBtn;


    /**
     * _____________________________________________________________________
     *                         SIDE MENU ITEMS
     * ---------------------------------------------------------------------
     */

    /**
     * LAUNCH APPOINTMENTS MENU
     * @param event on button click launch Appointments menu
     * @throws IOException if GUI FXML file not found throw exception
     */
    @FXML
    void onActionLaunchAppointments(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/mainmenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * LAUNCH CUSTOMERS MENU
     * @param event on button click launch Customers menu
     * @throws IOException if GUI FXML file not found throw exception
     */
    @FXML
    void onActionLaunchCustomers(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/customers_main.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * LAUNCH REPORTS MENU
     * @param event on button click launch reports menu
     * @throws IOException if GUI FXML file not found throw exception
     */
    @FXML
    void onActionLaunchReports(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/reports.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     *QUIT PROGRAM
     * On action close program
     * @param event pn action execute Quit method
     */
    @FXML
    void onActionQuit(ActionEvent event) {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        stage.close();
        DBConnection.closeConnection();
        LogFile.writeToLog(Login_Controller.getUser(), "logout successful.");
        System.exit(0);
    }

    /**
     * _____________________________________________________________________
     *                         SAVE UPDATE CUSTOMER METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * SAVE / UPDATE MySQL DATA                                                  <br>
     * --------------------------------------------------------------------------<br>
     * Initialize variables with data from form                                  <br>
     * Expand & Initialize variables to include CityID, CountryID by querying DB <br>
     * Compare all form variables with data from database                        <br>
     * If any form variables are different than MySQL data perform update actions<br>
     * @param event - on even execute save/update method
     */
    @FXML
    void onActionSave(ActionEvent event) {
        //Initialize variables with data input by user into form
        int customerID = Integer.parseInt(custIDTxt.getText());
        String customerName = custNameTxt.getText();
        String address1 = address1Txt.getText();
        String address2 = address2Txt.getText();
        if(address2.isEmpty()){
            address2 = "N/A";
        }
        String postalCode = postalCodeTxt.getText();
        String phone = phoneTxt.getText();
        String cityName = cityTxt.getText();
        String countryName = countryTxt.getText();
        int active = 1;
        if(activeYesRBtn.isSelected()){
            active = 1;
        }
        if(activeNoRBtn.isSelected()){
            active = 0;
        }

        //Declare variables for later use
        //These variables will be initialized with matching data from SQL statements
        //Or if not match is found in SQL these variables will be issued autogenerated unique IDs
        int addressID;
        int cityID;
        int countryID;


        //Validate Form Fields
        if(validateFields()) {
            //Open Connection to DB
            try {
                Connection connection = DBConnection.getConnection();

                //Get and Initiate AddressID for customer
                Statement customerStatement = connection.createStatement();
                ResultSet customerSet = customerStatement.executeQuery("SELECT * from U07RO2.customer where " +
                        "customerId = " + "\"" + customerID + "\"" + ";");
                customerSet.next();
                addressID = customerSet.getInt("addressId");

                //Get and Initiate CityID for customer
                Statement addressStatement = connection.createStatement();
                ResultSet addressSet = addressStatement.executeQuery("SELECT * from U07RO2.address where " +
                        "addressId = " + "\"" + addressID + "\"" + ";");
                addressSet.next();
                cityID = addressSet.getInt("cityId");

                //Get and Initiate CountryID for customer
                Statement cityStatement = connection.createStatement();
                ResultSet citySet = cityStatement.executeQuery("SELECT * from U07RO2.city where " +
                        "cityId = " + "\"" + cityID + "\"" + ";");
                citySet.next();
                countryID = citySet.getInt("countryId");

                //Get country row from SQL for customer
                Statement countryStatement = connection.createStatement();
                ResultSet countrySet = countryStatement.executeQuery("SELECT * from U07RO2.country where " +
                        "countryId = " + "\"" + countryID + "\"" + ";");
                countrySet.next();

                //Compare form data to MySQL data
                boolean customerNameCompare = customerName.equalsIgnoreCase(customerSet.getString("customerName"));
                boolean activeCompare = (active == customerSet.getInt("active"));
                boolean address1Compare = address1.equalsIgnoreCase(addressSet.getString("address"));
                boolean address2Compare = address2.equalsIgnoreCase(addressSet.getString("address2"));
                boolean postalCodeCompare = postalCode.equalsIgnoreCase(addressSet.getString("postalCode"));
                boolean phoneCompare = phone.equalsIgnoreCase(addressSet.getString("phone"));
                boolean cityNameCompare = cityName.equalsIgnoreCase(citySet.getString("city"));
                boolean countryNameCompare = countryName.equalsIgnoreCase(countrySet.getString("country"));
                boolean updateComplete = false;

                if (!(countryNameCompare) || !(cityNameCompare)) {
                    int addCountryID = Country.addSQLCountry(countryName);
                    int addCityID = City.addSQLCity(cityName, addCountryID);
                    int addAddressID = Address.addSQLAddress(address1, address2, phone, postalCode, addCityID);
                    Customer.updateSQLCustomer(customerID, customerName, addAddressID, active);
                    updateComplete = true;
                }

                if ((!(address1Compare) || !(address2Compare) || !(postalCodeCompare) || !(phoneCompare)) && !(updateComplete)) {
                    int addAddressID = Address.updateSQLAddress(addressID, address1, address2, phone, postalCode, cityID);
                    Customer.updateSQLCustomer(customerID, customerName, addAddressID, active);
                    updateComplete = true;
                }

                if ((!(customerNameCompare) || !(activeCompare)) && !(updateComplete)) {
                    Customer.updateSQLCustomer(customerID, customerName, addressID, active);
                }

                stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view/customers_main.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();

            } catch (SQLException | IOException e) {
                System.out.println("Update Error: " + e.getMessage());
            }
        }
    }

    /**
     * CANCEL UPDATE CUSTOMER <br>
     * load main customer screen
     * @param event on button click execute method
     * @throws IOException -
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/customers_main.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * VALIDATE FORM FIELDS                                     <br>
     * If a field is empty return the appropriate error message <br>
     * @return true or false all form fields are not empty
     */
    public boolean validateFields(){
        //Initialize variables with data input by user into form
        String customerName = custNameTxt.getText();
        String address1 = address1Txt.getText();
        String postalCode = postalCodeTxt.getText();
        String phone = phoneTxt.getText();
        String cityName = cityTxt.getText();
        String countryName = countryTxt.getText();

        //Input Validation - Display error if any field is empty
        //Do not display error if address2 field is empty
        if(!(customerName.isEmpty())){
            if(!(address1.isEmpty())){
                if(!(phone.isEmpty())){
                    if(!(postalCode.isEmpty())){
                        if(!(cityName.isEmpty())){
                            if(!(countryName.isEmpty())){
                                return true;
                            }else{
                                Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                                emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                                emptyFieldAlert.setHeaderText("Country cannot be empty.");
                                emptyFieldAlert.setContentText("Please enter a name in the Country field before submitting.");
                                emptyFieldAlert.show();
                            }
                        }else{
                            Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                            emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                            emptyFieldAlert.setHeaderText("City cannot be empty.");
                            emptyFieldAlert.setContentText("Please enter a name in the City field before submitting.");
                            emptyFieldAlert.show();
                        }
                    }else{
                        Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                        emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                        emptyFieldAlert.setHeaderText("Postal Code cannot be empty.");
                        emptyFieldAlert.setContentText("Please enter a name in the Postal Code field before submitting.");
                        emptyFieldAlert.show();
                    }
                }else{
                    Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                    emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                    emptyFieldAlert.setHeaderText("Phone cannot be empty.");
                    emptyFieldAlert.setContentText("Please enter a name in the Phone field before submitting.");
                    emptyFieldAlert.show();
                }
            }else{
                Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                emptyFieldAlert.setHeaderText("Address cannot be empty.");
                emptyFieldAlert.setContentText("Please enter a name in the Address field before submitting.");
                emptyFieldAlert.show();
            }
        } else {
            Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
            emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
            emptyFieldAlert.setHeaderText("Customer Name cannot be empty.");
            emptyFieldAlert.setContentText("Please enter a name in the Customer Name field before submitting.");
            emptyFieldAlert.show();
        }
        return false;
    }

    /**
     * _____________________________________________________________________
     *                         INITIALIZE METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * SEND & SET CUSTOMER DATA IN FORM FIELDS FROM MySQL                           <br>
     * Search Address, City and Country Table for corresponding customer data       <br>
     * Use data from MySQL to set form fields for customer                          <br>
     * @param customer lookup data from Customers OL and display data in form fields
     */
    public void sendCustomer(Customer customer) {

        custIDTxt.setText(String.valueOf(customer.getCustomerID()));
        custIDTxt.setEditable(false);
        custNameTxt.setText(customer.getCustomerName());
        int addressId = customer.getAddressID();
        int cityId = -1;
        int countryId = -1;
        if(customer.getActive() == 1) {
            activeYesRBtn.setSelected(true);
        }
        if(customer.getActive() == 0) {
            activeNoRBtn.setSelected(true);
        }

        try {
            //Open connection to DB and search address table for addressID
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.address WHERE addressId = " + addressId + ";");

            //Search Address table for CityID
            while (resultSet.next())
            {
                //Initialize address data into form
                phoneTxt.setText(resultSet.getString("phone"));
                address1Txt.setText(resultSet.getString("address"));
                address2Txt.setText(resultSet.getString("address2"));
                postalCodeTxt.setText(resultSet.getString("postalCode"));
                cityId = resultSet.getInt("cityId");
            }
            //Close AddressTable Result Set
            resultSet.close();

            //Search City table for CountryID
            resultSet = statement.executeQuery("SELECT * from U07RO2.city WHERE cityId = " + cityId + ";");
            while (resultSet.next())
            {
                //set City Field with data from MySQL City data
                cityTxt.setText(resultSet.getString("city"));
                countryId = resultSet.getInt("countryId");
            }
            //Close CityTable Result Set
            resultSet.close();

            //Search country table for CountryName
            resultSet = statement.executeQuery("SELECT * from U07RO2.country WHERE countryId = " + countryId + ";");
            while (resultSet.next())
            {
                //set country form field with data from MySQL Country table
                countryTxt.setText(resultSet.getString("country"));
            }
            //Close CountryTable ResultSet
            resultSet.close();

        }catch (SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * INITIALIZE DATA - unused on this form
     * @param url -
     * @param resourceBundle -
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
