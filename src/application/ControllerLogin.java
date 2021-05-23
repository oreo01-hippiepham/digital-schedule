package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable{
    static DatabaseConnection connectNow = new DatabaseConnection();
    static Connection connectDB = connectNow.getConnection();

    @FXML
    private AnchorPane scenePane;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;
    @FXML
    private Label warningLabel;
    private Stage stage;
    Statement statement = null;
    ResultSet resultSet = null;
    String query;

    public void changeColorLoginEntered (MouseEvent event)
    {
        signInButton.setStyle("-fx-background-color: transparent;" +
                " -fx-border-color: #a3c1ad; -fx-text-fill: #a3c1ad; -fx-border-radius: 3px;" +
                " -fx-background-radius: 3px;");
    }

    public void changeColorLoginExited (MouseEvent event)
    {
        signInButton.setStyle("-fx-background-color: #a3c1ad;" +
                " -fx-border-color: transparent; -fx-text-fill: #FFFFFF;");

    }

    public void exit (MouseEvent event)
    {
        stage = (Stage) scenePane.getScene().getWindow();
        stage.close();
    }

    public void login (ActionEvent event) throws SQLException {
        String email = emailTextField.getText();
        String password = passwordField.getText();
        statement = connectDB.createStatement();
        query = Main.getMainQuery("src/SQLScript/countNumberofEmail.sql");
        query = String.format(query, email);
        resultSet = statement.executeQuery(query);
        Integer res = null;

        while (resultSet.next())
        {
            res = Integer.parseInt(resultSet.getString("COUNT(*)"));
        }

        if (res == 0)
        {
            warningLabel.setText("Incorrect email or password. Please try again!");
        }
        else
        {
            query = Main.getMainQuery("src/SQLScript/getPasswordFromEmail.sql");
            query = String.format(query, email);
            resultSet = statement.executeQuery(query);
            String pw_returned = "";

            while (resultSet.next())
            {
                pw_returned = resultSet.getString("password");
            }
            if (!pw_returned.equals(password))
            {
                warningLabel.setText("Incorrect email or password. Please try again!");
            }
            else
            {
                try {
                    Main.mainUser = new User (email);
                    query =  Main.getMainQuery("src/SQLScript/getNameFromEmail.sql");
                    query = String.format(query, email);
                    resultSet = statement.executeQuery(query);
                    String username = "";
                    while (resultSet.next())
                    {
                        username = resultSet.getString("full_name");
                    }

                    Main.mainUser.setUserName(username);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("mainScreen1.fxml"));
                    Parent root = loader.load();
                    ControllerMainScreen1 screen1Controller = loader.getController();
                    screen1Controller.setMyProfile(username);
                    stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (connectDB == null)
        {
            warningLabel.setText("Cannot connect to the database!");
        }
    }
}
