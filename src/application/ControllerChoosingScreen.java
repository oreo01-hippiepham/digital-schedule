package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class ControllerChoosingScreen implements Initializable {
    static DatabaseConnection connectNow = new DatabaseConnection();
    static Connection connectDB = connectNow.getConnection();
    @FXML
    private TextField appointmentName;
    @FXML
    private TextField startTime;
    @FXML
    private TextField endTime;
    @FXML
    private Label recipientEmailLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ChoiceBox<String> appointmentDuration;
    @FXML
    private ChoiceBox<String> taskType;
    @FXML
    private Button saveButton;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private Label warningLabel;
    private final String[] types = {"Lecture", "Practical", "Meeting", "Sports", "Extra","Others"};
    private Stage stage;
    private String reci_email;
    Integer duration = null;
    String query = null;
    Statement statement = null;
    ResultSet resultSet = null;


    public void bookAppointment (ActionEvent event) throws SQLException
    {
        String currentTaskName = appointmentName.getText();
        String currentTimeBlock = appointmentDuration.getValue();
        String currentTaskType = taskType.getValue();

        String check = checkInsert1(currentTimeBlock);

        if (!check.equals("true"))
        {
            warningLabel.setText(check);
            return;
        }
        
        String currentStartTime = startTime.getText();
        String currentEndTime = endTime.getText();

        String boundedStartTime = appointmentDuration.getValue().substring(0,5);
        String boundedEndTime = appointmentDuration.getValue().substring(8,13);

        check = checkInsert2(currentTaskName, currentTaskType, currentStartTime, currentEndTime
        , boundedStartTime, boundedEndTime);

        Scanner sc = new Scanner (reci_email);

        if (!check.equals("true"))
        {
            warningLabel.setText(check);
        }
        else
        {
            currentStartTime = currentStartTime + ":00";
            currentEndTime = currentEndTime + ":00";
            int hashCode = hash();

            String taskType_id = "";
            String newTask_id = "";

            query = Main.getMainQuery("src/SQLScript/retrieve_task_type_id.sql");
            query = String.format(query, currentTaskType);
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                taskType_id = resultSet.getString("id");
            }


            query = Main.getMainQuery("src/SQLScript/InsertTaskWithHashCode.sql");
            query = String.format(query, currentTaskName, Integer.parseInt(taskType_id), currentStartTime, currentEndTime, hashCode);
            statement.executeUpdate(query);
            query = "SELECT MAX(id) FROM task;";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                newTask_id = resultSet.getString("MAX(id)");
            }

            while (sc.hasNextLine())
            {
                String currentEmail = sc.nextLine();
                statement = connectDB.createStatement();
                query = Main.getMainQuery("src/SQLScript/retrieve_id_from_email.sql");
                query = String.format(query, currentEmail);
                resultSet = statement.executeQuery(query);
                String person_id = "";

                while (resultSet.next()) {
                    person_id = resultSet.getString("id");
                }

                query = "INSERT INTO schedule (id, person_id, task_id) VALUES (NULL, %d, %d)";
                query = String.format(query, Integer.parseInt(person_id), Integer.parseInt(newTask_id));
                statement.executeUpdate(query);
                stage = (Stage) scenePane.getScene().getWindow();
                stage.close();
            }
        }
    }

    public void changeSaveButtonEntered (MouseEvent event)
    {
        saveButton.setStyle("-fx-background-color: transparent;" +
                " -fx-border-color: #FFFFFF; -fx-text-fill:  #FFFFFF; -fx-border-radius: 3px; " +
                "-fx-background-radius: 3px;");
    }

    public void changeSaveButtonExited (MouseEvent event)
    {
        saveButton.setStyle("-fx-background-color: #A3C1AD;" +
                " -fx-border-color: transparent; -fx-text-fill: #FFFFFF;");

    }

    public String checkInsert1 (String currentTimeBlock)
    {
        if (currentTimeBlock == null)
        {
            return "Please choose a time block!";
        }
        return "true";
    }

    public String checkInsert2(String currentTaskName, String currentTaskType, String currentStartTime,
                              String currentEndTime, String boundedStartTime, String boundedEndTime)
    {
        if (currentTaskName.equals(""))
        {
            return "Please enter the appointment's name!";
        }

        if (checkTime(currentStartTime) || checkTime(currentEndTime) || compareTime(currentStartTime, currentEndTime) >= 0)
        {
            return "Please enter valid time!";
        }

        if (compareTime(boundedStartTime, currentStartTime) > 0 || compareTime(currentEndTime,boundedEndTime) > 0)
        {
            return String.format("Please schedule between %s and %s!", boundedStartTime, boundedEndTime);
        }

        if (currentTaskType == null)
        {
            return "Please enter the appointment's type!";
        }
        return "true";
    }

    public void exit (MouseEvent event)
    {
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    public void getRecipientEmailLabel (String email)
    {
        reci_email = email;
        Scanner sc = new Scanner (reci_email);
        StringBuilder label = new StringBuilder();

        label.append("Sending invitation to: ").append(sc.nextLine());
        if (sc.hasNextLine())
        {
            label.append(", ").append(sc.nextLine());

        }
        if (sc.hasNextLine())
        {
            label.append(",...");
        }


        recipientEmailLabel.setText(label.toString());
        reci_email += "\n" + Main.mainUser.getEmail();
    }

    public void getMeetingDuration(int duration)
    {
        this.duration = duration;
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        if (this.duration > 1)
        {
            dateLabel.setText(String.format("%s. Duration: %d minutes",myDateObj.format(myFormatObj), duration));
        }
        else
        {
            dateLabel.setText(String.format("%s. Duration: %d minute", myDateObj.format(myFormatObj), duration));
        }
    }

    public void getTimeDuration (String[] durationList)
    {
        appointmentDuration.getItems().addAll(durationList);
    }

    public int hash ()
    {
        LocalDateTime timeObj = LocalDateTime.now();
        DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = timeObj.format(formatObj);
        StringBuilder rawString = new StringBuilder();
        Integer id = null;
        Scanner sc = new Scanner (reci_email);
        while (sc.hasNext())
        {
            String email = sc.nextLine();
            query = Main.getMainQuery("src/SQLScript/retrieve_id_from_email.sql");
            query = String.format(query, email);
            try {
                statement = connectDB.createStatement();
                resultSet = statement.executeQuery(query);
                while (resultSet.next())
                {
                    id = Integer.parseInt(resultSet.getString("id"));
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            rawString.append(id);
        }
        rawString.append(time);
        return rawString.toString().hashCode();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskType.getItems().addAll(types);
        taskType.setValue("Meeting");
        appointmentDuration.setOnAction(this::setTimeForMeeting);
    }

    public LocalTime changeStringToTime (String time)
    {
        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
        return LocalTime.parse(time, strictTimeFormatter);
    }

    public int compareTime (String startTime, String endTime)
    {
        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
        return LocalTime.parse(startTime, strictTimeFormatter).compareTo(LocalTime.parse(endTime, strictTimeFormatter));
    }

    public boolean checkTime (String time)
    {

        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalTime.parse(time, strictTimeFormatter);
            return false;
        } catch (DateTimeParseException | NullPointerException e) {
            return true;
        }
    }


    public void setTimeForMeeting(ActionEvent event)
    {
        warningLabel.setText("");

        startTime.setText(appointmentDuration.getValue().substring(0,5));
        endTime.setText(changeStringToTime(appointmentDuration.getValue().substring(0,5)).plusMinutes(duration).toString());

        startTime.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String currentStartTime = startTime.getText();
                String boundedStartTime = appointmentDuration.getValue().substring(0,5);
                String boundedEndTime = appointmentDuration.getValue().substring(8,13);
               if (!checkTime(currentStartTime))
               {
                   String currentEndTime = change(currentStartTime);
                   endTime.setText(currentEndTime);

                   if (compareTime(boundedStartTime, currentStartTime) > 0 || compareTime(currentEndTime,boundedEndTime) > 0)
                   {
                       warningLabel.setText(String.format("Please schedule between %s and %s!", boundedStartTime, boundedEndTime));
                   }
                   else
                   {
                       warningLabel.setText("");
                   }
               }
            }
            public String change(String startTime)
            {
                return changeStringToTime(startTime).plusMinutes(duration).toString();
            }
        });

        endTime.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String currentEndTime = endTime.getText();
                String boundedStartTime = appointmentDuration.getValue().substring(0,5);
                String boundedEndTime = appointmentDuration.getValue().substring(8,13);
                if (!checkTime(currentEndTime))
                {
                    String currentStartTime = change(currentEndTime);
                    startTime.setText(currentStartTime);
                    if (compareTime(boundedStartTime, currentStartTime) > 0 || compareTime(currentEndTime,boundedEndTime) > 0)
                    {
                        warningLabel.setText(String.format("Please schedule between %s and %s!", boundedStartTime, boundedEndTime));
                    }
                    else
                    {
                        warningLabel.setText("");
                    }
                }
            }
            public String change(String time)
            {
                return changeStringToTime(time).minusMinutes(duration).toString();
            }
        });
    }
}
