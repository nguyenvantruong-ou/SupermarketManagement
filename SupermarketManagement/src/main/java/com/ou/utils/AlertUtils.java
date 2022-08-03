package com.ou.utils;

import javafx.scene.control.Alert;

public class AlertUtils {
    public static void showAlert(String message, Alert.AlertType alertType) {
        new Alert(alertType, message).show();
    }
}
