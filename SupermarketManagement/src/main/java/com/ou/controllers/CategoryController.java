/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ou.controllers;

import com.ou.pojos.Category;
import com.ou.services.CategoryService;
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

/**
 *
 * @author danhn
 */
public class CategoryController implements Initializable {

    private static final CategoryService CATEGORY_SERVICE;

    static {
        CATEGORY_SERVICE = new CategoryService();
    }
    @FXML
    TableView<Category> tbvCategory;
    @FXML
    TextField txtSearchCatName;
    @FXML
    TextField txtCatId;
    @FXML
    TextField txtCatName;
    @FXML
    Text textCatAmount;
    @FXML
    TextField txtCatProductAmount;
    @FXML
    TextField txtCatIsActive;
    @FXML
    Button btnAddCat;
    @FXML
    Button btnEditCat;
    @FXML
    Button btnDelCat;
    @FXML
    Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ((Stage)App.window).setTitle("Quản lý danh mục - OU Market");
        this.initInputData();
        this.initCategoryTbv();
        this.loadCategoryTbvColumns();
        this.loadCategoryTbvData(this.txtSearchCatName.getText());
        this.loadCategoryAmount();
        this.tbvCategory.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Category>) e -> changeInputData());
        this.btnAddCat.setOnMouseClicked(e -> addCategory());
        this.btnEditCat.setOnMouseClicked(e -> updateCategory());
        this.btnDelCat.setOnMouseClicked(e -> deleteCategory());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchCatName.textProperty().addListener(e -> loadCategoryTbvData(this.txtSearchCatName.getText()));
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtCatIsActive.setDisable(true);
        this.txtCatProductAmount.setDisable(true);
        this.txtCatId.setDisable(true);
    }

    // Lấy dữ liệu cho table view
    private void loadCategoryTbvData(String kw) {
        try {
            this.tbvCategory.setItems(FXCollections.observableList(CATEGORY_SERVICE.getCategories(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Khởi tạo các thuộc tính của table view liệu chi nhánh
    private void initCategoryTbv() {
        this.tbvCategory.setPlaceholder(new Label("Không có danh mục sản phẩm nào!"));
        this.tbvCategory.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvCategory.setEditable(false);
    }
    // reset dữ liệu vùng input

    private void clearInputData() {
        this.txtCatId.setText("");
        this.txtCatName.setText("");
        this.txtCatIsActive.setText("");
        this.txtCatProductAmount.setText("0");
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Category selectedCat = this.tbvCategory.getSelectionModel().getSelectedItem();
        if (selectedCat != null) {
            this.txtCatId.setText(String.valueOf(selectedCat.getCatId()));
            this.txtCatName.setText(selectedCat.getCatName());
            this.txtCatProductAmount.setText(String.valueOf(selectedCat.getProductAmount()));
            if(selectedCat.getCatIsActive())
                this.txtCatIsActive.setText("Đang hoạt động");
            else
                this.txtCatIsActive.setText("Ngưng hoạt động");
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadCategoryTbvColumns() {
        TableColumn<Category, Integer> catIdColumn = new TableColumn<>("Mã danh mục");
        TableColumn<Category, String> catNameColumn = new TableColumn<>("Tên danh mục");
        TableColumn<Category, Integer> catProAmountColumn = new TableColumn<>("Số sản phẩm");
        TableColumn<Category, String> catIsActiveColumn = new TableColumn<>("Trạng thái");
        catIdColumn.setCellValueFactory(new PropertyValueFactory<>("catId"));
        catNameColumn.setCellValueFactory(new PropertyValueFactory<>("catName"));
        catProAmountColumn.setCellValueFactory(new PropertyValueFactory<>("productAmount"));
        catIsActiveColumn.setCellValueFactory(new PropertyValueFactory<>("catIsActive"));
        catIdColumn.setPrefWidth(300);
        catNameColumn.setPrefWidth(700);
        catProAmountColumn.setPrefWidth(300);
        catIsActiveColumn.setPrefWidth(200);
        catIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvCategory.getColumns().addAll(catIdColumn, catNameColumn, catProAmountColumn, catIsActiveColumn);
    }

    // load data
    private void reloadData() {
        loadCategoryTbvData(this.txtSearchCatName.getText());
        loadCategoryAmount();
        clearInputData();
    }

    // Lấy số lượng danh mục
    private void loadCategoryAmount() {
        try {
            this.textCatAmount.setText(String.valueOf(CATEGORY_SERVICE.getCategoryAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thêm một danh mục mới
    private void addCategory() {
        Category category = new Category();
        if(txtCatName.getText().length() > 200){
            AlertUtils.showAlert("Sửa thất bại! tên danh mục vui lòng không quá 200 kí tự", Alert.AlertType.ERROR);
            reloadData();
            return;
        }else
            category.setCatName(this.txtCatName.getText());
        try {
            if (CATEGORY_SERVICE.addCategory(category)) {
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin một danh mục
    private void updateCategory() {
        Category category = new Category();
        try {
            category.setCatId(Integer.parseInt(this.txtCatId.getText()));
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Sửa thất bại! Vui lòng chọn danh mục để sửa", Alert.AlertType.ERROR);
            return;
        }
        if(txtCatName.getText().length() > 200){
            AlertUtils.showAlert("Sửa thất bại! tên danh mục vui lòng không quá 200 kí tự", Alert.AlertType.ERROR);
            reloadData();
            return;
        }else
            category.setCatName(this.txtCatName.getText());
        try {
            if (CATEGORY_SERVICE.updateCategory(category)) {
                AlertUtils.showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Sửa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một danh mục
    private void deleteCategory() {
        Category category = new Category();
        try {
            category.setCatId(Integer.parseInt(this.txtCatId.getText()));
        } catch (NumberFormatException inputMismatchException) {
            AlertUtils.showAlert("Xóa thất bại! Vui lòng chọn danh mục cần xóa!", Alert.AlertType.ERROR);
            return;
        }
        try {
            if (CATEGORY_SERVICE.deleteCategory(category)) {
                AlertUtils.showAlert("Xoá thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
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
