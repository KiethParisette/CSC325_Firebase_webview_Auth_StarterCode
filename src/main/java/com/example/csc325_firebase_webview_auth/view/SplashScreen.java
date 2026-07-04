package com.example.csc325_firebase_webview_auth.view;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("CSC325 Firebase Project");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        ProgressIndicator progress = new ProgressIndicator();

        VBox root = new VBox(20, title, progress);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 600, 400);

        stage.setScene(scene);
        stage.setTitle("Loading...");
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(event -> {
            stage.close();

            Stage mainStage = new Stage();

            try {
                new App().start(mainStage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        delay.play();
    }
}