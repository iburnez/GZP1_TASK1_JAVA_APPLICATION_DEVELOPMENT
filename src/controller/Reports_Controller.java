package controller;

import Util.DBConnection;
import Util.LogFile;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.util.StringConverter;
import model.Appointment;
import model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Reports_Controller implements Initializable {

    ObservableList<ObservableList> typeByMonthList = FXCollections.observableArrayList();
    ObservableList<ObservableList> userAppointments = FXCollections.observableArrayList();
    ObservableList<ObservableList> typeAppointments = FXCollections.observableArrayList();
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
    private Button customerMenuBtn1;
    @FXML
    private Button apptTypeByMonthBtn;
    @FXML
    private ChoiceBox<User> consultantScheduleBtn;
    @FXML
    private Button scheduleGenerateBtn;
    @FXML
    private ChoiceBox<String> typeScheduleBtn1;
    @FXML
    private Button apptTypeSchedReportBtn;
    @FXML
    private TableView<ObservableList> appointmentsTable;
    @FXML
    private Button exportReportBtn;
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
     *QUIT PROGRAM
     * On action close DB connection & close program
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
     *                         REPORT ITEMS
     * ---------------------------------------------------------------------
     */

    /**
     * GENERATE # OF APPOINTMENT TYPE BY MONTH REPORT                                       <br>
     * -------------------------------------------------------------------------------------<br>
     * Clear other Report selections                                                        <br>
     * Clear Tableview                                                                      <br>
     * Clear all Observable lists to prevent displaying unwanted date                       <br>
     * Run SQL query to generate report                                                     <br>
     * Get # of columns in SQL report                                                       <br>
     * Run loop to create columns in tableview - get name for columns from SQL column name  <br>
     * Create SQL ROW Observable list to hold data for each SQL row                         <br>
     * Run loop to populate SQLROW OL with each column field for each row in SQL report.    <br>
     * Add each SQLRow list to Observable list                                              <br>
     * Display report contents in tableview                                                 <br>
     * @param event on buttnon execute method
     */
    @FXML
    void onActionGenerateApptTypeByMonth(ActionEvent event) {
        //Clear other Report selections
        //Clear Tableview
        consultantScheduleBtn.getSelectionModel().clearSelection();
        typeScheduleBtn1.getSelectionModel().clearSelection();
        appointmentsTable.getColumns().clear();

        //Clear Observable list to prevent unwanted data
        typeByMonthList.clear();
        userAppointments.clear();
        typeAppointments.clear();

        try {
            //Run SQL query to generate report
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Year(start) as \"Year\", Month(start) as \"Month\"" +
                                                    ", type as \"Appointment Type\", count(*) as \"# of Appointments\" " +
                                                    "from U07RO2.appointment group by Year(start), Month(start), type;");

            //Get # of columns in SQL report
            //Run loop to create columns in tableview - get name for columns from SQL column name
            for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
                int j = i;
                TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param){
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                //add each column to tableview
                appointmentsTable.getColumns().add(col);
            }

            while(resultSet.next()){

                //Create SQL ROW Observable list to hold data for each SQL row
                ObservableList<String> sqlString = FXCollections.observableArrayList();

                //Run loop to populate SQLROW OL with each column field for each row in SQL report.
                //Add each SQLRow list to Observable list
                for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
                    sqlString.add(resultSet.getString(i+1));
                }
                typeByMonthList.add(sqlString);

            }

        }catch(SQLException e){
            Logger.getLogger(Reports_Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        //Display report contents in tableview
        appointmentsTable.setItems(typeByMonthList);

        //Log activity to log file
        LogFile.writeToLog(Login_Controller.getUser(), "Report generated: # OF APPOINTMENT TYPE BY MONTH REPORT.");
    }

    /**
     * GENERATE CONSULTANT(USER) SCHEDULE REPORT                                            <br>
     * -------------------------------------------------------------------------------------<br>
     * Clear other Report selections                                                        <br>
     * Clear Tableview                                                                      <br>
     * Clear all Observable lists to prevent displaying unwanted date                       <br>
     * Run SQL query to generate report                                                     <br>
     * Get # of columns in SQL report                                                       <br>
     * Run loop to create columns in tableview - get name for columns from SQL column name  <br>
     * Create SQL ROW Observable list to hold data for each SQL row                         <br>
     * Run loop to populate SQLROW OL with each column field for each row in SQL report.    <br>
     * Add each SQLRow list to Observable list                                              <br>
     * Display report contents in tableview                                                 <br>
     * @param event on button click generate report & display in tableview
     */
    @FXML
    void onActionGenerateUserSchedule(ActionEvent event) {
        //Clear other Report selections
        //Clear Tableview
        typeScheduleBtn1.getSelectionModel().clearSelection();
        appointmentsTable.getColumns().clear();


        //Catch NullPointerException if no User is Selected and Display Error Dialog
        try {
            //Get User selected from combo box & User ID for SQL Query
            //If No selection User is selected display error message
            User selectedUser = consultantScheduleBtn.getSelectionModel().getSelectedItem();
            int userID = selectedUser.getUserID();

            //Clear list to remove entries from previous reports
            typeByMonthList.clear();
            userAppointments.clear();
            typeAppointments.clear();

            //Catch SQL SQLException for statement/result set
            try {
                //Run SQL query to generate report
                Connection connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM U07RO2.appointment where userId = " + userID + " ORDER BY start;");

                //Get # of columns in SQL report
                //Run loop to create columns in tableview - get name for columns from SQL column name
                //Add columns to tableview
                for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
                    int j = i;
                    TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i+1));
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                    appointmentsTable.getColumns().add(col);
                }

                while(resultSet.next()){

                    //Create SQL ROW Observable list to hold data for each SQL row
                    ObservableList<String> sqlRow = FXCollections.observableArrayList();

                    //Run loop to populate SQLROW OL with each column field for each row in SQL report.
                    //Add each SQLRow list to Observable list
                    for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++){
                        sqlRow.add(resultSet.getString(i + 1));
                    }
                    userAppointments.add(sqlRow);
                }
                resultSet.close();

            }catch(SQLException e){
                Logger.getLogger(Reports_Controller.class.getName()).log(Level.SEVERE, null, e);
            }

            //Display report contents in tableview
            appointmentsTable.setItems(userAppointments);
        }catch(NullPointerException e){
            Alert nullSelection = new Alert(Alert.AlertType.ERROR);
            nullSelection.setTitle("ERROR NO USER SELECTED");
            nullSelection.setHeaderText("Cannot Generate Report:");
            nullSelection.setContentText("Please select a user from the drop down list to generate a report.");
            nullSelection.show();
        }

        //Log activity to log file
        LogFile.writeToLog(Login_Controller.getUser(), "Report generated: CONSULTANT(USER) SCHEDULE REPORT.");
    }

    /**
     * GENERATE APPOINTMENT TYPE SCHEDULE REPORT                                            <br>
     * -------------------------------------------------------------------------------------<br>
     * Clear other Report selections                                                        <br>
     * Clear Tableview                                                                      <br>
     * Clear all Observable lists to prevent displaying unwanted date                       <br>
     * Run SQL query to generate report                                                     <br>
     * Get # of columns in SQL report                                                       <br>
     * Run loop to create columns in tableview - get name for columns from SQL column name  <br>
     * Create SQL ROW Observable list to hold data for each SQL row                         <br>
     * Run loop to populate SQLROW OL with each column field for each row in SQL report.    <br>
     * Add each SQLRow list to Observable list                                              <br>
     * Display report contents in tableview                                                 <br>
     * @param event on button click generate report & display in tableview
     */
    @FXML
    void OnActionApptTypeSchedule(ActionEvent event) {
        //Clear other Report selections
        //Clear Tableview
        consultantScheduleBtn.getSelectionModel().clearSelection();
        appointmentsTable.getColumns().clear();

        //Get selected Appointment Type from drop down box for SQL Querey
        String selectedType = typeScheduleBtn1.getSelectionModel().getSelectedItem();

        //Check if user has selected ApptType and display error if no type has been selected
        if(selectedType != null) {

            //Clear lists to remove entries from previous reports
            typeByMonthList.clear();
            userAppointments.clear();
            typeAppointments.clear();

            try {
                //Run SQL query to generate report
                Connection connection = DBConnection.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM U07RO2.appointment where type = " + "\"" + selectedType + "\"" + ";");

                //Get # of columns in SQL report
                //Run loop to create columns in tableview - get name for columns from SQL column name
                //Add Columns to table view
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    final int j = i;
                    TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                    appointmentsTable.getColumns().addAll(col);
                }

                while (resultSet.next()) {
                    //Create SQL ROW Observable list to hold data for each SQL row
                    ObservableList<String> sqlRow = FXCollections.observableArrayList();

                    //Run loop to populate SQLROW OL with each column field for each row in SQL report.
                    //Add each SQLRow list to Observable list
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        sqlRow.add(resultSet.getString(i + 1));
                    }
                    typeAppointments.add(sqlRow);
                }
            } catch (SQLException e) {
                Logger.getLogger(Reports_Controller.class.getName()).log(Level.SEVERE, null, e);
            }

            //Display report contents in tableview
            appointmentsTable.setItems(typeAppointments);
        }else{
            Alert nullSelection = new Alert(Alert.AlertType.ERROR);
            nullSelection.setTitle("ERROR NO APPOINTMENT TYPE SELECTED");
            nullSelection.setHeaderText("Cannot Generate Report:");
            nullSelection.setContentText("Please select an Appointment Type from the drop down list to generate a report.");
            nullSelection.show();
        }

        //Log activity to log file
        LogFile.writeToLog(Login_Controller.getUser(), "Report generated: APPOINTMENT TYPE SCHEDULE REPORT.");
    }

    /**
     * _____________________________________________________________________
     *                         EXPORT REPORT TO FILE
     * ---------------------------------------------------------------------
     */

    /**
     * EXPORT REPORT TO FILE        <br>
     * Create a file writer         <br>
     * Check which report has been  <br>
     * Generated and write to CSV   <br>
     * Log in log file              <br>
     * @param event on button click execute method
     */
    @FXML
    void onActionExportReport(ActionEvent event) {

        try {
            String fileName = "Appointment_Report_";
            File exportReport = new File("../Performance Assessment/Reports/" + fileName + LocalDateTime.now() + ".csv");
            FileWriter reportOutPut = new FileWriter(exportReport, false);
            PrintWriter reportWriter = new PrintWriter(reportOutPut);

            if(!(typeByMonthList.isEmpty())) {
                for (int i = 0; i < typeByMonthList.size(); i++) {
                    reportWriter.println(typeByMonthList.get(i));
                }
                Alert successWrite = new Alert(Alert.AlertType.INFORMATION);
                successWrite.setTitle("File Has Been Exported");
                successWrite.setHeaderText("Report Generation Successful");
                successWrite.setContentText("File can be found in the report folder of the Performance Assessment directory.");
                successWrite.show();

                //Log activity to log file
                LogFile.writeToLog(Login_Controller.getUser(), "Report Exported: # OF APPOINTMENT TYPE BY MONTH REPORT.");
            }
            if(!(userAppointments.isEmpty())){
                for (int i = 0; i < userAppointments.size(); i++) {
                    reportWriter.println(userAppointments.get(i));
                }
                Alert successWrite = new Alert(Alert.AlertType.INFORMATION);
                successWrite.setTitle("File Has Been Exported");
                successWrite.setHeaderText("Report Generation Successful");
                successWrite.setContentText("File can be found in the report folder of the Performance Assessment directory.");
                successWrite.show();

                //Log activity to log file
                LogFile.writeToLog(Login_Controller.getUser(), "Report Exported: CONSULTANT(USER) SCHEDULE REPORT.");
            }

            if(!(typeAppointments.isEmpty())){
                for (int i = 0; i < typeAppointments.size(); i++) {
                    reportWriter.println(typeAppointments.get(i));
                }
                Alert successWrite = new Alert(Alert.AlertType.INFORMATION);
                successWrite.setTitle("File Has Been Exported");
                successWrite.setHeaderText("Report Generation Successful");
                successWrite.setContentText("File can be found in the report folder of the Performance Assessment directory.");
                successWrite.show();

                //Log activity to log file
                LogFile.writeToLog(Login_Controller.getUser(), "Report Exported: APPOINTMENT TYPE SCHEDULE REPORT.");
            }
            if(typeByMonthList.isEmpty() && userAppointments.isEmpty() && typeAppointments.isEmpty()){
                Alert failedWrite = new Alert(Alert.AlertType.INFORMATION);
                failedWrite.setTitle("File Export Failure");
                failedWrite.setHeaderText("No Report Has been exported.");
                failedWrite.setContentText("Please select and generate a report prior to export.");
                failedWrite.show();
            }
            reportWriter.close();

        }catch(IOException e){
            Logger.getLogger(Reports_Controller.class.getName()).log(Level.SEVERE, null, e);
        }


    }

    /**
     * _____________________________________________________________________
     *                         INITIALIZE METHOD
     * ---------------------------------------------------------------------
     */

    /**
     * INITIALIZE REPORTS MENU                                                              <br>
     * -------------------------------------------------------------------------------------<br>
     * Populate Select Consultant(Users) combobox and display username                      <br>
     * Create and initialize Appointment Type OL                                            <br>
     * Populate Select Appointment Type combobox and display Type                           <br>
     * Include StringConverter in Combobox to display approriate variable from Object List  <br>
     * Override toString to return approriate variable to display in comboboxes             <br>
     * Override fromString method to object matching selection from combobox                <br>
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        User.buildAllUsers();
        consultantScheduleBtn.setItems(User.getAllUsers());
        //Use StringConvert to display userName in comboBox dropdown
        StringConverter<User> userConverter = new StringConverter<User>() {
            //OverRide toString method to display userName in combobox representing
            //each user object in the combobox
            @Override
            public String toString(User user) {
                return user.getUserName();
            }

            //OverRide fromString method to locate matching user object from selected
            //userName in comboBox dropdown and return matching customer object
            @Override
            public User fromString(String s) {
                return User.getAllUsers().stream().filter(user -> user.getUserName()
                                        .equals(s)).collect(Collectors.toList()).get(0);
            }
        };
        consultantScheduleBtn.setConverter(userConverter);

        //Create observable list for ApptType (Only used in reports so created here rather than in Appointment)
        //Clear list so each time the reports page is initialized it does not add to existing list
        ObservableList<String> apptTypes = FXCollections.observableArrayList();
        apptTypes.clear();

        try {
            //Run DISTINCT SQL Query to get all different appointment types
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT type FROM U07RO2.appointment");

            //add each apointment type to apptTypes OL
            while(resultSet.next()){
                String type = resultSet.getString("type");
                apptTypes.add(type);
            }

        }catch (SQLException e){
            Logger.getLogger(Reports_Controller.class.getName()).log(Level.SEVERE, null, e);
        }
        typeScheduleBtn1.setItems(apptTypes);


    }
}
