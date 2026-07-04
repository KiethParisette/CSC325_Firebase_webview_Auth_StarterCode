package com.example.csc325_firebase_webview_auth.view;


import com.example.csc325_firebase_webview_auth.model.FirestoreContext;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * JavaFX App
 */
public class App extends Application {

    public static Firestore fstore;
    public static FirebaseAuth fauth;
    public static Scene scene;
    private final FirestoreContext contxtFirebase = new FirestoreContext();

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Initialize Firebase
        fstore = contxtFirebase.firebase();
        fauth = FirebaseAuth.getInstance();

        // Splash screen
        Label title = new Label("CSC325 Firebase Project");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        ProgressIndicator progress = new ProgressIndicator();

        VBox splashRoot = new VBox(20, title, progress);
        splashRoot.setAlignment(Pos.CENTER);

        Scene splashScene = new Scene(splashRoot, 600, 400);

        primaryStage.setTitle("Loading...");
        primaryStage.setScene(splashScene);
        primaryStage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(event -> {
            try {
                scene = new Scene(loadFXML("/files/AccessFBView.fxml"));
                primaryStage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        delay.play();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml ));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
