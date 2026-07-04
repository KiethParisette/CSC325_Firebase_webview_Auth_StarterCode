package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AccessFBView {

    @FXML
    private TextField nameField;

    @FXML
    private TextField majorField;

    @FXML
    private TextField ageField;

    @FXML
    private Button writeButton;

    @FXML
    private Button readButton;

    @FXML
    private TextArea outputField;

    private boolean key;

    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();

    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {

        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();

        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
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

    // Opens Login window
    @FXML
    private void openLogin(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/LoginView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Opens Register window
    @FXML
    private void openRegister(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/files/RegisterView.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Register");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void addData() {

        DocumentReference docRef = App.fstore.collection("References").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));

        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public boolean readFirebase() {

        key = false;

        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();

        List<QueryDocumentSnapshot> documents;

        try {

            documents = future.get().getDocuments();

            if (documents.size() > 0) {

                System.out.println("Outing....");

                for (QueryDocumentSnapshot document : documents) {

                    outputField.setText(outputField.getText()
                            + document.getData().get("Name")
                            + " , Major: "
                            + document.getData().get("Major")
                            + " , Age: "
                            + document.getData().get("Age")
                            + "\n");

                    person = new Person(
                            String.valueOf(document.getData().get("Name")),
                            document.getData().get("Major").toString(),
                            Integer.parseInt(document.getData().get("Age").toString()));

                    listOfUsers.add(person);
                }

            } else {

                System.out.println("No data");
            }

            key = true;

        } catch (InterruptedException | ExecutionException ex) {

            ex.printStackTrace();
        }

        return key;
    }

    public void sendVerificationEmail() {

        try {

            UserRecord user = App.fauth.getUser("name");

        } catch (Exception e) {

        }
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

            return false;
        }
    }
}