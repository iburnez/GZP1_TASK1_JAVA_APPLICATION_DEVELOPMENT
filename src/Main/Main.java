package Main;


import Util.TimeConverter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.EOFException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage){

        /**
         * root variable holds FXML loader and resource path
         * to load login screen
         */
        try {
            Parent root;

            root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }catch (IOException e){
            System.out.println("Main Error: " + e.getMessage());
        }

    }

    public static void main(String[] args) {

        launch(args);
    }
}
