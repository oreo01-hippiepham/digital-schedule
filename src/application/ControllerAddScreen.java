package application;

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
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ResourceBundle;

public class ControllerAddScreen implements Initializable {
    static DatabaseConnection connectNow = new DatabaseConnection();
    static Connection connectDB = connectNow.getConnection();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private TextField taskName;
    @FXML
    private Label dateLabel;
    @FXML
    private TextField startTime;
    @FXML
    private TextField endTime;
    @FXML
    private ChoiceBox<String> taskType;
    @FXML
    private Button saveButton;
    @FXML
    private Button editButton;
    @FXML
    private Label warningLabel;
    private String currentTaskID;
    private final String[] types = {"Lecture", "Practical", "Meeting", "Sports", "Extra","Others"};
    private Stage stage;
    String query = null;
    Statement statement = null;
    ResultSet resultSet = null;
    ControllerMainScreen1 controllerMainScreen1 = null;

    public void bringEditButtonToFront()
    {
        saveButton.setVisible(false);
        editButton.toFront();
    }

    public void bringSaveButtonToFront()
    {
        editButton.setVisible(false);
        saveButton.toFront();
    }

    public void changeEditButtonEntered (MouseEvent event)
    {
        editButton.setStyle("-fx-background-color: transparent;" +
                " -fx-border-color: #FFFFFF; -fx-text-fill:  #FFFFFF; -fx-border-radius: 3px; " +
                "-fx-background-radius: 3px;");
    }

    public void changeEditButtonExited (MouseEvent event)
    {
        editButton.setStyle("-fx-background-color: #A3C1AD;" +
                " -fx-border-color: transparent; -fx-text-fill: #FFFFFF;");

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

    public String checkInsert(String currentTaskName, String currentTaskType, String currentStartTime,
                              String currentEndTime)
    {
        if (currentTaskName.equals(""))
        {
            return "Please Enter A Task Name!";
        }
        if (checkTime(currentStartTime) || checkTime(currentEndTime))
        {
            return "Please Enter A Valid Time!";
        }
        if (compareTime(currentStartTime, currentEndTime) == 1 || compareTime(currentStartTime, currentEndTime) == 0)
        {
            return "Please Enter A Valid Time!";
        }

        if (currentTaskType == null)
        {
            return "Please Choose A Task Type!";
        }

        return "true";
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

    public int compareTime (String startTime, String endTime)
    {
        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);
        return LocalTime.parse(startTime, strictTimeFormatter).compareTo(LocalTime.parse(endTime, strictTimeFormatter));
    }

    public void editTask (ActionEvent event) throws SQLException
    {
        String currentTaskName = taskName.getText();
        String currentStartTime = startTime.getText();
        String currentEndTime = endTime.getText();
        String currentTaskType = taskType.getValue();
        Integer typeID = null;

        statement = connectDB.createStatement();
        query = Main.getMainQuery("src/SQLScript/retrieve_task_type_id.sql");
        query = String.format(query, currentTaskType);
        resultSet = statement.executeQuery(query);

        while (resultSet.next())
        {
            typeID =Integer.parseInt(resultSet.getString("id"));
        }

        statement = connectDB.createStatement();
        query = Main.getMainQuery("src/SQLScript/edit_entry_from_id.sql");
        query = String.format(query, currentTaskName, typeID, currentStartTime, currentEndTime, Integer.parseInt(currentTaskID));
        statement.executeUpdate(query);
        controllerMainScreen1.refreshTask();
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    public void exit (MouseEvent event)
    {
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    public void getCurrentTaskID(String currentTaskID)
    {
        this.currentTaskID = currentTaskID;
    }


    public void getMainScreen1Controller(ControllerMainScreen1 controllerMainScreen1)
    {
        this.controllerMainScreen1 = controllerMainScreen1;
    }


    public void insertTask(ActionEvent event) throws SQLException {
        String currentTaskName = taskName.getText();
        String currentStartTime = startTime.getText();
        String currentEndTime = endTime.getText();
        String currentTaskType = taskType.getValue();

        String check = checkInsert(currentTaskName, currentTaskType, currentStartTime, currentEndTime);

        if (!check.equals("true"))
        {
            warningLabel.setText(check);
        }
        else
        {
            currentStartTime = currentStartTime + ":00";
            currentEndTime = currentEndTime + ":00";
            statement = connectDB.createStatement();
            query = Main.getMainQuery("src/SQLScript/retrieve_id_from_email.sql");
            query = String.format(query, Main.mainUser.getEmail());
            resultSet = statement.executeQuery(query);
            String person_id = "";
            String taskType_id = "";
            String newTask_id = "";

            while (resultSet.next())
            {
                person_id = resultSet.getString("id");
            }


            query = Main.getMainQuery("src/SQLScript/retrieve_task_type_id.sql");
            query = String.format(query, currentTaskType);
            resultSet = statement.executeQuery(query);
            while (resultSet.next())
            {
                 taskType_id = resultSet.getString("id");
            }


            query = Main.getMainQuery("src/SQLScript/insert_new_task.sql");
            query = String.format(query, currentTaskName, Integer.parseInt(taskType_id), currentStartTime, currentEndTime);
            statement.executeUpdate(query);
            query = "SELECT MAX(id) FROM task;";
            resultSet = statement.executeQuery(query);
            while (resultSet.next())
            {
                 newTask_id = resultSet.getString("MAX(id)");
            }

            query = "INSERT INTO schedule (id, person_id, task_id) VALUES (NULL, %d, %d)";
            query = String.format(query, Integer.parseInt(person_id), Integer.parseInt(newTask_id));
            statement.executeUpdate(query);

            controllerMainScreen1.refreshTask();
            stage = (Stage) scenePane.getScene().getWindow();
            stage.close();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        dateLabel.setText(myDateObj.format(myFormatObj));
        taskType.getItems().addAll(types);
    }

    public void setCurrentInfo(String taskName, String taskType, String startTime, String endTime)
    {
        this.taskName.setText(taskName);
        this.taskType.setValue(taskType);
        this.startTime.setText(startTime);
        this.endTime.setText(endTime);
    }
}
