package com.ou.controllers;

import com.ou.pojos.Staff;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static Window window;
    public static Staff currentStaff = null;

    @Override
    public void start(Stage stage) throws IOException {
        window = stage;
        scene = new Scene(loadFXML("sign-in"));
        stage.getIcons().add(new Image(Objects.requireNonNull(App.class.getResourceAsStream("/com.ou.images/ouIcon.png"))));
        stage.setTitle("Quản lý siêu thị - OU Market");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        if (fxml.equals("sign-in")) {
            window.setHeight(600);
            window.setWidth(1000);
        } else {
            window.setHeight(900);
            window.setWidth(1500);
        }
        window.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}