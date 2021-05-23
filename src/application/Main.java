package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.Scanner;
import java.io.File;


public class Main extends Application {

    public static User mainUser;

    public static String getMainQuery(String pathName)
    {
        StringBuilder query = new StringBuilder();
        File qr = new File (pathName);
        try
        {
            Scanner sc = new Scanner (qr);
            while (sc.hasNextLine())
            {
                query.append(sc.nextLine());
                query.append("\n");
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return query.toString();
    }


    @Override
    public void start(Stage primaryStage){
        try {
            Image icon = new Image("icon1.png");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("loginScreen.fxml"));
            Parent root=loader.load();
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Cambridge Meeting Scheduler");
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
