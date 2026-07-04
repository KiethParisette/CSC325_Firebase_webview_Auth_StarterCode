package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Bucket;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AccessFBView {

    @FXML private TextField nameField;
    @FXML private TextField majorField;
    @FXML private TextField ageField;
    @FXML private Button writeButton;
    @FXML private Button readButton;
    @FXML private TextArea outputField;

    @FXML private TableView<Person> tableView;
    @FXML private TableColumn<Person, String> nameColumn;
    @FXML private TableColumn<Person, String> majorColumn;
    @FXML private TableColumn<Person, Integer> ageColumn;

    @FXML private ImageView profileImageView;

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();

        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        tableView.setItems(listOfUsers);
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
        readFirebase();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

    @FXML
    private void regRecord(ActionEvent event) {
        registerUser();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("/files/WebContainer.fxml");
    }

    @FXML
    private void openLogin(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/LoginView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void openRegister(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/RegisterView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void uploadPicture(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                Bucket bucket = StorageClient.getInstance().bucket();

                String fileName = "profile_pictures/" + UUID.randomUUID() + "_" + file.getName();

                bucket.create(
                        fileName,
                        Files.readAllBytes(file.toPath()),
                        Files.probeContentType(file.toPath())
                );

                profileImageView.setImage(new Image(file.toURI().toString()));

                outputField.setText("Picture uploaded to Firebase Storage:\n" + fileName);

            } catch (Exception ex) {
                ex.printStackTrace();
                outputField.setText("Picture upload failed: " + ex.getMessage());
            }
        }
    }

    public void addData() {
        try {
            DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

            Map<String, Object> data = new HashMap<>();
            data.put("Name", nameField.getText());
            data.put("Major", majorField.getText());
            data.put("Age", Integer.parseInt(ageField.getText()));

            docRef.set(data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean readFirebase() {
        key = false;
        listOfUsers.clear();

        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                String name = String.valueOf(document.getData().get("Name"));
                String major = String.valueOf(document.getData().get("Major"));

                Object ageObject = document.getData().get("Age");
                int age = 0;

                if (ageObject != null) {
                    if (ageObject instanceof Long) {
                        age = ((Long) ageObject).intValue();
                    } else if (ageObject instanceof Integer) {
                        age = (Integer) ageObject;
                    } else {
                        age = Integer.parseInt(ageObject.toString());
                    }
                }

                listOfUsers.add(new Person(name, major, age));
            }

            key = true;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return key;
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user@example.com")
                .setEmailVerified(false)
                .setPassword("secretPassword")
                .setPhoneNumber("+11234567890")
                .setDisplayName("John Doe")
                .setDisabled(false);

        try {
            UserRecord userRecord = App.fauth.createUser(request);
            System.out.println("Successfully created new user: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}