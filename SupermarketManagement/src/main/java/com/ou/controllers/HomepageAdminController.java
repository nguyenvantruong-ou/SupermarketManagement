package com.ou.controllers;

import com.ou.pojos.Staff;
import com.ou.services.StaffService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomepageAdminController implements Initializable {
    private final static StaffService STAFF_SERVICE;

    static {
        STAFF_SERVICE = new StaffService();
    }

    @FXML
    private Label lblUsername;

    @FXML
    private Label lblLastName;

    @FXML
    private Label lblFirstName;

    @FXML
    private Label lblBranch;

    @FXML
    private Label lblCartId;

    @FXML
    private Label lblPhoneNumber;

    @FXML
    private Label lblSex;

    @FXML
    private Label lblDateOfBirth;

    @FXML
    private Label lblJoinedDate;
    
    @FXML
    private Button btnManuFacturer;

    @FXML
    private Button btnCategory;

    @FXML
    private Button btnProduct;

    @FXML
    private Button btnTypeMember;

    @FXML
    private Button btnMember;

    @FXML
    private Button btnStaff;

    @FXML
    private Button btnBranch;

    @FXML
    private Button btnUnit;

    @FXML
    private Button btnBill;

    @FXML
    private Button btnSalePercent;

    @FXML
    private Button btnSale;

    @FXML
    private Button btnLimitSale;

    @FXML
    private Button btnSignOut;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Trang tổng quan - OU Market");
        setInfoAcount();
        this.btnManuFacturer.setOnMouseClicked(mouseEvent -> setManufacturer());
        this.btnCategory.setOnMouseClicked(mouseEvent -> setCategory());
        this.btnProduct.setOnMouseClicked(mouseEvent -> setProduct());
        this.btnTypeMember.setOnMouseClicked(mouseEvent -> setTypeMember());
        this.btnMember.setOnMouseClicked(mouseEvent -> setMember());
        this.btnStaff.setOnMouseClicked(mouseEvent -> setStaff());
        this.btnBranch.setOnMouseClicked(mouseEvent -> setBranch());
        this.btnUnit.setOnMouseClicked(mouseEvent -> setUnit());
        this.btnBill.setOnMouseClicked(mouseEvent -> setBill());
        this.btnSalePercent.setOnMouseClicked(mouseEvent -> setSalePercent());
        this.btnSale.setOnMouseClicked(mouseEvent -> setSale());
        this.btnLimitSale.setOnMouseClicked(mouseEvent -> setLimitSale());
        this.btnSignOut.setOnMouseClicked(mouseEvent -> setSignOut());
    }

    private void setInfoAcount() {
        Staff staff = new Staff();
        staff = App.currentStaff;
        if (App.currentStaff.getPersId() > 0) {
            this.lblUsername.setText(":   " + staff.getStaUsername());
            this.lblLastName.setText(":   " + staff.getPersLastName());
            this.lblFirstName.setText(":   " + staff.getPersFirstName());
            this.lblCartId.setText(":   " + staff.getPersIdCard());
            this.lblPhoneNumber.setText(":   " + staff.getPersPhoneNumber());
            String sex = staff.getPersSex() == Byte.parseByte("1") ? "Nam" : "Nữ";
            this.lblSex.setText(":   " + sex);
            this.lblDateOfBirth.setText(":   " + staff.getPersDateOfBirth());
            this.lblJoinedDate.setText(":   " + staff.getPersJoinedDate());
            this.lblBranch.setText(":   " + staff.getBranch().getBraName());
        }
    }

    // quản lý nhà sản xuất
    private void setManufacturer() {
        try {
            App.setRoot("manufacturer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý loại sản phẩm
    private void setCategory() {
        try {
            App.setRoot("category");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý sản phẩm
    private void setProduct() {
        try {
            App.setRoot("product");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý loại thành viên
    private void setTypeMember() {
        try {
            App.setRoot("member-type");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý thành viên
    private void setMember() {
        try {
            App.setRoot("member");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý nhân viên
    private void setStaff() {
        try {
            App.setRoot("staff");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý nhánh
    private void setBranch() {
        try {
            App.setRoot("branch");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý đơn vị
    private void setUnit() {
        try {
            App.setRoot("unit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý phần trăm giảm giá
    private void setSalePercent() {
        try {
            App.setRoot("sale-percent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý giảm giá không thời hạn
    private void setSale() {
        try {
            App.setRoot("sale");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý giảm giá có thời hạn
    private void setLimitSale() {
        try {
            App.setRoot("limit-sale");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // quản lý bill
    private void setBill() {
        try {
            App.setRoot("bill");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // đăng xuất
    private void setSignOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Đăng xuất");
        alert.setHeaderText("Bạn có muốn đăng xuất không ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            try {
                App.currentStaff = null;
                App.setRoot("sign-in");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
