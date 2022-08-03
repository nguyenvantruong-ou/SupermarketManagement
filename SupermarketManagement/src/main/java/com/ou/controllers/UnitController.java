package com.ou.controllers;

import com.ou.pojos.Unit;
import com.ou.services.UnitService;
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

public class UnitController implements Initializable {
    private final static UnitService UNIT_SERVICE;

    static {
        UNIT_SERVICE = new UnitService();
    }

    @FXML
    private TableView<Unit> tbvUnit;

    @FXML
    private TextField txtSearchUniName;

    @FXML
    private TextField txtUniId;

    @FXML
    private TextField txtUniName;

    @FXML
    private TextField txtUniIsActive;

    @FXML
    private Text textUniAmount;

    @FXML
    private Button btnAddUni;

    @FXML
    private Button btnEditUni;

    @FXML
    private Button btnDelUni;

    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý nhà đơn vị sản phẩm - OU Market");
        this.initInputData();
        this.initUnitTbv();
        this.loadUnitTbvColumns();
        this.loadUnitTbvData(this.txtSearchUniName.getText());
        this.loadUniAmount();
        this.tbvUnit.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Unit>) e -> changeInputData());
        this.btnAddUni.setOnMouseClicked(e -> addUnit());
        this.btnEditUni.setOnMouseClicked(e -> updateUnit());
        this.btnDelUni.setOnMouseClicked(e -> deleteUnit());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchUniName.textProperty().addListener(e -> loadUnitTbvData(this.txtSearchUniName.getText()));
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtUniId.setDisable(true);
        this.txtUniIsActive.setDisable(true);
    }

    // Khởi tạo các thuộc tính của table view liệu chi nhánh
    private void initUnitTbv() {
        this.tbvUnit.setPlaceholder(new Label("Không có đơn vị nào!"));
        this.tbvUnit.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvUnit.setEditable(false);
    }

    // Lấy dữ liệu cho table view
    private void loadUnitTbvData(String kw) {
        try {
            this.tbvUnit.setItems(FXCollections.observableList(UNIT_SERVICE.getUnits(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadUnitTbvColumns() {
        TableColumn<Unit, Integer> uniIdColumn = new TableColumn<>("Mã đơn vị");
        TableColumn<Unit, String> uniNameColumn = new TableColumn<>("Tên đơn vị");
        TableColumn<Unit, String> uniIsActive = new TableColumn<>("Trạng thái");
        uniIdColumn.setCellValueFactory(new PropertyValueFactory<>("uniId"));
        uniNameColumn.setCellValueFactory(new PropertyValueFactory<>("uniName"));
        uniIsActive.setCellValueFactory(new PropertyValueFactory<>("uniIsActive"));
        uniIdColumn.setPrefWidth(250);
        uniNameColumn.setPrefWidth(600);
        uniIsActive.setPrefWidth(150);
        uniIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvUnit.getColumns().addAll(uniIdColumn, uniNameColumn, uniIsActive);
    }

    // Lấy số lượng đơn vị
    private void loadUniAmount() {
        try {
            this.textUniAmount.setText(String.valueOf(UNIT_SERVICE.getUnitAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Unit selectedUni = this.tbvUnit.getSelectionModel().getSelectedItem();
        if (selectedUni != null) {
            this.txtUniId.setText(String.valueOf(selectedUni.getUniId()));
            this.txtUniName.setText(selectedUni.getUniName());
            this.txtUniIsActive.setText(selectedUni.getUniIsActive() ? "Đang hoạt động" : "Ngưng hoạt động");
        }
    }

    // reset dữ liệu vùng input
    private void clearInputData() {
        this.txtUniId.setText("");
        this.txtUniName.setText("");
    }

    // reload dữ liệu
    private void reloadData() {
        loadUnitTbvData(this.txtSearchUniName.getText());
        loadUniAmount();
        clearInputData();
    }

    // Thêm một đơn vị mới
    private void addUnit() {
        Unit unit = new Unit();
        unit.setUniName(this.txtUniName.getText());
        try {
            if (UNIT_SERVICE.addUnit(unit)) {
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin một đơn vị
    private void updateUnit() {
        try {
            Unit unit = new Unit();
            unit.setUniId(Integer.parseInt(this.txtUniId.getText()));
            unit.setUniName(this.txtUniName.getText());
            unit.setUniIsActive(this.txtUniIsActive.getText().equals("Đang hoạt động"));
            if (UNIT_SERVICE.updateUnit(unit)) {
                AlertUtils.showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Sửa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException numberFormatException) {
            AlertUtils.showAlert("Sửa thất bại! Vui lòng chọn đơn vị để sửa", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một đơn vị
    private void deleteUnit() {
        try {
            Unit unit = new Unit();
            unit.setUniId(Integer.parseInt(this.txtUniId.getText()));
            if (UNIT_SERVICE.deleteUnit(unit)) {
                AlertUtils.showAlert("Xoá thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException inputMismatchException) {
            AlertUtils.showAlert("Xóa thất bại! Vui lòng chọn đơn vị cần xóa!", Alert.AlertType.ERROR);
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
