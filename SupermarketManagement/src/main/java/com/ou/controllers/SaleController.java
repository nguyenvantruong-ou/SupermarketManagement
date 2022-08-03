package com.ou.controllers;

import com.ou.pojos.Sale;
import com.ou.pojos.SalePercent;
import com.ou.services.SaleService;
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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class SaleController implements Initializable {

    private static final SaleService SALE_SERVICE;
    static {
        SALE_SERVICE = new SaleService();
    }

    @FXML
    Button btnAdd;
    @FXML
    Button btnEdit;
    @FXML
    Button btnDelete;
    @FXML
    Button btnBack;
    @FXML
    TextField txtSearchSale;
    @FXML
    TableView<Sale> tbvSale;
    @FXML
    TextField txtSaleId;
    @FXML
    TextField txtSalePercent;
    @FXML
    TextField txtSaleIsActive;
    @FXML
    ComboBox cbSperId;
    @FXML
    Text textSaleAmount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý giảm giá vô thời hạn - OU Market");
        this.initInputData();
        this.initSaleTbv();
        this.loadSaleTbvColumns();
        this.loadSaleTbvData(this.txtSearchSale.getText());
        this.loadSaleAmount();
        this.loadComboBoxSalePercentId();
        this.tbvSale.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Sale>) e -> changeInputData());
        this.btnAdd.setOnMouseClicked(e -> addSale());
        this.cbSperId.getSelectionModel().selectedItemProperty().
                addListener((observableValue, o, t1) -> SaleController.this.changeTxtSalePercent());
        this.btnEdit.setOnMouseClicked(e -> updateSale());
        this.btnDelete.setOnMouseClicked(e -> deleteSale());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchSale.textProperty().addListener(e -> loadSaleTbvData(this.txtSearchSale.getText().trim()));
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtSaleId.setEditable(false);
        this.txtSalePercent.setEditable(false);
        this.txtSaleIsActive.setEditable(false);
    }

    // Khởi tạo các thuộc tính của table view mã giảm giá
    private void initSaleTbv() {
        this.tbvSale.setPlaceholder(new Label("Không có mã giảm giá nào!"));
        this.tbvSale.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvSale.setEditable(false);
    }

    // Lấy dữ liệu cho table view
    private void loadSaleTbvData(String kw) {
        try {
            this.tbvSale.setItems(FXCollections.observableList(SALE_SERVICE.getSales(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadSaleTbvColumns() {
        TableColumn<Sale, Integer> saleIdColumn = new TableColumn<>("Mã giả giá");
        TableColumn<Sale, Integer> saleSperColumn = new TableColumn<>("Phần trăm giảm giá");
        TableColumn<Sale, Boolean> saleIsActive = new TableColumn<>("Trạng thái");
        saleIdColumn.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        saleSperColumn.setCellValueFactory(new PropertyValueFactory<>("salePercent"));
        saleIsActive.setCellValueFactory(new PropertyValueFactory<>("saleIsActive"));
        saleIdColumn.setPrefWidth(150);
        saleSperColumn.setPrefWidth(400);
        saleIsActive.setPrefWidth(150);
        saleIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvSale.getColumns().addAll(saleIdColumn, saleSperColumn, saleIsActive);
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Sale selectedSale = this.tbvSale.getSelectionModel().getSelectedItem();
        if (selectedSale != null) {
            this.txtSaleId.setText(String.valueOf(selectedSale.getSaleId()));
            this.txtSaleIsActive.setText(selectedSale.getSaleIsActive()? "Đang hoạt động" : "Ngưng hoạt động");
            this.cbSperId.setValue(selectedSale.getSalePercent().getSperId());
            this.txtSalePercent.setText(String.valueOf(selectedSale.getSalePercent().getSperPercent()));
        }
    }

    // Thiết lập số phần trăm giảm giá khi comboBox lựa chọn mã phần trăm giảm giá thay đổi
    private void changeTxtSalePercent(){
        if(cbSperId.getValue() != null && !cbSperId.getValue().toString().isEmpty()){
            int sper_id = parseInt(cbSperId.getValue().toString());
            txtSalePercent.setText(String.valueOf(SALE_SERVICE.getSalePercentBySperId(sper_id)));
        }

    }

    // Lấy số lượng chi nhánh
    private void loadSaleAmount() {
        try {
            this.textSaleAmount.setText(String.valueOf(SALE_SERVICE.getSaleAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // lấy mã loại phần trăm giảm giá vào combobox
    private void loadComboBoxSalePercentId(){
        try {
            List<String> salePercentIds = SALE_SERVICE.getSalePercentId();
            cbSperId.getItems().addAll(salePercentIds);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // thêm một mã giảm giá mới
    private void addSale(){
        Sale sale = new Sale();
        try {
            if(txtSaleId.getText().trim().isEmpty() || txtSaleId.getText() == null)
                sale.setSaleId(0);
            else{
                sale.setSaleId(parseInt(txtSaleId.getText()));
                sale.setSaleIsActive(Objects.equals(txtSaleIsActive.getText(), "Đang hoạt động"));
            }
            SalePercent salePercent = new SalePercent();
            salePercent.setSperId(parseInt(cbSperId.getValue().toString()));
            sale.setSalePercent(salePercent);
        } catch (Exception e) {
            AlertUtils.showAlert("Thêm thất bại! Vui lòng chọn mã phần trăm giảm giá để thêm", Alert.AlertType.ERROR);
            return;
        }
        try {
            if(SALE_SERVICE.addSale(sale)){
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            }
            else {
            AlertUtils.showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // sửa một mã giảm giá
    private void updateSale(){
        Sale sale = new Sale();
        try {
            if(txtSaleId.getText().isEmpty() || txtSaleId.getText() == null || Objects.equals(txtSaleIsActive.getText(), "Ngưng hoạt động")){
                AlertUtils.showAlert("Cập nhật thất bại thất bại!", Alert.AlertType.ERROR);
                return;
            }
            else {
                sale.setSaleId(parseInt(txtSaleId.getText()));
                sale.setSaleIsActive(Objects.equals(txtSaleIsActive.getText(), "Đang hoạt động"));
                SalePercent salePercent = new SalePercent();
                salePercent.setSperId(parseInt(cbSperId.getValue().toString()));
                sale.setSalePercent(salePercent);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            if(SALE_SERVICE.updateSale(sale)){
                AlertUtils.showAlert("Cập nhật thành công", Alert.AlertType.INFORMATION);
                reloadData();
            }
            else {
                AlertUtils.showAlert("Cập nhật thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một mã giảm giá
    private void deleteSale(){
        Sale sale = new Sale();
        try {
            if(txtSaleId.getText().isEmpty() || txtSaleId.getText() == null){
                AlertUtils.showAlert("Xóa thất bại! vui lòng chọn mã giảm giá cần xóa", Alert.AlertType.ERROR);
                return;
            }
            else {
                sale.setSaleId(parseInt(txtSaleId.getText()));
                sale.setSaleIsActive(Objects.equals(txtSaleIsActive.getText(), "Đang hoạt động"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        try {
            if(SALE_SERVICE.deleteSale(sale)){
                AlertUtils.showAlert("Xóa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            }
            else {
                AlertUtils.showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // reser dữ liệu vùng input
    private void clearInputData(){
        txtSaleId.setText("");
        txtSalePercent.setText("");
        txtSearchSale.setText("");
        txtSaleIsActive.setText("");
        cbSperId.setValue("");
    }

    // load data
    private void reloadData() {
        loadSaleTbvData(this.txtSearchSale.getText());
        loadSaleAmount();
        clearInputData();
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
