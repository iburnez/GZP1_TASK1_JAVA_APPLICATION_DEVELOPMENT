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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class Customer_Main_Controller implements Initializable {

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
    private Button reportsMenuBtn;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<Customer, Integer> custIDCol;
    @FXML
    private TableColumn<Customer, String> custNameCol;
    @FXML
    private TableColumn<Customer, Integer> addressCol;
    @FXML
    private TableColumn<Customer, Integer> activeCol;
    @FXML
    private TableColumn<Customer, String> createdDateCol;
    @FXML
    private TableColumn<Customer, String> createdByCol;
    @FXML
    private TableColumn<Customer, String> lastUpdatedCol;
    @FXML
    private TableColumn<Customer, String> lastUpdatedByCol;
    @FXML
    private Button addCustBtn;
    @FXML
    private Button updateCustBtn;
    @FXML
    private Button deleteCustBtn;
    @FXML
    private Button newAppointmentBtn;
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
     *                       APPOINTMENT MENU CONTROLS
     * ---------------------------------------------------------------------
     */

    /**
     * LAUNCH ADD CUSTOMER MENU
     * @param event on button click launch reports menu
     * @throws IOException if GUI FXML file not found throw exception
     */
    @FXML
    void onActionAddCust(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/customers_add.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * LAUNCH UPDATE CUSTOMER MENU
     * @param event on button click launch reports menu
     * @throws IOException if GUI FXML file not found throw exception
     */
    @FXML
    void onActionUpdateCust(ActionEvent event) throws SQLException, IOException {

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/customers_update.fxml"));
                loader.load();

                Customer_Update_Controller CUController = loader.getController();
                CUController.sendCustomer(customersTable.getSelectionModel().getSelectedItem());

                stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();
            }catch (NullPointerException e){
                Alert nullSelectAlert = new Alert(Alert.AlertType.ERROR);
                nullSelectAlert.setTitle("No Customer Selected");
                nullSelectAlert.setHeaderText("Please select a customer to update.");
                nullSelectAlert.setContentText("Please select a customer from the customer list to update.");
                nullSelectAlert.show();
            }
    }

    /**
     * DELETE SELECTED CUSTOMER                                                             <br>
     * -------------------------------------------------------------------------------------<br>
     * Prior to delete display confirmation dialog to user                                  <br>
     * If user confirms dialog -> Delete selected customer from MySQL then remove from OL   <br>
     * If no customer is selected display an error                                          <br>
     * @param event on button click execute remove customer function
     */
    @FXML
    void onActionDeleteCust(ActionEvent event) {
        Customer deleteCustomer = customersTable.getSelectionModel().getSelectedItem();

        //If user selects customer from list execute delete
        if(deleteCustomer != null){

            //Display delete confirmation to user
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + deleteCustomer.getCustomerName() + "?");
            Optional<ButtonType> confirmAlert = confirmDelete.showAndWait();

            //If user confirms delete -> execute delete customer
            if(confirmAlert.isPresent() && confirmAlert.get() == ButtonType.OK) {
                Customer.deleteSQLCustomer(deleteCustomer.getCustomerID());
                Customer.buildAllCustomers();
            }
        }
        //if now customer is selected to delete display error.
        else {
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle("ERROR");
            noneSelected.setHeaderText("No Customer Selected: ");
            noneSelected.setContentText("Please select an customer from the list to delete.");
            noneSelected.show();
        }
    }

    /**
     * LAUNCH NEW APPOINTMENT MENU                            <br>
     * get selected customer and send to add appointment menu <br>
     * @param event on button click launch reports menu
     * @throws IOException if GUI FXML file not found throw exception
     */
    @FXML
    void onActionNewAppointment(ActionEvent event) throws IOException {
        try {
            Customer selectedCust = customersTable.getSelectionModel().getSelectedItem();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/appointment_add.fxml"));
            loader.load();

            Appointment_Add_Controller AptAddController = loader.getController();
            AptAddController.sendCustomer(selectedCust);

            stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NullPointerException e){
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle("ERROR");
            noneSelected.setHeaderText("No Customer Selected: ");
            noneSelected.setContentText("Please select an Customer from the list to create appointment. " + e.getMessage());
            noneSelected.show();
        }
    }

    /**
     * _____________________________________________________________________
     *                         INITIALIZE METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * INITIALIZE CUSTOMER TABLE            <br>
     * Build customer ObservableList        <br>
     * Initialize data in table with OL data<br>
     * @param url -
     * @param resourceBundle -
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Customer.buildAllCustomers();
        customersTable.setItems(Customer.getAllCustomers());
        custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        custNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("addressID"));
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        createdDateCol.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        lastUpdatedCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdated"));
        lastUpdatedByCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));


    }
}
