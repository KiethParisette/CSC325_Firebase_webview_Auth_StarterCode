module com.example.csc325_firebase_webview_auth {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.web;

    requires java.xml;
    requires java.logging;
    requires jdk.jsobject;

    requires com.google.auth;
    requires com.google.auth.oauth2;
    requires com.google.api.apicommon;

    requires google.cloud.core;
    requires google.cloud.firestore;
    requires google.cloud.storage;

    requires firebase.admin;

    opens com.example.csc325_firebase_webview_auth.viewmodel to jdk.jsobject;
    exports com.example.csc325_firebase_webview_auth.viewmodel;

    opens com.example.csc325_firebase_webview_auth.view to javafx.fxml;
    exports com.example.csc325_firebase_webview_auth.view;

    opens com.example.csc325_firebase_webview_auth.model;
    exports com.example.csc325_firebase_webview_auth.model;
}