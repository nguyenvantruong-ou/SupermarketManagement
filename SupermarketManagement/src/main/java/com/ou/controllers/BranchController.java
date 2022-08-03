package com.ou.controllers;

import com.ou.pojos.Branch;
import com.ou.services.BranchService;
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

public class BranchController implements Initializable {
    private final static BranchService BRANCH_SERVICE;

    static {
        BRANCH_SERVICE = new BranchService();
    }

    @FXML
    private TableView<Branch> tbvBranch;

    @FXML
    private TextField txtSearchBraName;

    @FXML
    private TextField txtBraId;

    @FXML
    private TextField txtBraName;

    @FXML
    private TextField txtBraAddress;

    @FXML
    private TextField txtBraStaffAmount;

    @FXML
    private TextField txtBraProductAmount;

    @FXML
    private TextField txtBraIsActive;

    @FXML
    private Text textBraAmount;

    @FXML
    private Button btnAddBra;

    @FXML
    private Button btnEditBra;

    @FXML
    private Button btnDelBra;

    @FXML
    private Button btnBack;

    // Khởi tạo trước khi giao diện hiển thị
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý chi nhánh - OU Market");
        this.initInputData();
        this.initBranchTbv();
        this.loadBranchTbvColumns();
        this.loadBranchTbvData(this.txtSearchBraName.getText());
        this.loadBranchAmount();
        this.tbvBranch.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Branch>) e -> changeInputData());
        this.btnAddBra.setOnMouseClicked(e -> addBranch());
        this.btnEditBra.setOnMouseClicked(e -> updateBranch());
        this.btnDelBra.setOnMouseClicked(e -> deleteBranch());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchBraName.textProperty().addListener(e -> loadBranchTbvData(this.txtSearchBraName.getText()));
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtBraId.setDisable(true);
        this.txtBraProductAmount.setDisable(true);
        this.txtBraStaffAmount.setDisable(true);
        this.txtBraIsActive.setDisable(true);
    }

    // Khởi tạo các thuộc tính của table view liệu chi nhánh
    private void initBranchTbv() {
        this.tbvBranch.setPlaceholder(new Label("Không có chi nhánh nào!"));
        this.tbvBranch.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvBranch.setEditable(false);
    }

    // Lấy dữ liệu cho table view
    private void loadBranchTbvData(String kw) {
        try {
            this.tbvBranch.setItems(FXCollections.observableList(BRANCH_SERVICE.getBranches(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadBranchTbvColumns() {
        TableColumn<Branch, Integer> braIdColumn = new TableColumn<>("Mã chi nhánh");
        TableColumn<Branch, String> braNameColumn = new TableColumn<>("Tên chi nhánh");
        TableColumn<Branch, String> braAddressColumn = new TableColumn<>("Địa chỉ chi nhánh");
        TableColumn<Branch, Integer> braStaffAmountColumn = new TableColumn<>("Số lượng nhân viên");
        TableColumn<Branch, String> braProductAmountColumn = new TableColumn<>("Số lượng sản phẩm");
        TableColumn<Branch, Boolean> braIsActiveColumn = new TableColumn<>("Trạng thái");
        braIdColumn.setCellValueFactory(new PropertyValueFactory<>("braId"));
        braNameColumn.setCellValueFactory(new PropertyValueFactory<>("braName"));
        braAddressColumn.setCellValueFactory(new PropertyValueFactory<>("braAddress"));
        braStaffAmountColumn.setCellValueFactory(new PropertyValueFactory<>("staffAmount"));
        braProductAmountColumn.setCellValueFactory(new PropertyValueFactory<>("productAmount"));
        braIsActiveColumn.setCellValueFactory(new PropertyValueFactory<>("braIsActive"));
        braIdColumn.setPrefWidth(100);
        braNameColumn.setPrefWidth(300);
        braAddressColumn.setPrefWidth(600);
        braStaffAmountColumn.setPrefWidth(150);
        braProductAmountColumn.setPrefWidth(150);
        braIsActiveColumn.setPrefWidth(200);
        braIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvBranch.getColumns().addAll(braIdColumn, braNameColumn, braAddressColumn,
                braStaffAmountColumn, braProductAmountColumn, braIsActiveColumn);
    }

    // Lấy số lượng chi nhánh
    private void loadBranchAmount() {
        try {
            this.textBraAmount.setText(String.valueOf(BRANCH_SERVICE.getBranchAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Branch selectedBra = this.tbvBranch.getSelectionModel().getSelectedItem();
        if (selectedBra != null) {
            this.txtBraId.setText(String.valueOf(selectedBra.getBraId()));
            this.txtBraName.setText(selectedBra.getBraName());
            this.txtBraAddress.setText(selectedBra.getBraAddress());
            this.txtBraStaffAmount.setText(String.valueOf(selectedBra.getStaffAmount()));
            this.txtBraProductAmount.setText(String.valueOf(selectedBra.getProductAmount()));
            this.txtBraIsActive.setText(selectedBra.getBraIsActive() ? "Đang hoạt động" : "Ngưng hoạt động");
        }
    }

    // reset dữ liệu vùng input
    private void clearInputData() {
        this.txtBraId.setText("");
        this.txtBraName.setText("");
        this.txtBraAddress.setText("");
        this.txtBraStaffAmount.setText("0");
        this.txtBraProductAmount.setText("0");
    }

    // reload dữ liệu
    private void reloadData() {
        loadBranchTbvData(this.txtSearchBraName.getText());
        loadBranchAmount();
        clearInputData();
    }

    // Thêm một chi nhánh mới
    private void addBranch() {
        Branch branch = new Branch();
        branch.setBraName(this.txtBraName.getText());
        branch.setBraAddress(this.txtBraAddress.getText());
        try {
            if (BRANCH_SERVICE.addBranch(branch)) {
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin một chi nhánh
    private void updateBranch() {
        try {
            Branch branch = new Branch();
            branch.setBraId(Integer.parseInt(this.txtBraId.getText()));
            branch.setBraName(this.txtBraName.getText());
            branch.setBraAddress(this.txtBraAddress.getText());
            branch.setBraIsActive(this.txtBraIsActive.getText().equals("Đang hoạt động"));

            if (BRANCH_SERVICE.updateBranch(branch)) {
                AlertUtils.showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Sửa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException numberFormatException) {
            AlertUtils.showAlert("Sửa thất bại! Vui lòng chọn chi nhánh để sửa", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một chi nhánh
    private void deleteBranch() {
        try {
            Branch branch = new Branch();
            branch.setBraId(Integer.parseInt(this.txtBraId.getText()));
            if (BRANCH_SERVICE.deleteBranch(branch)) {
                AlertUtils.showAlert("Xoá thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException inputMismatchException) {
            AlertUtils.showAlert("Xóa thất bại! Vui lòng chọn chi nhánh cần xóa!", Alert.AlertType.ERROR);
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
