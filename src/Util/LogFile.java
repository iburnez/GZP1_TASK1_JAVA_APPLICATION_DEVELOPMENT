package Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogFile {

    public static void writeToLog(String userName, String string)  {

        try {
            File logFile = new File("../Performance Assessment/Logs/logfile.txt");
            FileWriter outputActivity = new FileWriter(logFile, true);
            PrintWriter logActivity = new PrintWriter(outputActivity);
            logActivity.println(TimeConverter.dateTimeToUTC(ZonedDateTime.now()) + "[UTC], User Name: " + userName + ", " + string);
            logActivity.close();
        }catch (IOException e){
            Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void writeToLog(String userName, String string, String string2)  {

        try {
            File logFile = new File("../Performance Assessment/Logs/logfile.txt");
            FileWriter outputActivity = new FileWriter(logFile, true);
            PrintWriter logActivity = new PrintWriter(outputActivity);
            logActivity.println(TimeConverter.dateTimeToUTC(ZonedDateTime.now()) + "[UTC], User Name: " + userName + ", " + string + " " + string2);
            logActivity.close();
        }catch (IOException e){
            Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
