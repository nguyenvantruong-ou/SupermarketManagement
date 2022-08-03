package com.ou.controllers;

import com.ou.pojos.Staff;
import com.ou.services.SignInService;
import com.ou.services.StaffService;
import com.ou.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignInController implements Initializable {
    private SignInService signInService;
    private final static StaffService STAFF_SERVICE;

    static {
        STAFF_SERVICE = new StaffService();
    }

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnSignIn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý siêu thị - OU Market");
        signInService = new SignInService();
        this.btnSignIn.setOnMouseClicked((t) -> {
            checkAccount();
        });
    }
    
    // Kiểm tra mật khẩu
    @FXML
    private void checkAccount() {
        try {
            if (checkTextInput()) {
                Staff staff = signInService.getAccountMD5(this.txtUsername.getText().trim(), this.txtPassword.getText().trim());
                if (staff == null)
                    AlertUtils.showAlert("Tên tài khoản hoặc mật khẩu không đúng!", Alert.AlertType.ERROR);
                else {
                    App.currentStaff = STAFF_SERVICE.getStaffByUsername(this.txtUsername.getText().trim());
                    if (staff.getStaIsAdmin()) // admin
                        try {
                            App.setRoot("homepage-admin");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    else {      // nhan vien
                        try {
                            App.setRoot("payment");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException ex) {
            Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Kiểm tra dữ liệu nhập
    private boolean checkTextInput() throws SQLException, NoSuchAlgorithmException {
        if ("".equals(this.txtUsername.getText().trim()) || this.txtUsername.getText().trim().length() < 6) {
            AlertUtils.showAlert("Tên tài khoản phải có ít nhất 6 kí tự !!", Alert.AlertType.ERROR);
            return false;
        } else if ("".equals(this.txtPassword.getText().trim()) || this.txtPassword.getText().trim().length() < 6) {
            AlertUtils.showAlert("Mật khẩu phải có ít nhất 6 kí tự !!", Alert.AlertType.ERROR);
            return false;
        } else {
            return true;
        }
    }

    // Sự kiện bàn phím
    @FXML
    public void setKeyEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            checkAccount();
        }
    }
}
