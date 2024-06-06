package org.utotec.utotec;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button btnLogin;

    @FXML
    private Hyperlink hyperRegister;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML private Label txtWarning;

    static Stage loginStage;
    static Stage primaryStage;

    Connection connection = DB.connectDB();

    public void btnLoginClicked(ActionEvent actionEvent) throws IOException {Login(actionEvent);}
    public void hyperRegisterClicked(ActionEvent actionEvent){openRegisterPopup(actionEvent);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtWarning.setVisible(false);
    }

    private void Login(ActionEvent actionEvent) throws IOException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM akun WHERE username=? AND password=?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                SesiUser.LoginSession = 1;
                SesiUser.uid = resultSet.getInt("id");
                SesiUser.username = resultSet.getString("username");
                SesiUser.izin = resultSet.getString("izin");

                Tools.AlertNotifINFORMATION("Notifikasi", "Login", "Login Berhasil");

                Stage stagelog = Tools.CloseMeStage(actionEvent);
                loginStage = stagelog;

                loginStage.close();

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                primaryStage = stage;
                Scene scene = new Scene(fxmlLoader.load());
                File file = new File("src/main/resources/img/backspaceaqua.png");
                stage.getIcons().add(new Image(file.toURI().toString()));
                stage.setTitle("Backspace Computer");
                stage.setScene(scene);
                stage.show();
            } else {
                txtWarning.setVisible(true);
                PauseTransition visiblePause = new PauseTransition(Duration.seconds(3));
                visiblePause.setOnFinished(event -> txtWarning.setVisible(false));
                visiblePause.play();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openRegisterPopup(ActionEvent actionEvent) {
        Tools.CloseMeStage(actionEvent);

        Popup REGpopup = new Popup();
        REGpopup.setAutoHide(true);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            Stage POPstage = new Stage();
            POPstage.setScene(scene);
            POPstage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
