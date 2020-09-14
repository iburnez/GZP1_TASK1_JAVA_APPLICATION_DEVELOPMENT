package Util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeConverter {
    /**
     * ______________________________________________________
     *                 TIME CONVERSION METHODS
     * ------------------------------------------------------
     */

    /**
     * CONVERT ZONED TIME TO UTC TIME       <br>
     * take zonedTime var and convert to UTC<br>
     * @param timeZoned zoned time var
     * @return UTC time var
     */
    public static LocalDateTime dateTimeToUTC(ZonedDateTime timeZoned){
        LocalDateTime timeUTC = timeZoned.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
       return timeUTC;
    }

    /**
     * CONVERT ZONED TIME TO UTC TIME - STRING      <br>
     * take zonedTime STRING var and convert to UTC <br>
     * @param timeZonedString string of zonedtime var
     * @return UTC Time
     */
    public static LocalDateTime dateTimeToUTC(String timeZonedString){
        ZonedDateTime timeZoned = ZonedDateTime.parse(timeZonedString);
        LocalDateTime timeUTC = timeZoned.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        return timeUTC;
    }

    /**
     * CONVERT UTC TO ZONED TIME                                    <br>
     * -------------------------------------------------------------<br>
     * Take a string of UTC Time convert to LocalDateTime UTC       <br>
     * Convert LocalDateTime-UTC to ZonedTime-UTC                   <br>
     * Convert ZonedTime-UTC to ZonedTime-LocalSysTime              <br>
     * Convert ZonedTime-LocalSysTime to LocalDateTime-LocalSysTime <br>
     * Convert & Return LocalDateTime-LocalSysTime to String        <br>
     * @param timeUTCString String of UTC time
     * @return String of ZonedTime
     */
    public static String utcToZone(String timeUTCString){
        Timestamp utcTimeStamp = Timestamp.valueOf(timeUTCString);
        LocalDateTime timeUTC = utcTimeStamp.toLocalDateTime();
        ZonedDateTime timeWithUTC = timeUTC.atZone(ZoneId.of("UTC"));
        ZonedDateTime utcToLocal = timeWithUTC.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        LocalDateTime localTime = utcToLocal.toLocalDateTime();
        DateTimeFormatter ldtFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String localTimeString = localTime.format(ldtFormatter);

        return localTimeString;
    }

    /**
     * CONVERT UTC TO ZONED TIME                                    <br>
     * -------------------------------------------------------------<br>
     * Take a string of UTC Time convert to LocalDateTime UTC       <br>
     * Convert LocalDateTime-UTC to ZonedTime-UTC                   <br>
     * Convert ZonedTime-UTC to ZonedTime-LocalSysTime              <br>
     * Convert ZonedTime-LocalSysTime to LocalDateTime-LocalSysTime <br>
     * Convert & Return LocalDateTime-LocalSysTime to String        <br>
     * @param timeUTCString String of UTC time
     * @return String of ZonedTime
     */
    public static LocalDateTime utcToZoneTime(String timeUTCString){
        Timestamp utcTimeStamp = Timestamp.valueOf(timeUTCString);
        LocalDateTime timeUTC = utcTimeStamp.toLocalDateTime();
        ZonedDateTime timeWithUTC = timeUTC.atZone(ZoneId.of("UTC"));
        ZonedDateTime utcToLocal = timeWithUTC.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().toString()));
        LocalDateTime localTime = utcToLocal.toLocalDateTime();

        return localTime;
    }

    /**
     * BUILD DATE TIME FROM (STRING DATE) + (STRING TIME)<br>
     * -------------------------------------------------<br>
     * Convert String Date+Time to LocalDateTime        <br>
     * Convert LocalDateTime to ZonedDateTime           <br>
     * Convert & Return ZonedDateTime to String         <br>
     * @param date string of date
     * @param time string of time
     * @return string of ZonedDateTime
     */
    public static String stringLDTBuilder(LocalDate date, String time){
        String ldtString = date + "T" + time;
        LocalDateTime ldtTime = LocalDateTime.parse(ldtString);
        ZonedDateTime zdt = ldtTime.atZone(ZoneId.of(ZoneId.systemDefault().toString()));
        String zdtString = String.valueOf(zdt);

        return zdtString;
    }

    /**
     * PARSE DATE FROM LOCALDATETIME STRING <br>
     * Parse & Return substring of param    <br>
     * @param dateTime LocalDateTime String
     * @return date string
     */
    public static String parseDate(String dateTime){
        String date = dateTime.substring(0,10);
        return date;
    }

    /**
     * PARSE TIME FROM LOCALDATETIME STRING <br>
     * Parse & Return substring of param    <br>
     * @param dateTime LocalDateTime String
     * @return time string
     */
    public static String parseTime(String dateTime){
        String time = dateTime.substring(11,16);
        return time;
    }

    /**
     * PARSE HOUR FROM LOCALDATETIME STRING <br>
     * Parse & Return substring of param    <br>
     * @param dateTime LocalDateTime String
     * @return hour string
     */
    public static String parseHour(String dateTime){
        String hour = dateTime.substring(11,13);
        return hour;
    }

    /**
     * PARSE MINUTES FROM LOCALDATETIME STRING  <br>
     * Parse & Return substring of param        <br>
     * @param dateTime LocalDateTime String
     * @return minutes string
     */
    public static String parseMins(String dateTime){
        String minutes = dateTime.substring(14, 16);
        return minutes;
    }

    /**
     * GET DURATION OF APPOINTMENT - HOURS                                                                  <br>
     * -----------------------------------------------------------------------------------------------------<br>
     * Convert String to LocalDateTime                                                                      <br>
     * Calculate Difference between start and end time of appointment                                       <br>
     * (int)Divide difference by 60 to calculate appointment duration in hours and drop remainder minutes   <br>
     * return appointment duration in hours                                                                 <br>
     * @param startTimeString string of LocalDateTime (start of appointment)
     * @param endTimeString string of LocalDateTime (end of appointment)
     * @return duration of appointment in Hours (difference between start and end of appointment)
     */
    public static String getDurationHours(String startTimeString, String endTimeString){
        LocalTime startTime = LocalTime.parse(startTimeString);
        LocalTime endTime = LocalTime.parse(endTimeString);

        Duration duration = Duration.between(startTime, endTime);
        int totalMinutes = (int)duration.toMinutes();
        int totalHours = totalMinutes / 60;

        return String.valueOf(totalHours);
    }

    /**
     * GET DURATION OF APPOINTMENT - REMAINDER MINUTES                              <br>
     * -----------------------------------------------------------------------------<br>
     * Convert String to LocalDateTime                                              <br>
     * Calculate Difference between start and end time of appointment               <br>
     * (int)Modulo difference by 60 to calculate remainder minutes of appointment   <br>
     * return remainder minutes of appointment                                      <br>
     * @param startTimeString string of LocalDateTime (start of appointment)
     * @param endTimeString string of LocalDateTime (end of appointment)
     * @return  duration of appointment in Remainder Minutes (difference between start and end of appointment)
     */
    public static String getDurationMins(String startTimeString, String endTimeString) {
        LocalTime startTime = LocalTime.parse(startTimeString);
        LocalTime endTime = LocalTime.parse(endTimeString);

        Duration duration = Duration.between(startTime, endTime);
        int totalMinutes = (int)duration.toMinutes();
        int remainMins = totalMinutes % 60;

        return String.valueOf(remainMins);
    }

    /**
     * CONVERT UTC TIME TO LOCAL USER TIME                                      <br>
     * Used to convert time taken from add/update appointment time to Local Time<br>
     * @param time time taken from appointment forms
     * @return localTime of user
     */
    public static LocalDateTime utcToUserLocal(String time){
        //Convert Time String to LocalDateTime
        LocalDateTime timeLocalUTC = LocalDateTime.parse(time);

        //Convert LDT to TS to correctly format for use in TimeConverter.utcToZoneTime method
        //TimeConverter.utcToZoneTime converts UTC to LocalTime for the User
        Timestamp ts = Timestamp.valueOf(timeLocalUTC);

        //Convert TS to LocalTime for User
        return TimeConverter.utcToZoneTime(String.valueOf(ts));
    }


}
