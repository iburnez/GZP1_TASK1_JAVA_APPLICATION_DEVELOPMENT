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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login_Controller implements Initializable {
    private int timeToAppt;
    private String title;
    private String location;
    private String type;
    private String start;
    private static String user;
    private static int userID;
    private static Locale userLocale = Locale.getDefault();
    ResourceBundle resourceBundle;
    Stage stage;
    Parent scene;

    /**
     * _____________________________________________________________________
     *                            FXML id selectors
     * ---------------------------------------------------------------------
     * selectors used to read or set form fields
     */

    @FXML
    private Label userNameLbl;
    @FXML
    private Label passwordLabl;
    @FXML
    private TextField userNameTxt;
    @FXML
    private TextField passwordTxt;
    @FXML
    private Button loginBtn;
    @FXML
    private Button quitBtn;

    /**
     * _____________________________________________________________________
     *                               GETTERS
     * ---------------------------------------------------------------------
     */

    /**
     * GET USER
     * @return user
     */
    public static String getUser(){
        return user;
    }

    /**
     * GET USER ID
     * @return userID
     */
    public static int getUserID(){
        return userID;
    }


    /**
     * _____________________________________________________________________
     *                               LOGIN METHODS
     * ---------------------------------------------------------------------
     */

    /**
     * LOGIN METHOD                                                 <br>
     * _____________________________________________________________<br>
     * Reads user name and password fields                          <br>
     * Uses username and password as parameters in SELECT statement <br>
     * from MySQL DB which should return a single entry or no entry <br>
     * Compares fields to mySQL database                            <br>
     * If username/pass correct launches main menu                  <br>
     * If username/pass incorrect displays error alert              <br>
     * Check if an appointment scheduled within 15 min of login     <br>
     * Display alert if appointment within 15 min of login          <br>
     * @param event on login button click login method executed
     */
    @FXML
    void onActionLogIn(ActionEvent event) {
        //Variables to hold username and pass word entered into form fields
        String userName = userNameTxt.getText();
        String password = passwordTxt.getText();

        //Connect to MySQL database to verify valid username and password
        Connection connection = DBConnection.openConnection();

        try{
            /**
             * Create MySQL SELECT statement from userName provided by user
             * Using both username and passwords as parameters in SELECT statement
             * should return a single result or no result.
              */
            Statement loginStmnt =  connection.createStatement();
            ResultSet loginResult = loginStmnt.executeQuery("SELECT * from U07RO2.user where userName = \"" +
                                                                userName + "\" AND password = \"" + password + "\";");

            /**
             * If the SELECT statement returns a result double check valid match
             * If match = true launch main menu
             * If match = false display error message
             */
            if(loginResult.next()){
                if (userName.equals(loginResult.getString("userName")) && password.equals(loginResult.getString("password"))) {
                    try {

                        user = userName;
                        userID = loginResult.getInt("userId");
                        userLocale = Locale.getDefault();
                        resourceBundle = ResourceBundle.getBundle("Util.login", userLocale);

                        /**
                         * This event attempts button click of the submit button first          |<br>
                         * and if a ClassCastException is received then tries a TextField       |<br>
                         * action event second if the user has pressed enter in the text field  |<br>
                         * to initiate the method <br>                                          |<br>
                         * ---------------------------------------------------------------------|<br>
                         * scene variable holds path to mainmenu file                           |<br>
                         * stage variable loads main menu screen on button click                |<br>
                         */
                        try{
                            stage = (Stage) ((Button) (event.getSource())).getScene().getWindow();
                        }catch (ClassCastException e){
                            stage = (Stage) ((TextField) (event.getSource())).getScene().getWindow();
                        }
                        scene = FXMLLoader.load(getClass().getResource("/view/mainmenu.fxml"));
                        stage.setScene(new Scene(scene));
                        stage.show();

                        //Create or login entry in logfile for user login.
                        LogFile.writeToLog(userName, "login successful.");

                        /**
                         * Check to see if any appointments are within 15 minutes of login time.
                         * If appointment is within 15 min of login time display alert
                         * If no appointment within 15 min do nothing.
                         */
                        if(onLoginAppointmentSoonAlert()){
                            Alert apptSoonAlert = new Alert(Alert.AlertType.INFORMATION);
                            apptSoonAlert.setTitle("UPCOMING APPOINTMENT");
                            apptSoonAlert.setHeaderText("You have an appointment in " + timeToAppt + " minutes.");
                            apptSoonAlert.setContentText("Scheduled appointment info: \n" +
                                                        "Appointment Date & Time: " + start + "\n" +
                                                        "Appointment Type: " + type + "\n" +
                                                        "Appointment Location" + location + "\n" +
                                                        "Appointment Title: " + title );
                            apptSoonAlert.show();
                        }

                        /**
                         *  CATCH CLAUSE - If main menu file not found display error message
                         */
                    } catch (IOException e) {
                        resourceBundle = ResourceBundle.getBundle("Util.login", userLocale);
                        Alert fileNotFoundAlert = new Alert(Alert.AlertType.ERROR);
                        fileNotFoundAlert.setTitle(resourceBundle.getString("fileNotFoundAlertTitle"));
                        fileNotFoundAlert.setHeaderText(resourceBundle.getString("fileNotFoundAlertHead"));
                        fileNotFoundAlert.setContentText(resourceBundle.getString("fileNotFoundAlertText") + " " + e.getCause());
                        fileNotFoundAlert.show();
                    }// Closes catch statement
                }// Closes if (userName && password == result from DB)
                /**
                 * If username and password do not match results returned from DB display error
                 * MySQL Select Statement is not case sensitive - compare operator is case sensitive
                 */
                else{
                    resourceBundle = ResourceBundle.getBundle("Util.login", userLocale);
                    Alert usernameAlert = new Alert(Alert.AlertType.ERROR);
                    usernameAlert.setTitle(resourceBundle.getString("userNameAlertTitle"));
                    usernameAlert.setHeaderText(resourceBundle.getString("userNameAlertHeader"));
                    usernameAlert.setContentText(resourceBundle.getString("userNameAlertText"));
                    usernameAlert.show();
                }
            } //closes if (loginResult from the mySQL DB == true)
            /**
             * If username and password combo does not return result from DB display
             * error message stating wrong username and password.
             */
            else {
                resourceBundle = ResourceBundle.getBundle("Util.login", userLocale);
                Alert usernameAlert = new Alert(Alert.AlertType.ERROR);
                usernameAlert.setTitle(resourceBundle.getString("userNameAlertTitle"));
                usernameAlert.setHeaderText(resourceBundle.getString("userNameAlertHeader"));
                usernameAlert.setContentText(resourceBundle.getString("userNameAlertText"));
                usernameAlert.show();
            }
        //CATCH CLAUSE - if connection is refused from DB display error
        }catch (SQLException e){
            resourceBundle = ResourceBundle.getBundle("Util.login", userLocale);
            Alert failAlert = new Alert(Alert.AlertType.ERROR);
            failAlert.setTitle(resourceBundle.getString("failedConnectionTitle"));
            failAlert.setHeaderText(resourceBundle.getString("failedConnectionHeader"));
            failAlert.setContentText(resourceBundle.getString("failedConnectionText") + " " + e.getMessage());
            failAlert.show();
        }
    }

    /**
     * CHECK APPOINTMENTS WITHIN 15 MIN OF LOGIN    <br>
     * Connect to DB and get all appointments       <br>
     * Check the amount of time to next appointment <br>
     * If < 15 min to next appointment Return true &<br>
     * Set descriptive vars for appointment details <br>
     * If > 15 min to next appointment do nothing & <br>
     * return false                                 <br>
     * @return  false if >15 min to appointment / true if < 15 min to appointment
     */
    boolean onLoginAppointmentSoonAlert(){
        boolean apptSoon = false;

        //Catch SQL exception
        try {
            //Connect to db and get all appointments
            Connection connection = DBConnection.getConnection();
            ResultSet rs = connection.createStatement().executeQuery("SELECT * from U07RO2.appointment;");

            //Check how man min to each appointment
            //If less than 15 min to appointment set appointment detail vars & return true
            //If more than 15 min to appointment do nothing
            while(rs.next()){
                String apptStartTime = rs.getString("start");

                //Call Method to return min to appointment start time from LocalTime Now
                int minToAppt = checkAppointment15Min(rs.getString("start"));

                if(minToAppt > 0 && minToAppt <= 15){
                    timeToAppt = minToAppt;
                    title = rs.getString("title");
                    location = rs.getString("location");
                    type = rs.getString("type");
                    start = TimeConverter.utcToZone(rs.getString("start"));
                    apptSoon = true;
                }
            }
        }catch(SQLException e){
            Logger.getLogger(Login_Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        return apptSoon;
    }

    /**
     * CALCULATE MIN TO APPOINTMENT
     * Compare user local time to start time of appointment
     * @param apptStartTimeString appointment start time to compare
     * @return # of min to next appointment
     */
    int checkAppointment15Min(String apptStartTimeString){

        //Get Local time & convert apptTime from UTC to LocalDateTime
        LocalDateTime userDateTime = LocalDateTime.now();
        LocalDateTime apptZoneTime = TimeConverter.utcToZoneTime(apptStartTimeString);

        //Calculate difference between NOW and ApptStartTime
        //Return difference in min
        Duration timeToAppt = Duration.between(userDateTime, apptZoneTime);
        return (int)timeToAppt.toMinutes();
    }

    /**
     * _____________________________________________________________________
     *                               QUIT BUTTON
     * ---------------------------------------------------------------------
     */

    /**
     *QUIT PROGRAM
     * On action close program
     * @param event pn action execute Quit method
     */
    @FXML
    void onActionQuit(ActionEvent event) {
        stage = (Stage) ((Button)(event.getSource())).getScene().getWindow();
        stage.close();
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            resourceBundle = ResourceBundle.getBundle("Util.login", userLocale);
            userNameLbl.setText(resourceBundle.getString("username"));
            passwordLabl.setText(resourceBundle.getString("password"));
            loginBtn.setText(resourceBundle.getString("login"));
            quitBtn.setText(resourceBundle.getString("quit"));
        }catch(MissingResourceException e){
            Logger.getLogger(Login_Controller.class.getName()).log(Level.SEVERE, null, e);
        }

    }
}
