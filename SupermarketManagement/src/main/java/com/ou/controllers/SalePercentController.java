package com.ou.controllers;

import com.ou.pojos.SalePercent;
import com.ou.services.SalePercentService;
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

public class SalePercentController implements Initializable {
    private static final SalePercentService SALE_PERCENT_SERVICE;

    static {
        SALE_PERCENT_SERVICE = new SalePercentService();
    }

    @FXML
    private TableView<SalePercent> tbvSper;

    @FXML
    private TextField txtSperId;

    @FXML
    private TextField txtSperPercent;

    @FXML
    private TextField txtSperIsActive;

    @FXML
    private TextField txtSearchSper;

    @FXML
    private Text textSperAmount;

    @FXML
    private Button btnAddSper;

    @FXML
    private Button btnDelSper;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnEditSper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage) App.window).setTitle("Quản lý phần trăm giảm giá - OU Market");
        this.initInputData();
        this.initSperTbv();
        this.loadSperTbvColumns();
        this.loadSperTbvData(-1);
        this.loadSperAmount();
        this.tbvSper.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super SalePercent>) e -> changeInputData());
        this.btnAddSper.setOnMouseClicked(e -> addSalePercent());
        this.btnEditSper.setOnMouseClicked(e -> updateSalePercent());
        this.btnDelSper.setOnMouseClicked(e -> deleteSalePercent());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchSper.textProperty().addListener(e -> {
            if (isNumber(this.txtSearchSper.getText().trim()))
                loadSperTbvData(Integer.parseInt(this.txtSearchSper.getText().trim()));
            else if (this.txtSearchSper.getText().trim().length() == 0)
                loadSperTbvData(-1);
            else loadSperTbvData(999999);
        });
    }

    private boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Khỏi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtSperId.setDisable(true);
        this.txtSperIsActive.setDisable(true);
    }

    // Khỏi tạo các thuộc tính của table view mã giảm giá
    private void initSperTbv() {
        this.tbvSper.setPlaceholder(new Label("Không có mã giảm giá nào!"));
        this.tbvSper.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    // Lấy dữ liệu cho table view
    private void loadSperTbvData(int kw) {
        try {
            this.tbvSper.setItems(FXCollections.observableList(SALE_PERCENT_SERVICE.getSalePercents(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadSperTbvColumns() {
        TableColumn<SalePercent, Integer> sperIdColumn = new TableColumn<>("Mã phiếu giảm giá");
        TableColumn<SalePercent, Float> sperPercentColumn = new TableColumn<>("Phần trăm giảm giá");
        TableColumn<SalePercent, Boolean> sperIsActiveColumn = new TableColumn<>("Trạng thái");
        sperIdColumn.setCellValueFactory(new PropertyValueFactory<>("sperId"));
        sperPercentColumn.setCellValueFactory(new PropertyValueFactory<>("sperPercent"));
        sperIsActiveColumn.setCellValueFactory(new PropertyValueFactory<>("sperIsActive"));
        sperIdColumn.setPrefWidth(200);
        sperPercentColumn.setPrefWidth(400);
        sperIsActiveColumn.setPrefWidth(149);
        sperIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvSper.getColumns().addAll(sperIdColumn, sperPercentColumn, sperIsActiveColumn);
    }

    // Lấy số lượng mã giảm giá
    private void loadSperAmount() {
        try {
            this.textSperAmount.setText(String.valueOf(SALE_PERCENT_SERVICE.getSalePercentAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        SalePercent selectedSper = this.tbvSper.getSelectionModel().getSelectedItem();
        if (selectedSper != null) {
            this.txtSperId.setText(String.valueOf(selectedSper.getSperId()));
            this.txtSperPercent.setText(String.valueOf(selectedSper.getSperPercent()));
            this.txtSperIsActive.setText(selectedSper.getSperIsActive() ? "Đang hoạt động" : "Ngưng hoạt động");
        }
    }

    // Reset dữ liệu vùng input
    private void clearInputData() {
        this.txtSperId.setText("");
        this.txtSperPercent.setText("");
        this.txtSperIsActive.setText("");
    }

    // reload dữ liệu
    private void reloadData() {
        loadSperTbvData(-1);
        loadSperAmount();
        clearInputData();
    }

    // Thêm mã giảm giá mới
    private void addSalePercent() {
        SalePercent salePercent = new SalePercent();
        try {
            int percent = Integer.parseInt(this.txtSperPercent.getText().trim());
            salePercent.setSperPercent(percent);
            if (SALE_PERCENT_SERVICE.addSalePercent(salePercent)) {
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            }  else if (percent <= 0 || percent > 100){
                AlertUtils.showAlert("Phầm trăm giảm giảm phải từ 1 -> 100. Thêm thất bại !", Alert.AlertType.ERROR);
            } else {
                AlertUtils.showAlert("Thêm thất bại !", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException numberFormatException) {

            AlertUtils.showAlert("Thêm thất bại !", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin mã giảm giá
    private void updateSalePercent() {
        try {
            SalePercent salePercent = new SalePercent();
            salePercent.setSperId(Integer.parseInt(this.txtSperId.getText()));
            salePercent.setSperPercent(Integer.parseInt(this.txtSperPercent.getText().trim()));
            salePercent.setSperIsActive(this.txtSperIsActive.getText().equals("Đang hoạt động"));

            if (SALE_PERCENT_SERVICE.updateSalePercent(salePercent)) {
                AlertUtils.showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Sửa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException numberFormatException) {
            AlertUtils.showAlert("Sửa thất bại! Vui lòng chọn mã giảm giá để sửa", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xoá mã giảm giá
    private void deleteSalePercent() {
        try {
            SalePercent salePercent = new SalePercent();
            salePercent.setSperId(Integer.parseInt(this.txtSperId.getText()));
            salePercent.setSperPercent(Integer.parseInt(this.txtSperId.getText().trim()));

            if (isActive()) {
                if (SALE_PERCENT_SERVICE.deleteSalePercent(salePercent)) {
                    AlertUtils.showAlert("Xoá thành công", Alert.AlertType.INFORMATION);
                    reloadData();
                } else {
                    AlertUtils.showAlert("Xoá thất bại", Alert.AlertType.ERROR);
                }
            } else
                AlertUtils.showAlert("Mã giảm giá đã xoá từ trước!", Alert.AlertType.ERROR);

        } catch (NumberFormatException numberFormatException) {
            AlertUtils.showAlert("Xoá thất bại! Vui lòng chọn mã giảm giá cần xoá", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra tài khoản có đang còn hoạt động hay không
    private boolean isActive(){
        try {
            return SALE_PERCENT_SERVICE.isActive(Integer.valueOf(this.txtSperId.getText()));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
