package controller;

import Util.DBConnection;
import Util.LogFile;
import Util.TimeConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Appointment;
import model.BusinessTime;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Appointment_Add_Controller implements Initializable {

    //Vars for onSaveAction Error Alert
    private String titleError;
    private String descriptionError;
    private String locationError;
    private String contactError;
    private String typeError;
    private String startError;
    private String endError;

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
    private TextField apptIDTxt;
    @FXML
    private TextField titleTxt;
    @FXML
    private TextField descriptionTxt;
    @FXML
    private TextField contactTxt;
    @FXML
    private TextField locationTxt;
    @FXML
    private TextField typeTxt;
    @FXML
    private TextField appointmentURLTxt;
    @FXML
    private ComboBox<Customer> custNameTxt;
    @FXML
    private DatePicker appointmentDatePicker;
    @FXML
    private ComboBox<BusinessTime> selectHourCmbBx;
    @FXML
    private ComboBox<BusinessTime> selectMinuteCmbBx;
    @FXML
    private ComboBox<BusinessTime> durationHoursCmbBx;
    @FXML
    private ComboBox<BusinessTime> durationMinutesCmbBx;
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
     *                     SAVE NEW APPOINTMENT METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * SAVE NEW APPOINTMENT                                                                             <br>
     * Check if fields are not empty - false display error dialog                                       <br>
     * Initialize appt vars with data from form                                                         <br>
     * Calculate Start / End Time using TimeConverter methods                                           <br>
     * Check if appointment date is in the future - false display error dialog                          <br>
     * Check if appointment is within business hours - false display error dialog                       <br>
     * Check if appointment does not overlap other existing appointments - false display error dialog   <br>
     * If all above valid then Insert new appointment into SQL DB                                       <br>
     * @param event on action execute save method
     */
    @FXML
    void onActionSave(ActionEvent event) throws IOException {

        if(validateFields()) {
            Customer selectedCustomer = custNameTxt.getSelectionModel().getSelectedItem();
            int appointmentID = Integer.parseInt(apptIDTxt.getText());
            int customerID = selectedCustomer.getCustomerID();
            int userID = Login_Controller.getUserID();
            String title = titleTxt.getText();
            String description = descriptionTxt.getText();
            String location = locationTxt.getText();
            String contact = contactTxt.getText();
            String type = typeTxt.getText();
            String appointmentURL = appointmentURLTxt.getText();
            LocalDate date = appointmentDatePicker.getValue();
            String start = String.valueOf(TimeConverter.dateTimeToUTC(TimeConverter.stringLDTBuilder(date, parseApptStartTime())));
            String end = String.valueOf(TimeConverter.dateTimeToUTC(TimeConverter.stringLDTBuilder(date, parseApptEndTime())));

            //Vars for Invalid Appointment Alerts
            String durationHours = durationHoursCmbBx.getSelectionModel().getSelectedItem().getDisplayTime();
            String durationMin = durationMinutesCmbBx.getSelectionModel().getSelectedItem().getDisplayTime();
            DateTimeFormatter ldtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
            String userStart = TimeConverter.utcToUserLocal(start).format(ldtFormatter);
            String userEnd = TimeConverter.utcToUserLocal(end).format(ldtFormatter);

            //Check if appointment is in the future
            if(isFutureDateCheck(start)){
                //Check if appointment time is between business hours
                if(businessHoursCheck(start, end)) {
                    if(checkAllApptOverlap(start, end)){
                        Appointment.addOrUpdateSQLAppointment(appointmentID, customerID, userID, title, description, location,
                                contact, type, appointmentURL, start, end);

                        stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
                        scene = FXMLLoader.load(getClass().getResource("/view/mainmenu.fxml"));
                        stage.setScene(new Scene(scene));
                        stage.show();
                    }else{
                        String startErrorLDT = TimeConverter.utcToZoneTime(startError).format(ldtFormatter);
                        String endErrorLDT = TimeConverter.utcToZoneTime(endError).format(ldtFormatter);

                        Alert invalidAppointment = new Alert(Alert.AlertType.ERROR);
                        invalidAppointment.setTitle("INVALID APPOINTMENT");
                        invalidAppointment.setHeaderText("Appointments must NOT overlap other appointments");
                        invalidAppointment.setContentText("Please adjust your appointment start time and duration to ensure " +
                                "your NEW appointment does not conflict with the following existing appointment.\n" +
                                "\nEXISTING APPOINTMENT DETAILS:" +
                                "\nAppointment Start Time: " + startErrorLDT +
                                "\nAppointment End Time: " + endErrorLDT +
                                "\nAppointment Title:" + titleError +
                                "\nAppointment Type: " + typeError +
                                "\nAppointment Description: " + descriptionError +
                                "\nAppointment Contact: " + contactError +
                                "\nAppointment Location: " + locationError );
                        invalidAppointment.show();
                    }
                }else{
                    Alert invalidAppointment = new Alert(Alert.AlertType.ERROR);
                    invalidAppointment.setTitle("INVALID APPOINTMENT");
                    invalidAppointment.setHeaderText("Appointments must be scheduled within busness hours (8AM - 5PM)");
                    invalidAppointment.setContentText("Please adjust your appointment start time and duration to ensure " +
                            "the appointment remains within the Business Hours of 8:00AM and " +
                            "5:00PM.\n " +
                            "\nCurrently Your Appointment is as follows:" +
                            "\nAppointment Start Time: " + userStart +
                            "\nAppointment End Time: " + userEnd +
                            "\nDuration of Appointment: " + durationHours + " " + durationMin);
                    invalidAppointment.show();
                }//end validate businessHoursCheck
            }else{
                Alert invalidAppointment = new Alert(Alert.AlertType.ERROR);
                invalidAppointment.setTitle("INVALID APPOINTMENT");
                invalidAppointment.setHeaderText("Appointments cannot be scheduled in the past");
                invalidAppointment.setContentText("Please adjust your appointment time to a future date.\n " +
                                                "\nCurrently Your Appointment is as follows:" +
                                                "\nAppointment Start Time: " + userStart +
                                                "\nAppointment End Time: " + userEnd +
                                                "\nDuration of Appointment: " + durationHours + " " + durationMin);
                invalidAppointment.show();
            }//end validate futureDateCheck
        }
    }

    /**
     * CANCEL NEW APPOINTMENT <br>
     * Load main appointment screen
     * @param event on button click load main menu
     */
    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        stage = (Stage)((Button)(event.getSource())).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/view/mainmenu.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * PARSE APPOINTMENT START TIME                             <br>
     * Get MathTime from appointment start hours box            <br>
     * Get MathTime from appointment start min box              <br>
     * Concatenate start time string from startHours & StartMin <br>
     * @return string of startTime
     */
    public String parseApptStartTime(){
        String startTime = null;

        if(validateFields()) {
            //Get MathTime from appointment start hours box
            //Get MathTime from appointment start min box
            String startTimeHours = selectHourCmbBx.getSelectionModel().getSelectedItem().getMathTime();
            String startTimeMinutes = selectMinuteCmbBx.getSelectionModel().getSelectedItem().getMathTime();

            //Concatenate start time string from startHours & StartMin
            startTime = String.valueOf((LocalTime.parse(startTimeHours)).plusMinutes(Integer.parseInt(startTimeMinutes)));
        }
        return startTime;
    }

    /**
     * PARSE APPOINTMENT END TIME                               <br>
     * Call parseApptStartTime string                           <br>
     * Get MathTime from appointment duration hours box         <br>
     * Get MathTime from appointment duration min box           <br>
     * Calculate endtime startTime + DurationHours + DurationMin<br>
     * @return return end time
     */
    public String parseApptEndTime(){
        //Call parseApptStartTime string - Parse out Time from DateTime
        LocalTime startTime = LocalTime.parse(parseApptStartTime());
        LocalTime endTime = null;

        if(validateFields()) {
            //Get MathTime from appointment duration hours box
            //Get MathTime from appointment duration min box
            String durationHours = durationHoursCmbBx.getSelectionModel().getSelectedItem().getMathTime();
            String durationMin = durationMinutesCmbBx.getSelectionModel().getSelectedItem().getMathTime();

            //Calculate endtime startTime + DurationHours + DurationMin
            endTime = startTime.plusHours(Integer.parseInt(durationHours));
            endTime = endTime.plusMinutes(Integer.parseInt(durationMin));
        }
        return String.valueOf(endTime);
    }

    /**
     * VALIDATE FORM FIELDS ARE NOT EMPTY       <br>
     * Test if each form field has a value      <br>
     * display error if form field is empty     <br>
     * @return true/false form field are empty
     */
    public boolean validateFields(){
        //initialize variables with values entered by the user into the form
        Customer selectedCustomer = custNameTxt.getSelectionModel().getSelectedItem();
        String title = titleTxt.getText();
        String description = descriptionTxt.getText();
        String contact = contactTxt.getText();
        String location = locationTxt.getText();
        String type = typeTxt.getText();
        String appointmentURL = appointmentURLTxt.getText();
        LocalDate date = appointmentDatePicker.getValue();
        BusinessTime startTimeHours = selectHourCmbBx.getSelectionModel().getSelectedItem();
        BusinessTime startTimeMinutes = selectMinuteCmbBx.getSelectionModel().getSelectedItem();
        BusinessTime durationHours = durationHoursCmbBx.getSelectionModel().getSelectedItem();
        BusinessTime durationMin = durationMinutesCmbBx.getSelectionModel().getSelectedItem();

        //test if each variable has a value
        //if variable is empty or null display corresponding error message
        if(selectedCustomer != null){
            if(!(title.isEmpty())){
                if(!(description.isEmpty())){
                    if(!(contact.isEmpty())){
                        if(!(location.isEmpty())){
                            if(!(type.isEmpty())){
                                if(!(appointmentURL.isEmpty())){
                                    if(date != null){
                                        if(startTimeHours != null || startTimeMinutes != null){
                                                if(durationHours != null || durationMin != null){
                                                        return true;
                                                }else {
                                                    Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                                                    emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                                                    emptyFieldAlert.setHeaderText("Appointment Duration cannot be empty.");
                                                    emptyFieldAlert.setContentText("Please select a value in both duration fields before submitting.");
                                                    emptyFieldAlert.show();
                                                }
                                        }else {
                                            Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                                            emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                                            emptyFieldAlert.setHeaderText("Appointment time cannot be empty.");
                                            emptyFieldAlert.setContentText("Please select a value in both Appointment Time fields before submitting.");
                                            emptyFieldAlert.show();
                                        }
                                    }else{
                                        Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                                        emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                                        emptyFieldAlert.setHeaderText("Date cannot be empty.");
                                        emptyFieldAlert.setContentText("Please enter a value in the date field before submitting.");
                                        emptyFieldAlert.show();
                                    }
                                }else {
                                    Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                                    emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                                    emptyFieldAlert.setHeaderText("Appointment URL cannot be empty.");
                                    emptyFieldAlert.setContentText("Please enter a value in the Appointment URL field before submitting.");
                                    emptyFieldAlert.show();
                                }
                            }else {
                                Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                                emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                                emptyFieldAlert.setHeaderText("Type cannot be empty.");
                                emptyFieldAlert.setContentText("Please enter a value in the type field before submitting.");
                                emptyFieldAlert.show();
                            }
                        }else {
                            Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                            emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                            emptyFieldAlert.setHeaderText("Location cannot be empty.");
                            emptyFieldAlert.setContentText("Please enter a value in the location field before submitting.");
                            emptyFieldAlert.show();
                        }
                    }else{
                        Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                        emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                        emptyFieldAlert.setHeaderText("Contact cannot be empty.");
                        emptyFieldAlert.setContentText("Please enter a value in the contact field before submitting.");
                        emptyFieldAlert.show();
                    }
                }else {
                    Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                    emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                    emptyFieldAlert.setHeaderText("Description cannot be empty.");
                    emptyFieldAlert.setContentText("Please enter a value in the Description field before submitting.");
                    emptyFieldAlert.show();
                }
            }else{
                Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
                emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
                emptyFieldAlert.setHeaderText("Title cannot be empty.");
                emptyFieldAlert.setContentText("Please enter a value in the Title field before submitting.");
                emptyFieldAlert.show();
            }
        }else{
            Alert emptyFieldAlert = new Alert(Alert.AlertType.ERROR);
            emptyFieldAlert.setTitle("ERROR FIELD REQUIRED");
            emptyFieldAlert.setHeaderText("Customer must be selected.");
            emptyFieldAlert.setContentText("Please select a customer before submitting.");
            emptyFieldAlert.show();
        }
        return false;
    }

    /**
     * _____________________________________________________________________
     *                 APPOINTMENT SCHEDULE VALIDATION RULES
     * ---------------------------------------------------------------------
     */

    /**
     * VALIDATE APPOINTMENT IS IN FUTURE                                                    <br>
     * Convert Start String to LocalTime for User                                           <br>
     * Calculate if appointment is in future                                                <br>
     * @param start start time of appointment
     * @return true or false if appointment time is within business hours
     */
    boolean isFutureDateCheck(String start){

        //Set Current Date Time Var
        LocalDateTime userNow = LocalDateTime.now();

        //Convert StartTime String to LocalDateTime Var
        LocalDateTime startLocal = TimeConverter.utcToUserLocal(start);

        //Calculate if appointment is within business hours
        boolean isValid = startLocal.isAfter(userNow);

        return isValid;
    }

    /**
     * VALIDATE APPOINTMENT IS WITHIN BUSINESS HOURS                                        <br>
     * Convert Start & End String to LocalTime for User                                     <br>
     * Parse out Time from dateTime using TimeConverter.parseTime                           <br>
     * Convert String to LocalTime to calculate before/after business hours                 <br>
     * Calculate if appointment is within business hours                                    <br>
     * @param start start time of appointment
     * @param end end time of appointment
     * @return true or false if appointment time is within business hours
     */
    boolean businessHoursCheck(String start, String end){
        boolean validAppointment = false;

        //Set Business Hours Vars
        LocalTime open4Business = LocalTime.parse("07:59");
        LocalTime closed4Business = LocalTime.parse("17:01");

        //Convert TS to LocalTime for User
        LocalDateTime startLocal = TimeConverter.utcToUserLocal(start);
        LocalDateTime endLocal = TimeConverter.utcToUserLocal(end);

        //Parse out Time from dateTime using TimeConverter.parseTime
        String startLTString = TimeConverter.parseTime(String.valueOf(startLocal));
        String endLTString = TimeConverter.parseTime(String.valueOf(endLocal));

        //Convert String to LocalTime to calculate before/after business hours
        LocalTime startTimeLocal = LocalTime.parse(startLTString);
        LocalTime endTimeLocal = LocalTime.parse(endLTString);

        //Calculate if appointment is within business hours
        boolean validStart = startTimeLocal.isAfter(open4Business);
        boolean validEnd = endTimeLocal.isBefore(closed4Business);

        //If start & end time are within business hours return true
        if(validStart && validEnd){
            validAppointment = true;
        }

        return validAppointment;
    }

    /**
     * CHECK ALL APPOINTMENTS FOR OVERLAP                                           <br>
     * Convert Start & End time from String LocalDateTime                           <br>
     * Check Each new appointment doest not overlap each appointment in DB          <br>
     * if no overlap return true                                                    <br>
     * If overlap return false                                                      <br>
     * If overlap initialize appointment detail fields to display in error dialogue <br>
     * @param start start of new appointment
     * @param end end of new appointment
     * @return true or false new appointment does not overlap any other appointment
     */
    boolean checkAllApptOverlap(String start, String end){
        boolean isValidAppt = true;

        //Convert Start & End time from String LocalDateTime
        LocalDateTime startDateTime = TimeConverter.utcToUserLocal(start);
        LocalDateTime endDateTime = TimeConverter.utcToUserLocal(end);

        //Catch SQL Exception
        try {
            Connection connection = DBConnection.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM U07RO2.appointment;");

            //Check Each new appointment doest not overlap each appointment in DB
            //if no overlap return true
            //If overlap return false
            while(rs.next()){
                LocalDateTime otherApptStart = TimeConverter.utcToZoneTime(rs.getString("start"));
                LocalDateTime otherApptEnd = TimeConverter.utcToZoneTime(rs.getString("end"));
                if(!(checkIndividualApptOverlap(startDateTime, endDateTime, otherApptStart, otherApptEnd))){
                    isValidAppt = false;

                    //If overlap initialize appointment detail fields to display in error dialogue
                    titleError = rs.getString("title");
                    descriptionError = rs.getString("description");
                    locationError = rs.getString("location");
                    contactError = rs.getString("contact");
                    typeError = rs.getString("type");
                    startError = rs.getString("start");
                    endError = rs.getString("end");

                }
            }

        }catch (SQLException e){
            Logger.getLogger(Appointment_Add_Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        return isValidAppt;
    }

    /**
     * CHECK IF APPOINTMENT IS BEFORE || AFTER APPOINTMENT                          <br>
     * Check if new appointment start and ends before the other appointment         <br>
     * Check if new appointment starts and ends after the other appointment         <br>
     * @param startDateTime new appointment start time
     * @param endDateTime new appointment end time
     * @param otherApptStart other appointment start time
     * @param otherApptEnd other appointment end time
     * @return true or false if new appointment is before or after other appointment
     */
    boolean checkIndividualApptOverlap(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime otherApptStart, LocalDateTime otherApptEnd){
        boolean isValidAppt = false;

        //Check if new appointment start and ends before the other appointment
        if(startDateTime.isBefore(otherApptStart) && endDateTime.isBefore(otherApptEnd)){
            isValidAppt = true;
        }

        //Check if new appointment starts and ends after the other appointment
        if(startDateTime.isAfter(otherApptStart) && endDateTime.isAfter(otherApptEnd)){
            isValidAppt = true;
        }

        return isValidAppt;

    }

    /**
     * _____________________________________________________________________
     *                         INITIALIZE METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * SEND CUSTOMER TO ADD APPOINTMENT FIELDS
     * @param customer customer to load into new appointment screen
     */
    public void sendCustomer(Customer customer){

        custNameTxt.setItems(Customer.getAllCustomers());
        custNameTxt.getSelectionModel().select(customer);
    }

    /**
     * INITIALIZE COMBOX DROPDOWN MENUS
     * Include StringConverter in Combobox to display approriate variable from Object List
     * Override toString to return approriate variable to display in comboboxes
     * Override fromString method to object matching selection from combobox
     * @param url -
     * @param resourceBundle -
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        apptIDTxt.setText(String.valueOf(Appointment.autoGenID()));
        apptIDTxt.setEditable(false);

        Customer.buildAllCustomers();
        custNameTxt.setItems(Customer.getAllCustomers());
        //Use StringConvert to display customerName in comboBox dropdown
        StringConverter<Customer> stringConverter = new StringConverter<Customer>() {
            //OverRide toString method to display customerName in combobox representing
            //each customer object in the combobox
            @Override
            public String toString(Customer customer) {
                return customer.getCustomerName();
            }

            //OverRide fromString method to locate matching customer object from selected
            //customerName in comboBox dropdown and return matching customer object
            @Override
            public Customer fromString(String selectedCustName) {
                return Customer.getAllCustomers().stream().filter(customer -> customer.getCustomerName()
                        .equals(selectedCustName)).collect(Collectors.toList()).get(0);
            }
        };
        custNameTxt.setConverter(stringConverter);

        BusinessTime.buildBusinessHoursList();
        selectHourCmbBx.setItems(BusinessTime.getBusinessHoursList());
        //Use StringConvert to display Hours of Day in AM/PM format in comboBox dropdown
        StringConverter<BusinessTime> bHoursConverter = new StringConverter<BusinessTime>() {
            //OverRide toString method to display Hours of day in AM/PM format in combobox
            //representing BusinessTime Object from BusinessHoursList
            @Override
            public String toString(BusinessTime businessTime) {
                return businessTime.getDisplayTime();
            }
            //OverRide fromString method to locate matching BusinessTime object in BusinessHoursList
            // from selected AM/PM Hours in comboBox dropdown and return matching BusinessTime object
            @Override
            public BusinessTime fromString(String s) {
                return BusinessTime.getBusinessHoursList().stream().filter(businessTime -> businessTime
                        .getDisplayTime().equals(s)).collect(Collectors.toList()).get(0);
            }
        };
        selectHourCmbBx.setConverter(bHoursConverter);

        BusinessTime.buildBusinessMinuteList();
        selectMinuteCmbBx.setItems(BusinessTime.getBusinessMinuteList());
        //Use StringConvert to display Minutes in comboBox dropdown
        StringConverter<BusinessTime> bMConverter = new StringConverter<BusinessTime>() {
            //OverRide toString method to display Minutes in combobox
            //representing BusinessTime Object from BusinessMinuteList
            @Override
            public String toString(BusinessTime businessTime) {
                return businessTime.getDisplayTime();
            }
            //OverRide fromString method to locate matching BusinessTime object in BusinessMinuteList
            // from selected Minutes comboBox dropdown and return matching BusinessTime object
            @Override
            public BusinessTime fromString(String s) {
                return BusinessTime.getBusinessMinuteList().stream().filter(businessTime -> businessTime
                        .getDisplayTime().equals(s)).collect(Collectors.toList()).get(0);
            }
        };
        selectMinuteCmbBx.setConverter(bMConverter);

        BusinessTime.buildDurationHoursList();
        durationHoursCmbBx.setItems(BusinessTime.getDurationHoursList());
        //Use StringConvert to display Appointment Duration Hours in comboBox dropdown
        StringConverter<BusinessTime> durHoursConverter = new StringConverter<BusinessTime>() {
            //OverRide toString method to display Appointment Duration Hours in combobox
            //representing BusinessTime Object from DurationHoursList
            @Override
            public String toString(BusinessTime businessTime) {
                return businessTime.getDisplayTime();
            }
            //OverRide fromString method to locate matching BusinessTime object in DurationHoursList
            // from selected Appointment Duration Hours in comboBox dropdown and return matching BusinessTime object
            @Override
            public BusinessTime fromString(String s) {
                return null;
            }
        };
        durationHoursCmbBx.setConverter(durHoursConverter);

        BusinessTime.buildDurationMinutesList();
        durationMinutesCmbBx.setItems(BusinessTime.getDurationMinutesList());
        //Use StringConvert to display Duration Minutes in comboBox dropdown
        StringConverter<BusinessTime> durMinsConverter = new StringConverter<BusinessTime>() {
            //OverRide toString method to display Minutes in combobox
            //representing BusinessTime Object from DurationMinutesList
            @Override
            public String toString(BusinessTime businessTime) {
                return businessTime.getDisplayTime();
            }
            //OverRide fromString method to locate matching BusinessTime object in DurationMinutesList
            // from selected Minutes comboBox dropdown and return matching BusinessTime object
            @Override
            public BusinessTime fromString(String s) {
                return null;
            }
        };
        durationMinutesCmbBx.setConverter(durMinsConverter);
    }
}
