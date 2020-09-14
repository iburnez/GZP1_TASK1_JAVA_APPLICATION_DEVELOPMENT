package controller;


import Util.DBConnection;
import Util.LogFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Address;
import model.City;
import model.Country;
import model.Customer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Customer_Add_Controller implements Initializable {

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
    private ToggleGroup activeGroup;
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
     *                         SAVE NEW CUSTOMER METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * SAVE NEW CUSTOMER                            <br>
     * Initialize all variables with data from form <br>
     * Validate all fields are not empty            <br>
     * Add or Update Country / City to MySQL        <br>
     * Add Address, Customer to MySQL               <br>
     * @param event on button click execute save method
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
        int active = 1;
        if(activeYesRBtn.isSelected()){
            active = 1;
        }
        if(activeNoRBtn.isSelected()){
            active = 0;
        }
        String countryName = countryTxt.getText();

        if(validateFields()) {
            try {
                int addCountryID = Country.addSQLCountry(countryName);
                int addCityID = City.addSQLCity(cityName, addCountryID);
                int addAddressID = Address.addSQLAddress(address1, address2, phone, postalCode, addCityID);
                Customer.addSQLCustomer(customerName, addAddressID, active);

                //Load Customer Main Menu On Save
                stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/view/customers_main.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();

            }catch (IOException e){
                System.out.println("Error: " + e.getMessage() + " " + e.getCause() + " " + e.getClass());
            }
        }
    }

    /**
     * CANCEL NEW CUSTOMER <br>
     * Load main customer menu
     * @param event on button click execute method
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/customers_main.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * VALIDATE FORM FIELDS
     * If a field is empty return the appropriate error message
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
                    emptyFieldAlert.setHeaderText("Phone Code cannot be empty.");
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
     * INITIALIZE CUSTOMER FORM
     * Populate CustomerID field with new unique ID
     * @param url -
     * @param resourceBundle -
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        custIDTxt.setText(String.valueOf(Customer.autoGenID()));
        custIDTxt.setEditable(false);
        phoneTxt.setPromptText("###-###-####");
        address2Txt.setPromptText("Optional");

    }
}
