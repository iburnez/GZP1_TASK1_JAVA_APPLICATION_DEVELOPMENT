package model;

import Util.DBConnection;
import Util.LogFile;
import Util.TimeConverter;
import controller.Login_Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Appointment {

    private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private int appointmentID;
    private int customerID;
    private int userID;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String appointmentURL;
    private String start;
    private String end;
    private String createdDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;

    /**
     * Default Appointment constructor
     * @param appointmentID sets appointmentID
     * @param customerID sets customerID
     * @param userID sets userID
     * @param title sets title
     * @param description sets description
     * @param location sets location
     * @param contact sets contact
     * @param type sets type
     * @param appointmentURL sets appointmentURL
     * @param start sets start
     * @param end sets end
     * @param createdDate sets createdDate
     * @param createdBy sets createdBy
     * @param lastUpdate sets lastUpdate
     * @param lastUpdatedBy sets lastUpdatedBy
     */
    public Appointment(int appointmentID, int customerID, int userID, String title, String description, String location, String contact, String type, String appointmentURL, String start, String end, String createdDate, String createdBy, String lastUpdate, String lastUpdatedBy){
        setAppointmentID(appointmentID);
        setCustomerID(customerID);
        setUserID(userID);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setContact(contact);
        setType(type);
        setAppointmentURL(appointmentURL);
        setStart(start);
        setEnd(end);
        setCreatedDate(createdDate);
        setCreatedBy(createdBy);
        setLastUpdate(lastUpdate);
        setLastUpdatedBy(lastUpdatedBy);
    }

    /**
     * ______________________________________________________
     *                         SETTERS
     * ------------------------------------------------------
     */

    public void setAppointmentID(int appointmentID){
        this. appointmentID = appointmentID;
    }

    public void setCustomerID(int customerID){
        this.customerID = customerID;
    }

    public void setUserID(int userID){
        this.userID = userID;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setContact(String contact){
        this.contact = contact;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setAppointmentURL(String appointmentURL){
        this.appointmentURL = appointmentURL;
    }

    public void setStart(String start){
        this.start = start;
    }

    public void setEnd(String end){
        this.end = end;
    }

    public void setCreatedDate(String createdDate){
        this.createdDate = createdDate;
    }

    public void setCreatedBy(String createdBy){
        this.createdBy = createdBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy){
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setLastUpdate(String lastUpdate){
        this.lastUpdate = lastUpdate;
    }

    /**
     * ______________________________________________________
     *                         GETTERS
     * ------------------------------------------------------
     */

    public int getAppointmentID(){
        return appointmentID;
    }

    public int getCustomerID(){
        return customerID;
    }

    public int getUserID(){
        return userID;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContact(){
        return contact;
    }

    public String getType(){
        return type;
    }

    public String getAppointmentURL(){
        return appointmentURL;
    }

    public String getStart(){
        return TimeConverter.utcToZone(start);
    }

    public String getEnd(){
        return TimeConverter.utcToZone(end);
    }

    /**
     * Get Start DateTime in UTC
     * @return start DateTime in UTC
     */
    public String getStartUTC(){
        return start;
    }

    /**
     * Get End DateTime in UTC
     * @return End DateTime in UTC
     */
    public String getEndUTC(){
        return end;
    }

    /**
     * Get Appt Start & Convert from UTC to LocalTime
     * @return appt start in localtime
     */
    public String getCreatedDate(){
        return TimeConverter.utcToZone(createdDate);
    }

    public String getCreatedBy(){
        return createdBy;
    }

    /**
     * Get Appt end & Convert from UTC to LocalTime
     * @return appt end in localtime
     */
    public String getLastUpdate(){
        return TimeConverter.utcToZone(lastUpdate);
    }

    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }

    public static void clearAppointmentList(){
        appointmentList.clear();
    }

    /**
     * ______________________________________________________
     *                OBSERVABLE LIST CONTROLS
     * ------------------------------------------------------
     */

    /**
     * ADD APPOINTMENT TO OBSERVABLE LIST
     * @param appointment add appointment object to observable list
     */
    public static void addAppointment(Appointment appointment){
        appointmentList.add(appointment);
    }

    /**
     * REMOVE APPOINTMENT FROM ALL APPOINTMENTS OL
     * @param appointment remove appointment from appointments OL
     */
    public static void removeAppointment(Appointment appointment){
        appointmentList.remove(appointment);
    }

    /**
     * BUILD APPOINTMENT LIST OBSERVABLE LIST FROM MySQL DB <br>
     * ---------------------------------------------------------------------------- |<br>
     * Check if list has entries and clear or duplicates will be created on each    |<br>
     * screen load.                                                                 |<br>
     * Create a connection to the database                                          |<br>
     * Select all appointments from appointment table                               |<br>
     * Loop through results and use each field entry to populate Appointment Object |<br>
     * Save each Appointment object to Observable list                              |<br>
     * If an SQL exception display error in console                                 |<br>
     */
    public static void buildAppointmentList(){
        //Clear appointment list each build to prevent duplicate entries.
        if(!(appointmentList.isEmpty())){appointmentList.clear();}

        //Try statement to handle SQL exception.
        try{

            //get existing connection to DB & create new statement
            Connection connection = DBConnection.getConnection();
            Statement stmnt = connection.createStatement();

            //Create and execute new search statement in MySQL Database
            ResultSet resultList = stmnt.executeQuery("SELECT * FROM U07RO2.appointment;");

            //Loop through results from MySQL DB and save each Field to variable
            while(resultList.next()){
                int appointmentID = Integer.parseInt(resultList.getString("appointmentId"));
                int customerID = Integer.parseInt(resultList.getString("customerId"));
                int userID = Integer.parseInt(resultList.getString("userId"));
                String title = resultList.getString("title");
                String description = resultList.getString("description");
                String location = resultList.getString("location");
                String contact = resultList.getString("contact");
                String type = resultList.getString("type");
                String appointmentURL = resultList.getString("url");
                String start = resultList.getString("start");
                String end = resultList.getString("end");
                String createdDate = resultList.getString("createDate");
                String createdBy = resultList.getString("createdBy");
                String lastUpdate = resultList.getString("lastUpdate");
                String lastUpdatedBy = resultList.getString("lastUpdateBy");

                /**
                 * Call Appointment Constructor and use new variables to populate fields
                 */
                addAppointment(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, appointmentURL, start, end, createdDate, createdBy, lastUpdate, lastUpdatedBy));
            }

        //CATCH CLAUSE - catch potential SQLException and display error in console
        } catch(SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * GET ALL APPOINTMENTS OBSERVABLE LIST
     * @return return AppointmentList
     */
    public static ObservableList<Appointment> getAppointmentList(){
        return appointmentList;
    }

    /**
     * ______________________________________________________
     *                      AUTO GEN ID
     * ------------------------------------------------------
     */

    /**
     *AUTOGENERATE APPOINTMENT ID                   <br>
     * Search MySQL Database for max AppointmentID  <br>
     * Add 1 to MaxAppointmentID and return value   <br>
     * @return unique appointment ID
     */
    public static int autoGenID() {
        int appointmentID = 0;

        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.appointment;");

            //Search MySQL Database for max AppointmentID
            while (resultSet.next()) {
                int sqlAppointmentID = resultSet.getInt("appointmentId");
                if (sqlAppointmentID > appointmentID) {
                    appointmentID = sqlAppointmentID;
                }
            }
        }catch(SQLException e){
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE,null, e);
        }

        //Add 1 to MaxAppointmentID and return value
        ++appointmentID;
        return appointmentID;
    }

    /**
     * ______________________________________________________
     *               APPOINTMENT SQL CONTROLS
     * ------------------------------------------------------
     */

    /**
     * ADD OR UPDATE APPOINTMENT IN SQL                  <br>
     * -------------------------------------------------|<br>
     * Check to see if Appointment already exists in DB |<br>
     * If appointment exists - preform update           |<br>
     * If appointment does not exist - preform insert   |<br>
     * @param appointmentID Sets appointmentID in SQL
     * @param customerID Sets customerID in SQL
     * @param userID Sets userID in SQL
     * @param title Sets title in SQL
     * @param description Sets description in SQL
     * @param location Sets location in SQL
     * @param contact Sets contact in SQL
     * @param type Sets type in SQL
     * @param appointmentURL Sets appointmentURL in SQL
     * @param start Sets start in SQL
     * @param end Sets end in SQL
     */
    public static void addOrUpdateSQLAppointment( int appointmentID, int customerID, int userID, String title, String description, String location, String contact, String type, String appointmentURL, String start, String end){
        //Declare Vars for method
        //Initialize TimeDate Vars with Current TimeDate
        //Initialize User Vars with Current User
        String appointmentQuery;
        PreparedStatement appointmentPrepStatement;
        String createdDate = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
        String createdBy = Login_Controller.getUser();
        String lastUpdated = String.valueOf(TimeConverter.dateTimeToUTC(ZonedDateTime.now()));
        String lastUpdatedBy = Login_Controller.getUser();

        Connection connection = DBConnection.getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from U07RO2.appointment where appointmentId = " + appointmentID + ";");

            //Check if appointment already exists in DB
            //If does not exist insert new Appointment into DB
            if(!(resultSet.next())){
                //New Appointments get new ID
                appointmentID = Appointment.autoGenID();
                appointmentQuery = "INSERT INTO U07RO2.appointment VALUES (" + appointmentID + ", " + customerID + ", "
                         + userID + ", " + "\"" + title + "\"" + ", " + "\"" + description + "\"" + ", " + "\""
                        + location + "\"" + ", " + "\"" + contact + "\"" + ", " + "\"" + type + "\"" + ", " + "\""
                        + appointmentURL + "\"" +  ", " + "\"" + start + "\"" +  ", " + "\"" + end + "\"" + ", " + "\""
                        + createdDate + "\"" + ", " + "\"" + createdBy + "\"" + ", " + "\"" + lastUpdated + "\"" + ", "
                        + "\"" + lastUpdatedBy + "\"" + ");";

                //Log activity to logfile
                LogFile.writeToLog(Login_Controller.getUser(), "Add New Appointment Successful: AppointmentID ", String.valueOf(appointmentID));

            //If appointment already exists in DB Update appointment with new info
            }else {
                //Initialize creation vars with data from DB
                createdDate = resultSet.getString("createDate");
                createdBy =  resultSet.getString("createdBy");
                appointmentQuery = "UPDATE U07RO2.appointment SET customerId= " + customerID + ", userId = " + userID
                        + ", title = " + "\"" + title + "\"" + ", description = " + "\"" + description + "\""
                        + ", location = " + "\"" + location + "\"" + ", contact = " + "\"" + contact + "\""
                        + ", type = " + "\"" + type + "\"" + ", url = " + "\"" + appointmentURL + "\""
                        +  ", start = " + "\"" + start + "\"" +  ", end = " + "\"" + end + "\"" + ", createDate = "
                        + "\"" + createdDate + "\"" + ", createdBy = " + "\"" + createdBy + "\"" + ", lastUpdate = "
                        + "\"" + lastUpdated + "\"" + ", lastUpdateBy = " + "\"" + lastUpdatedBy + "\""
                        + "where appointmentId = " + appointmentID + ";";

                //Log activity to logfile
                LogFile.writeToLog(Login_Controller.getUser(), "Update Appointment Successful: AppointmentID ", String.valueOf(appointmentID));
            }
            //Write statement to DB and close statement
            appointmentPrepStatement = connection.prepareStatement(appointmentQuery);
            appointmentPrepStatement.execute();
            appointmentPrepStatement.close();
            resultSet.close();

        }catch (SQLException e){
            Logger.getLogger(Appointment.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * DELETE APPOINTMENT FROM SQL                           <br>
     * -----------------------------------------------------|<br>
     * Prior to delete display confirmation dialog to user  |<br>
     * If user confirms action preform delete               |<br>
     * Connect to DB and delete selected Appointment by ID
     * @param appointmentID locate appointment using ID in SQL Query
     */
    public static void deleteSQLAppointment(int appointmentID){
        try {
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE from U07RO2.appointment where appointmentId = " + appointmentID + ";");

            //Log activity to logfile
            LogFile.writeToLog(Login_Controller.getUser(), "Delete Appointment Successful: AppointmentID ", String.valueOf(appointmentID));

        }catch(SQLException e){
            Alert noneSelected = new Alert(Alert.AlertType.ERROR);
            noneSelected.setTitle("ERROR");
            noneSelected.setHeaderText("Could Not Delete Appointment: ");
            noneSelected.setContentText("Error could not delete Appointment.\n" + e.getMessage());
            noneSelected.show();

            //Log activity to logfile
            LogFile.writeToLog(Login_Controller.getUser(), "Delete Appointment Failed: AppointmentID ", String.valueOf(appointmentID));
        }
    }

}
