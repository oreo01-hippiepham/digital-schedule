package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControllerMainScreen2 implements Initializable {
    static DatabaseConnection connectNow = new DatabaseConnection();
    static Connection connectDB = connectNow.getConnection();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private Label myUsername;
    @FXML
    private Label dateAndTimeLabel;
    @FXML
    private TextArea recipientEmail;
    @FXML
    private TextField meetingDuration;
    @FXML
    private Button generateButton;
    @FXML
    private Label warningLabel;
    private Stage stage;
    private Scene scene;
    private Parent root;
    String query = null;
    ResultSet resultSet = null;

    public void setMyProfile(String userName) {
        myUsername.setText("Welcome, " + userName + "!");
    }

    public void changeGenerateButtonEntered(MouseEvent event) {
        generateButton.setStyle("-fx-background-color: transparent;" +
                " -fx-border-color: #A3C1AD; -fx-text-fill:  #A3C1AD; -fx-border-radius: 3px; " +
                "-fx-background-radius: 3px;");
    }

    public void changeGenerateButtonExited(MouseEvent event) {
        generateButton.setStyle("-fx-background-color: #A3C1AD;" +
                " -fx-border-color: transparent; -fx-text-fill: #FFFFFF;");

    }


    public void logOut(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginScreen.fxml"));
            root = loader.load();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            Main.mainUser = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToMainScreen1(MouseEvent event) {
        try {
            Task.numTask = 0;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("mainScreen1.fxml"));
            root = loader.load();

            ControllerMainScreen1 screen1Controller = loader.getController();
            screen1Controller.setMyProfile(Main.mainUser.getUserName());

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String[]> getTimePeriod(String email) throws SQLException {
        Statement statement = connectDB.createStatement();
        query = Main.getMainQuery("src/SQLScript/main_query.sql");
        query = String.format(query, email);
        ResultSet resultSet = statement.executeQuery(query);

        LinkedList<String[]> resSet = new LinkedList<>();

        while (resultSet.next()) {
            String[] timePeriod = new String[2];
            timePeriod[0] = resultSet.getString("start_time").substring(0, 5);
            timePeriod[1] = resultSet.getString("end_time").substring(0, 5);
            resSet.add(timePeriod);
        }
        return resSet;
    }


    public void generateAppointment(ActionEvent event) throws SQLException {
        String reci_email = recipientEmail.getText();
        if (reci_email.equals(""))
        {
            warningLabel.setText("Please enter an email!");
            return;
        }
        Scanner sc = new Scanner(reci_email);
        Statement statement = connectDB.createStatement();
        CoreAlgorithm algorithm = new CoreAlgorithm();
        LinkedList<LinkedList<String[]>> allTask = new LinkedList<>();
        allTask.add(getTimePeriod(Main.mainUser.getEmail()));

        while (sc.hasNextLine()) {
            String emailString = sc.nextLine();
            if (emailString.equals(Main.mainUser.getEmail())) {
                warningLabel.setText("You cannot book yourself!");
                return;
            }
            query = Main.getMainQuery("src/SQLScript/countNumberofEmail.sql");
            query = String.format(query, emailString);
            resultSet = statement.executeQuery(query);
            Integer res = null;
            while (resultSet.next()) {
                res = Integer.parseInt(resultSet.getString("COUNT(*)"));
            }
            if (res == 0) {
                warningLabel.setText(String.format("The recipient \"%s\" does not exist!", emailString));
                return;
            } else {
                allTask.add(getTimePeriod(emailString));
            }
        }

        String durationString = meetingDuration.getText();
        int duration;

        try {
            duration = Integer.parseInt(durationString);
        } catch (NumberFormatException e) {
            warningLabel.setText("Please enter a valid time!");
            return;
        }

        if (duration <=0)
        {
            warningLabel.setText("Please enter a valid time!");
            return;
        }

        String[] durationList = algorithm.calendarMatching(duration, allTask);


        if (durationList.length == 0) {
            warningLabel.setText("There is no current available time block!");
            return;
        }

        try {
            Image icon = new Image("icon1.png");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("choosingAppointment.fxml"));
            Parent parent = loader.load();

            ControllerChoosingScreen controllerChoosingScreen = loader.getController();
            controllerChoosingScreen.getRecipientEmailLabel(reci_email);
            controllerChoosingScreen.getTimeDuration(durationList);
            controllerChoosingScreen.getMeetingDuration(duration);

            Scene newScene = new Scene(parent);
            newScene.setFill(Color.TRANSPARENT);
            Stage newStage = new Stage();
            newStage.getIcons().add(icon);
            newStage.setTitle("Choosing Appointment");
            newStage.setScene(newScene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            refreshScreen();
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ControllerMainScreen1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshScreen()
    {
        recipientEmail.clear();
        meetingDuration.clear();
        warningLabel.setText("");
    }


    public void exit (MouseEvent event)
    {
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        dateAndTimeLabel.setText(myDateObj.format(myFormatObj));
    }
}
