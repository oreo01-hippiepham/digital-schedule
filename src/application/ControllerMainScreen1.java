package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;

public class ControllerMainScreen1 implements Initializable {
    public static Scene scene;
    static DatabaseConnection connectNow = new DatabaseConnection();
    static Connection connectDB = connectNow.getConnection();
    @FXML
    private AnchorPane scenePane;
    @FXML
    private Label myUsername;
    @FXML
    private Label dateAndTimeLabel;
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> idCol;
    @FXML
    private TableColumn<Task, String> timeCol;
    @FXML
    private TableColumn<Task, String> taskNameCol;
    @FXML
    private TableColumn<Task, String> taskTypeCol;
    private Stage stage;
    private Parent root;
    Task currentTask = null;
    String query = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    ObservableList<Task> taskList = FXCollections.observableArrayList();

    public void setMyProfile (String userName)
    {
        myUsername.setText("Welcome, " + userName + "!");
    }

    public void exit (MouseEvent event)
    {
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }


    //Log Out
    public void logOut (MouseEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("loginScreen.fxml"));
            root = loader.load();
            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            scene = new Scene (root);
            stage.setScene(scene);
            stage.show();
            Main.mainUser = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToMainScreen2(MouseEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("mainScreen2.fxml"));
            root = loader.load();

            ControllerMainScreen2 screen2Controller = loader.getController();
            screen2Controller.setMyProfile(Main.mainUser.getUserName());

            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            scene = new Scene (root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");
        dateAndTimeLabel.setText(myDateObj.format(myFormatObj));
        loadTask();

    }

    public void deleteTask(MouseEvent event)
    {
        currentTask = taskTable.getSelectionModel().getSelectedItem();
        try {
            query = Main.getMainQuery("src/SQLScript/deleteTaskFromSchedule.sql");
            try {
                query = String.format(query, currentTask.getQueryID());
            } catch (NullPointerException e) {
                return;
            }
            preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.executeUpdate();

            query = Main.getMainQuery("src/SQLScript/deleteTaskFromTask.sql");
            query = String.format(query, currentTask.getQueryID());
            preparedStatement = connectDB.prepareStatement(query);
            preparedStatement.executeUpdate();

            refreshTask();
        } catch (SQLException ex) {
            Logger.getLogger(ControllerMainScreen1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void addTask(MouseEvent event)
    {
        try {
            Image icon = new Image("icon1.png");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addingScreen.fxml"));
            Parent parent = loader.load();

            ControllerAddScreen controllerAddScreen = loader.getController();
            controllerAddScreen.bringSaveButtonToFront();
            controllerAddScreen.getMainScreen1Controller(this);

            Scene newScene = new Scene(parent);
            newScene.setFill(Color.TRANSPARENT);
            Stage newStage = new Stage();
            newStage.getIcons().add(icon);
            newStage.setTitle("Adding Schedule");
            newStage.setScene(newScene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ControllerMainScreen1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editTask (MouseEvent event)
    {
        currentTask = taskTable.getSelectionModel().getSelectedItem();
        try {
            Image icon = new Image("icon1.png");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addingScreen.fxml"));
            Parent parent = loader.load();

            ControllerAddScreen controllerAddScreen = loader.getController();
            controllerAddScreen.bringEditButtonToFront();
            controllerAddScreen.getMainScreen1Controller(this);
            Scene newScene = new Scene(parent);
            newScene.setFill(Color.TRANSPARENT);
            Stage newStage = new Stage();

            try {
                controllerAddScreen.setCurrentInfo(currentTask.getTaskName(), currentTask.getTaskType(),
                        currentTask.getStartTime(), currentTask.getEndTime());
                controllerAddScreen.getCurrentTaskID(currentTask.getQueryID());
            }
            catch (NullPointerException exception)
            {
                return;
            }


            newStage.getIcons().add(icon);
            newStage.setTitle("Adding Schedule");
            newStage.setScene(newScene);
            newStage.initStyle(StageStyle.TRANSPARENT);
            newStage.show();
        } catch (IOException ex) {
            Logger.getLogger(ControllerMainScreen1.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }



    public void refreshTask()
    {
        Task.numTask = 0;
        try
        {
            taskList.clear();
            String getTaskFromEmailQuery = Main.getMainQuery("src/SQLScript/getTaskfromEmail.sql");
            getTaskFromEmailQuery = String.format(getTaskFromEmailQuery, Main.mainUser.getEmail());
            preparedStatement = connectDB.prepareStatement(getTaskFromEmailQuery);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                taskList.add(
                        new Task(resultSet.getString("name"),
                                convertToTitleCaseIteratingChars(resultSet.getString("task_name")),
                                resultSet.getString("start_time").substring(0, 5),
                                resultSet.getString("end_time").substring(0, 5),
                                resultSet.getString("id"), resultSet.getString("hash")));
                taskTable.setItems(taskList);
            }

        } catch (SQLException ex) {
            Logger.getLogger(ControllerMainScreen1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public void loadTask()
    {
        refreshTask();
        idCol.setCellValueFactory(new PropertyValueFactory<>("taskID"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("expressTime"));
        taskNameCol.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        taskTypeCol.setCellValueFactory(new PropertyValueFactory<>("taskType"));
    }


}
