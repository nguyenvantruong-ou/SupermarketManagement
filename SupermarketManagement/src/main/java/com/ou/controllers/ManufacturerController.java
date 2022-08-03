package com.ou.controllers;


import com.ou.pojos.Manufacturer;
import com.ou.services.ManufacturerService;
import com.ou.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManufacturerController implements Initializable {
    private final static ManufacturerService MANUFACTURER_SERVICE;

    static {
        MANUFACTURER_SERVICE = new ManufacturerService();
    }

    @FXML
    private TableView<Manufacturer> tbvManufacturer;

    @FXML
    private TextField txtSearchManName;

    @FXML
    private TextField txtManId;

    @FXML
    private TextField txtManName;

    @FXML
    private TextField txtManProductAmount;

    @FXML
    private TextField txtManIsActive;

    @FXML
    private Text textManAmount;

    @FXML
    private Button btnAddMan;

    @FXML
    private Button btnEditMan;

    @FXML
    private Button btnDelMan;

    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý nhà sản xuất - OU Market");
        this.initInputData();
        this.initManufacturerTbv();
        this.loadManufacturerTbvColumns();
        this.loadManufacturerTbvData(this.txtSearchManName.getText());
        this.loadManufacturerAmount();
        this.tbvManufacturer.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Manufacturer>) e -> changeInputData());
        this.btnAddMan.setOnMouseClicked(e -> addManufacturer());
        this.btnEditMan.setOnMouseClicked(e -> updateManufacturer());
        this.btnDelMan.setOnMouseClicked(e -> deleteManufacturer());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchManName.textProperty().addListener(e -> loadManufacturerTbvData(this.txtSearchManName.getText()));
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtManId.setDisable(true);
        this.txtManProductAmount.setDisable(true);
        this.txtManIsActive.setDisable(true);
    }

    // Khởi tạo các thuộc tính của table view liệu chi nhánh
    private void initManufacturerTbv() {
        this.tbvManufacturer.setPlaceholder(new Label("Không có nhà sản xuất nào!"));
        this.tbvManufacturer.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvManufacturer.setEditable(false);
    }

    // Lấy dữ liệu cho table view
    private void loadManufacturerTbvData(String kw) {
        try {
            this.tbvManufacturer.setItems(FXCollections.observableList(MANUFACTURER_SERVICE.getManufacturers(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadManufacturerTbvColumns() {
        TableColumn<Manufacturer, Integer> manIdColumn = new TableColumn<>("Mã nhà sản xuất");
        TableColumn<Manufacturer, String> manNameColumn = new TableColumn<>("Tên nhà sản xuất");
        TableColumn<Manufacturer, String> manProductAmountColumn = new TableColumn<>("Số lượng sản phẩm");
        TableColumn<Manufacturer, String> manIsActive = new TableColumn<>("Trạng thái");
        manIdColumn.setCellValueFactory(new PropertyValueFactory<>("manId"));
        manNameColumn.setCellValueFactory(new PropertyValueFactory<>("manName"));
        manProductAmountColumn.setCellValueFactory(new PropertyValueFactory<>("productAmount"));
        manIsActive.setCellValueFactory(new PropertyValueFactory<>("manIsActive"));
        manIdColumn.setPrefWidth(300);
        manNameColumn.setPrefWidth(700);
        manProductAmountColumn.setPrefWidth(300);
        manIsActive.setPrefWidth(200);
        manIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvManufacturer.getColumns().addAll(manIdColumn, manNameColumn, manProductAmountColumn, manIsActive);
    }

    // Lấy số lượng chi nhánh
    private void loadManufacturerAmount() {
        try {
            this.textManAmount.setText(String.valueOf(MANUFACTURER_SERVICE.getManufacturerAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Manufacturer selectedMan = this.tbvManufacturer.getSelectionModel().getSelectedItem();
        if (selectedMan != null) {
            this.txtManId.setText(String.valueOf(selectedMan.getManId()));
            this.txtManName.setText(selectedMan.getManName());
            this.txtManProductAmount.setText(String.valueOf(selectedMan.getProductAmount()));
            this.txtManIsActive.setText(selectedMan.getManIsActive() ? "Đang hoạt động" : "Ngưng hoạt động");
        }
    }

    // reset dữ liệu vùng input
    private void clearInputData() {
        this.txtManId.setText("");
        this.txtManName.setText("");
        this.txtManProductAmount.setText("0");
    }

    // reload dữ liệu
    private void reloadData() {
        loadManufacturerTbvData(this.txtSearchManName.getText());
        loadManufacturerAmount();
        clearInputData();
    }

    // Thêm một nhà sản xuất mới
    private void addManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManName(this.txtManName.getText());
        try {
            if (MANUFACTURER_SERVICE.addManufacturer(manufacturer)) {
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin một nhà sản xuất
    private void updateManufacturer() {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setManId(Integer.parseInt(this.txtManId.getText()));
            manufacturer.setManName(this.txtManName.getText());
            manufacturer.setManIsActive(this.txtManIsActive.getText().equals("Đang hoạt động"));
            if (MANUFACTURER_SERVICE.updateManufacturer(manufacturer)) {
                AlertUtils.showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Sửa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException numberFormatException) {
            AlertUtils.showAlert("Sửa thất bại! Vui lòng chọn nhà sản xuất để sửa", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một nhà sản xuất
    private void deleteManufacturer() {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setManId(Integer.parseInt(this.txtManId.getText()));
            if (MANUFACTURER_SERVICE.deleteManufacturer(manufacturer)) {
                AlertUtils.showAlert("Xoá thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException inputMismatchException) {
            AlertUtils.showAlert("Xóa thất bại! Vui lòng chọn nhà sản xuất cần xóa!", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Trở về giao diện ban đầu
    private void backMenu() {
        try {
            App.setRoot("homepage-admin");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
