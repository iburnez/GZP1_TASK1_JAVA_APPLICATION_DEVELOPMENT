package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BusinessTime {
    private String displayTime;
    private String mathTime;
    private static final ObservableList<BusinessTime> businessHoursList = FXCollections.observableArrayList();
    private static final ObservableList<BusinessTime> businessMinuteList = FXCollections.observableArrayList();
    private static final ObservableList<BusinessTime> durationHoursList = FXCollections.observableArrayList();
    private static final ObservableList<BusinessTime> durationMinutesList = FXCollections.observableArrayList();
    private static String[] displayTimeList = new String[]{"12:00 AM", "1:00 AM", "2:00 AM", "3:00 AM", "4:00 AM", "5:00 AM", "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM", "10:00 PM", "11:00 PM"};
    private static String[] mathTimeList = new String[]{"00:00:00", "01:00:00", "02:00:00", "03:00:00", "04:00:00", "05:00:00", "06:00:00", "07:00:00", "08:00:00", "09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00", "17:00:00", "18:00:00", "19:00:00", "20:00:00", "21:00:00", "22:00:00", "23:00:00"};
    private static String[] displayMinuteList = new String[]{"00:00", "00:15", "00:30", "00:45"};
    private static String[] mathMinuteList = new String[]{"00", "15", "30", "45"};
    private static String[] displayDurationHour = new String[]{"0 Hours", "1 Hours", "2 Hours","3 Hours", "4 Hours", "5 Hours","6 Hours", "7 Hours", "8 Hours"};
    private static String[] mathDurationHour = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"};
    private static String[] displayDurationMinute = new String[]{"0 Minutes", "15 Minutes", "30 Minutes", "45 Minutes"};
    private static String[] mathDurationMinute = new String[]{"0", "15", "30", "45"};

    /**
     * BusinessTime Constructor
     * @param displayTime - time displayed in combo boxes
     * @param mathTime - time used for math equations to calculate start and end times for appointments
     */
    public BusinessTime(String displayTime, String mathTime){
        setDisplayTime(displayTime);
        setMathTime(mathTime);
    }

    /**
     * ______________________________________________________
     *                         SETTERS
     * ------------------------------------------------------
     */

    /**
     * SET DISPLAY TIME
     * @param displayTime - set param as displayTime var
     */
    public void setDisplayTime(String displayTime){
        this.displayTime = displayTime;
    }

    /**
     * SET MATH TIME
     * @param mathTime - set param as mathTime var
     */
    public void setMathTime(String mathTime){
        this.mathTime = mathTime;
    }

    /**
     * ______________________________________________________
     *                         GETTERS
     * ------------------------------------------------------
     */

    /**
     * GET DISPLAY TIME
     * @return - return display time
     */
    public String getDisplayTime(){
        return displayTime;
    }

    /**
     * GET MATH TIME
     * @return math time
     */
    public String getMathTime(){
        return mathTime;
    }

    /**
     * GET BUSINESS HOURS OBSERVABLE LIST
     * @return  all items in business minutes OL
     */
    public static ObservableList<BusinessTime> getBusinessHoursList(){
        return businessHoursList;
    }

    /**
     * GET BUSINESS MINUTES OBSERVABLE LIST
     * @return all items in business minutes OL
     */
    public static ObservableList<BusinessTime> getBusinessMinuteList(){
        return businessMinuteList;
    }

    /**
     * GET DURATION HOURS OBSERVABLE LIST
     * @return  all items in duration hours OL
     */
    public static ObservableList<BusinessTime> getDurationHoursList(){
        return durationHoursList;
    }

    /**
     * GET DURATION MINUTES OBSERVABLE LIST
     * @return all items in duration minutes OL
     */
    public static ObservableList<BusinessTime> getDurationMinutesList(){
        return durationMinutesList;
    }

    /**
     * GET BUSINESS HOUR OBJECT BY MATH HOUR                                <br>
     * Loop through getBusinessHoursList and compare object Mathtime        <br>
     * to Param MathTime. If a match return the object, no match return null<br>
     * @param hours use math time to lookup BusinessTime Object
     * @return business time object
     */
    public static BusinessTime getbHoursByMath(String hours){
        //convert param hours to MathTime format
        String convertHours = hours + ":00:00";
        BusinessTime foundHours = null;

        for(int i = 0; i < getBusinessHoursList().size(); i++){
            if(getBusinessHoursList().get(i).getMathTime().equals(convertHours)){
                foundHours = getBusinessHoursList().get(i);
            }
        }
        return foundHours;
    }

    /**
     * GET BUSINESS MINUTES OBJECT BY MATH MINUTES                          <br>
     * Loop through getBusinessMinuteList and compare object Mathtime       <br>
     * to Param MathTime. If a match return the object, no match return null<br>
     * @param mins use math time to lookup BusinessTime Object
     * @return business time object
     */
    public static BusinessTime getbMinsByMath(String mins){
        BusinessTime foundMins = null;

        for(int i = 0; i < getBusinessMinuteList().size(); i++){
            if(getBusinessMinuteList().get(i).getMathTime().equals(mins)){
                foundMins = getBusinessMinuteList().get(i);
            }
        }
        return foundMins;
    }

    /**
     * GET DURATION HOUR OBJECT BY MATH HOUR                                <br>
     * Loop through getDurationHoursList and compare object Mathtime        <br>
     * to Param MathTime. If a match return the object, no match return null<br>
     * @param mathTime use math time to lookup BusinessTime Object
     * @return business time object
     */
    public static BusinessTime getDhourByMath(String mathTime){
        BusinessTime dHour = null;
        for(int i = 0; i < getDurationHoursList().size(); i++){
            if(getDurationHoursList().get(i).getMathTime().equals(mathTime)){
                dHour = getDurationHoursList().get(i);
            }
        }
        return dHour;
    }

    /**
     * GET DURATION MINUTES OBJECT BY MATH MINUTES                          <br>
     * Loop through getDurationMinutesList and compare object Mathtime      <br>
     * to Param MathTime. If a match return the object, no match return null<br>
     * @param mathTime use math time to lookup BusinessTime Object
     * @return business time object
     */
    public static BusinessTime getDMinByMath(String mathTime){
        BusinessTime dMin = null;
        for(int i = 0; i < getDurationMinutesList().size(); i++){
            if(getDurationMinutesList().get(i).getMathTime().equals(mathTime)){
                dMin = getDurationMinutesList().get(i);
            }
        }
        return dMin;
    }

    /**
     * ______________________________________________________
     *                BUILD OBSERVABLE LISTS
     * ------------------------------------------------------
     */

    /**
     * ADD BUSINESS TIME OBJECT TO businessHoursList OL
     * @param businessHours add BusinessTime object to OL
     */
    public static void addBusinessHoursList(BusinessTime businessHours){
        businessHoursList.add(businessHours);
    }

    /**
     * ADD BUSINESS TIME OBJECT TO BusinessMinuteList OL
     * @param businessHours add BusinessTime object to OL
     */
    public static void addBusinessMinuteList(BusinessTime businessHours){
        businessMinuteList.add(businessHours);
    }

    /**
     * ADD BUSINESS TIME OBJECT TO durationHoursList OL
     * @param businessHours add BusinessTime object to OL
     */
    public static void addDurationHoursList(BusinessTime businessHours){
        durationHoursList.add(businessHours);
    }

    /**
     * ADD BUSINESS TIME OBJECT TO durationMinutesList OL
     * @param businessHours add BusinessTime object to OL
     */
    public static void addDurationMinutesList(BusinessTime businessHours){
        durationMinutesList.add(businessHours);
    }

    /**
     * BUILD BusinessHoursList OL                               <br>
     * Clear list before build to prevent duplicates.           <br>
     * USE displayTimeList & mathTimeList arrays to build list. <br>
     */
    public static void buildBusinessHoursList(){
        if(!(businessHoursList.isEmpty())){
            businessHoursList.clear();
        }

        for(int i = 0; i < displayTimeList.length; i++){
            addBusinessHoursList(new BusinessTime(displayTimeList[i], mathTimeList[i]));
        }
    }

    /**
     * BUILD BusinessMinuteList OL                                  <br>
     * Clear list before build to prevent duplicates.               <br>
     * USE displayMinuteList & mathMinuteList arrays to build list  <br>
     */
    public static void buildBusinessMinuteList(){
        if(!(businessMinuteList.isEmpty())){
            businessMinuteList.clear();
        }

        for(int i = 0; i < displayMinuteList.length; i++){
            addBusinessMinuteList(new BusinessTime(displayMinuteList[i], mathMinuteList[i]));
        }
    }

    /**
     * BUILD DurationHoursList OL                                       <br>
     * Clear list before build to prevent duplicates.                   <br>
     * USE displayDurationHour & mathDurationHour arrays to build list. <br>
     */
    public static void buildDurationHoursList(){
        if(!(durationHoursList.isEmpty())){
            durationHoursList.clear();
        }

        for(int i = 0; i < displayDurationHour.length; i++){
            addDurationHoursList(new BusinessTime(displayDurationHour[i], mathDurationHour[i]));
        }
    }

    /**
     * BUILD DurationMinutesList OL                                         <br>
     * Clear list before build to prevent duplicates.                       <br>
     * USE displayDurationMinute & mathDurationMinute arrays to build list. <br>
     */
    public static void buildDurationMinutesList(){
        if(!(durationMinutesList.isEmpty())){
            durationMinutesList.clear();
        }

        for(int i = 0; i < displayDurationMinute.length; i++){
            addDurationMinutesList(new BusinessTime(displayDurationMinute[i], mathDurationMinute[i]));
        }
    }

}
