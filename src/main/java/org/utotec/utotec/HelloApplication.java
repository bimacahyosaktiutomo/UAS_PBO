package org.utotec.utotec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    static Connection con = DB.connectDB();

    @Override
    public void start(Stage stage) throws IOException {
        HomeRefresh(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public static void HomeRefresh(Stage stage) throws IOException {
        if (con != null) {
            System.out.println("Database connection successful.");
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            File file = new File("src/main/resources/img/backspaceaqua.png");
            stage.getIcons().add(new Image(file.toURI().toString()));
            stage.setTitle("Backspace Computer");
            stage.setScene(scene);
            stage.show();
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }
}