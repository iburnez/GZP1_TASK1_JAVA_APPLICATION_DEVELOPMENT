package controller;

import Util.DBConnection;
import Util.LogFile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Appointment;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainMenu_Controller implements Initializable {

    LocalDateTime userDateTime = LocalDateTime.now();

    Stage stage;
    Parent scene;

    /**
     * _____________________________________________________________________
     *                            FXML id selectors
     * ---------------------------------------------------------------------
     * selectors used to read or set form fields
     */

    @FXML
    private Button customerMenuBtn;
    @FXML
    private Button reportsMenuBtn;
    @FXML
    private RadioButton monthlyRadioBtn;
    @FXML
    private RadioButton weeklyRadioBtn;
    @FXML
    private Label monthOrweekLabel;
    @FXML
    private Label displayDateLabel;
    @FXML
    private Button previousBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, Integer> apptIDCol;
    @FXML
    private TableColumn<Appointment, LocalDateTime> startTimeCol;
    @FXML
    private TableColumn<Appointment, LocalDateTime> endTimeCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, Integer> cusIDCol;
    @FXML
    private TableColumn<Appointment, String> createDate;
    @FXML
    private TableColumn<Appointment, String> createdByCol;
    @FXML
    private TableColumn<Appointment, String> lastUpdateByCol;
    @FXML
    private TableColumn<Appointment, LocalDateTime> lastUpdateCol;
    @FXML
    private Button addAppointmentBtn;
    @FXML
    private Button updateAppointmentBtn;
    @FXML
    private Button deleteAppointmentBtn;
    @FXML
    private Button quitBtn;

    /**
     * _____________________________________________________________________
     *                         SIDE MENU ITEMS
     * ---------------------------------------------------------------------
     */

    /**
     * LAUNCH CUSTOMERS MAIN MENU
     * @param event on button click launch customer menu
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
     *                     WEEKLY VS MONTHLY VIEW CONTROLS
     * ---------------------------------------------------------------------
     */

    /**
     * MONTHLY APPT VIEW RADIO BUTTON                                   <br>
     * Restart the appointment table view at first day of current month <br>
     * Call monthlyView generator method                                <br>
     * @param event on radio button select execute method
     */
    @FXML
    void onActionDisplayMonthly(ActionEvent event) {
        if(monthlyRadioBtn.isSelected()){
            //set Month/Week of Label:
            monthOrweekLabel.setText("MONTH OF: ");



            //Set userDate to Now to make it easier for users to reset date
            userDateTime = LocalDateTime.now();

            //Call monthlyView with first day of month for users Local Date
            callMonthlyView(userDateTime.withDayOfMonth(1));
        }
    }

    /**
     * WEEKLY APPT VIEW RADIO BUTTON                                   <br>
     * Restart the appointment table view at first day of current week <br>
     * Call weeklyView generator method                                <br>
     * @param event on radio button select execute method
     */
    @FXML
    void onActionDisplayWeekly(ActionEvent event) {
        if(weeklyRadioBtn.isSelected()){
            //set Month/Week of Label:
            monthOrweekLabel.setText("WEEK OF: ");

            //Set userDate to Now to make it easier for users to reset date
            userDateTime = LocalDateTime.now();

            //Get date of first day of the week (local for user)
            Locale userLocale = Locale.getDefault();
            TemporalField fieldLocal = WeekFields.of(userLocale).dayOfWeek();

            //Call weeklyView with first day of week for users Local Date
            callWeeklyView(userDateTime.with(fieldLocal, 1));
        }
    }

    /**
     * CREATE TABLEVIEW OF APPOINTMENTS BY MONTH                <br>
     * ---------------------------------------------------------<br>
     * Clear tableview and monthlyAppointments OL               <br>
     * Create var for paramDateTime + 30days                    <br>
     * Query SQL for appointments after date and before date+30 <br>
     * Loop through results to create columns for tableview     <br>
     * Loop through results and assign to sqlRow OL             <br>
     * Add sqlRow OL to monthlyAppointments OL                  <br>
     * If no results for month display default "no results" row <br>
     * @param date display appointments from date 30days
     */
    void callMonthlyView(LocalDateTime date){
        //Set Display Date Label to tell user which month of appointments are being displayed
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        displayDateLabel.setText(date.format(dateTimeFormatter));

        //Clear Appointment list so only items from SQL Query are displayed.
        Appointment.clearAppointmentList();

        //Declare var for 30 days from param date
        LocalDateTime datePlus30 = date.plusMonths(1);
        try {
            Connection connection = DBConnection.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM U07RO2.appointment where start >" +  "\"" + date + "\"" + " and start < " +  "\"" + datePlus30 + "\"" + " order by start;");

            //Loop through SQL results
            //Create Appointment Object and save to AppointmentsList OL
            rs.beforeFirst();
            while (rs.next()){
                int appointmentID = rs.getInt("appointmentId");
                int customerID = rs.getInt("customerId");
                int userID = rs.getInt("userId");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String type = rs.getString("type");
                String appointmentURL = rs.getString("URL");
                String start = rs.getString("start");
                String end = rs.getString("end");
                String createdDate = rs.getString("createDate");
                String createdBy = rs.getString("createdBy");
                String lastUpdate = rs.getString("lastUpdate");
                String lastUpdatedBy = rs.getString("lastUpdateBy");

                Appointment.addAppointment(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, appointmentURL, start, end, createdDate, createdBy, lastUpdate, lastUpdatedBy));
            }
        }catch (SQLException e){
            Logger.getLogger(MainMenu_Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        appointmentsTable.setItems(Appointment.getAppointmentList());
    }

    /**
     * CREATE TABLEVIEW OF APPOINTMENTS BY WEEK                 <br>
     * ---------------------------------------------------------<br>
     * Clear tableview and weeklyAppointments OL                <br>
     * Create var for paramDateTime + 7days                     <br>
     * Query SQL for appointments after date and before date+7  <br>
     * Loop through results to create columns for tableview     <br>
     * Loop through results and assign to sqlRow OL             <br>
     * Add sqlRow OL to weeklyAppointments OL                   <br>
     * If no results for week display default "no results" row  <br>
     * @param date display appointments from date +7days
     */
    void callWeeklyView(LocalDateTime date){
        //Set Display Date Label to tell user which week of appointments are being displayed
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        displayDateLabel.setText(date.format(dateTimeFormatter));

        //Clear Appointment list so only items from SQL Query are displayed.
        Appointment.clearAppointmentList();

        //Declare var for 7 days from param date
        LocalDateTime datePlus7 = date.plusWeeks(1);
        try {
            Connection connection = DBConnection.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM U07RO2.appointment where start >" +  "\"" + date + "\"" + " and start < " +  "\"" + datePlus7 + "\"" + " order by start;");

            //Loop through SQL results
            //Create Appointment Object and save to AppointmentsList OL
            rs.beforeFirst();
            while (rs.next()){
                int appointmentID = rs.getInt("appointmentId");
                int customerID = rs.getInt("customerId");
                int userID = rs.getInt("userId");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String type = rs.getString("type");
                String appointmentURL = rs.getString("URL");
                String start = rs.getString("start");
                String end = rs.getString("end");
                String createdDate = rs.getString("createDate");
                String createdBy = rs.getString("createdBy");
                String lastUpdate = rs.getString("lastUpdate");
                String lastUpdatedBy = rs.getString("lastUpdateBy");

                Appointment.addAppointment(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, appointmentURL, start, end, createdDate, createdBy, lastUpdate, lastUpdatedBy));
            }
        }catch (SQLException e){
            Logger.getLogger(MainMenu_Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        appointmentsTable.setItems(Appointment.getAppointmentList());
    }

    /**
     * NEXT MONTH/WEEK BUTTON                                           <br>
     * Check if which view is selected (monthly or weekly)              <br>
     * If monthly view add 1 month to date and callMonthlyView method   <br>
     * If weekly view add 1 week to date and callWeeklyView method      <br>
     * @param event on button click execute method
     */
    @FXML
    void onActionNext(ActionEvent event) {
        if(monthlyRadioBtn.isSelected()){
            //add 1 month to userDate var and call monthly view
            userDateTime = userDateTime.withDayOfMonth(1).plusMonths(1);
            callMonthlyView(userDateTime);
        }

        if(weeklyRadioBtn.isSelected()){
            //Get date of first day of the week (local for user)
            Locale userLocale = Locale.getDefault();
            TemporalField fieldLocal = WeekFields.of(userLocale).dayOfWeek();
            //Add 1 week to userDate and call weekly view
            userDateTime = userDateTime.with(fieldLocal, 1).plusWeeks(1);
            callWeeklyView(userDateTime);
        }
    }

    /**
     * PREVIOUS MONTH/WEEK BUTTON                                            <br>
     * Check if which view is selected (monthly or weekly)                   <br>
     * If monthly view subtract 1 month to date and callMonthlyView method   <br>
     * If weekly view subtract 1 week to date and callWeeklyView method      <br>
     * @param event on button click execute method
     */
    @FXML
    void onActionPrevious(ActionEvent event) {
        if(monthlyRadioBtn.isSelected()){
            //subtract 1 month to userDate var and call monthly view
            userDateTime = userDateTime.withDayOfMonth(1).minusMonths(1);
            callMonthlyView(userDateTime);
        }

        if(weeklyRadioBtn.isSelected()){
            //Get date of first day of the week (local for user)
            Locale userLocale = Locale.getDefault();
            TemporalField fieldLocal = WeekFields.of(userLocale).dayOfWeek();
            //Subtract 1 week to userDate and call weekly view
            userDateTime = userDateTime.with(fieldLocal, 1).minusWeeks(1);
            callWeeklyView(userDateTime);
        }
    }

    /**
     * _____________________________________________________________________
     *                       APPOINTMENT MENU CONTROLS
     * ---------------------------------------------------------------------
     */

    /**
     * LAUNCH ADD APPOINTMENT MENU
     * @param event on button click launch Add Appointment menu
     * @throws IOException if GUI FXML File not found throw exception
     */
    @FXML
    void onActionAddAppointment(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/appointment_add.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * LAUNCH UPDATE APPOINTMENT MENU
     * @param event on button click launch Update Appointment menu
     * @throws IOException if GUI FXML File not found throw exception
     */
    @FXML
    void onActionUpdateAppointment(ActionEvent event) throws IOException {
        Appointment selectAppt = appointmentsTable.getSelectionModel().getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/appointment_update.fxml"));
            loader.load();

            Appointment_Update_Controller ApptUpdateController = loader.getController();
            ApptUpdateController.sendAppointment(selectAppt);

            stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();

        }catch (NullPointerException e) {
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle("ERROR");
            noneSelected.setHeaderText("No Appointment Selected: ");
            noneSelected.setContentText("Please select an appointment from the list to update. " + e.getMessage());
            noneSelected.show();
        }

    }

    /**
     * DELETE SELECTED APPOINTMENT                                                          <br>
     * Prior to delete display confirmation dialog to user                                  <br>
     * If user confirms dialog -> Delete selected appointment from MySQL then remove from OL<br>
     * If no appointment is selected display an error                                       <br>
     * @param event on button click execute remove appointment function
     */
    @FXML
    void onActionDeleteAppointment(ActionEvent event) {
        Appointment deleteAppt = appointmentsTable.getSelectionModel().getSelectedItem();

        //If user selects customer from list execute delete
        if(deleteAppt != null){

            //Display delete confirmation to user
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the " + deleteAppt.getType() +
                                                                            " on " + deleteAppt.getStart() + "?");
            Optional<ButtonType> confirmAlert = confirmDelete.showAndWait();

            //If user confirms delete -> execute delete appointment
            if(confirmAlert.isPresent() && confirmAlert.get() == ButtonType.OK){
                Appointment.deleteSQLAppointment(deleteAppt.getAppointmentID());
            }
        }
        //if now customer is selected to delete display error.
        else {
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle("ERROR");
            noneSelected.setHeaderText("No Appointment Selected: ");
            noneSelected.setContentText("Please select an appointment from the list to delete.");
            noneSelected.show();
        }
        if(weeklyRadioBtn.isSelected()){
            callWeeklyView(userDateTime);
        }
        if(monthlyRadioBtn.isSelected()){
            callMonthlyView(userDateTime.withDayOfMonth(1));
        }
    }

    /**
     * _____________________________________________________________________
     *                         INITIALIZE METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * INITIALIZE GUI (DEFAULT METHOD)  <br>
     * Initialize user DateTime.now     <br>
     * Call callMonthlyView method      <br>
     * @param url -
     * @param resourceBundle -
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        monthOrweekLabel.setText("MONTH OF: ");
        callMonthlyView(userDateTime.withDayOfMonth(1));

        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        cusIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        createDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        createdByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        lastUpdateByCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        lastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));

    }
}
