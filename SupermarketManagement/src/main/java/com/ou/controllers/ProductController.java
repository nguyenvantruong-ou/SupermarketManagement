package com.ou.controllers;

import com.ou.pojos.*;
import com.ou.services.*;
import com.ou.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable {
    private final static ProductService PRODUCT_SERVICE;
    private final static BranchService BRANCH_SERVICE;
    private final static UnitService UNIT_SERVICE;
    private final static ManufacturerService MANUFACTURER_SERVICE;
    private final static CategoryService CATEGORY_SERVICE;
    private final static LimitSaleService LIMIT_SALE_SERVICE;

    static {
        PRODUCT_SERVICE = new ProductService();
        BRANCH_SERVICE = new BranchService();
        UNIT_SERVICE = new UnitService();
        MANUFACTURER_SERVICE=new ManufacturerService();
        CATEGORY_SERVICE= new CategoryService();
        LIMIT_SALE_SERVICE = new LimitSaleService();
    }

    @FXML
    private TableView<Product> tbvProduct;

    @FXML
    private TextField txtSearchProName;

    @FXML
    private TextField txtProId;

    @FXML
    private TextField txtProName;

    @FXML
    private TextField txtProBranchAmount;

    @FXML
    private TextField txtProUnitAmount;

    @FXML
    private TextField txtProIsActive;

    @FXML
    private ComboBox<String> cbxProCategory;

    @FXML
    private ComboBox<String> cbxProManufacturer;

    @FXML
    private Text textProAmount;

    @FXML
    private VBox vbxProUnits;

    @FXML
    private VBox vbxProBranches;

    @FXML
    private VBox vbxProLimitSales;

    @FXML
    private Button btnAddPro;

    @FXML
    private Button btnEditPro;

    @FXML
    private Button btnDelPro;

    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý sản phẩm - OU Market");
        try {
            this.initInputData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.initProductTbv();
        this.loadProductTbvColumns();
        this.loadProductTbvData(this.txtSearchProName.getText());
        this.loadProductAmount();
        this.tbvProduct.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Product>) e -> changeInputData());
        this.btnAddPro.setOnMouseClicked(e -> addProduct());
        this.btnEditPro.setOnMouseClicked(e -> updateProduct());
        this.btnDelPro.setOnMouseClicked(e -> deleteProduct());
        this.btnBack.setOnMouseClicked(e -> backMenu());
        this.txtSearchProName.textProperty().addListener(e -> loadProductTbvData(this.txtSearchProName.getText()));
        this.loadProUnitsData();
        this.loadProBranchData();
        this.loadProLimitSaleData();
    }
    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() throws SQLException {
        this.txtProId.setDisable(true);
        this.txtProIsActive.setDisable(true);
        this.txtProBranchAmount.setDisable(true);
        this.txtProUnitAmount.setDisable(true);
        List<String> categories =new ArrayList<>();
        List<String> manufacturers = new ArrayList<>();
            CATEGORY_SERVICE.getAllActiveCategory().forEach(c->{
                categories.add(c.getCatName());
            });
        MANUFACTURER_SERVICE.getAllActiveManufacturer().forEach(m->{
            manufacturers.add(m.getManName());
        });
        this.cbxProCategory.setItems(FXCollections.observableList(categories));
        this.cbxProManufacturer.setItems(FXCollections.observableList(manufacturers));
    }

    // Khởi tạo các thuộc tính của table view liệu chi nhánh
    private void initProductTbv() {
        this.tbvProduct.setPlaceholder(new Label("Không có sản phẩm nào!"));
        this.tbvProduct.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvProduct.setEditable(false);
    }

    // Lấy dữ liệu cho table view
    private void loadProductTbvData(String kw) {
        try {
            this.tbvProduct.setItems(FXCollections.observableList(PRODUCT_SERVICE.getProducts(kw)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadProductTbvColumns() {
        TableColumn<Product, Integer> proIdColumn = new TableColumn<>("Mã sản phẩm");
        TableColumn<Product, String> proNameColumn = new TableColumn<>("Tên sản phẩm");
        TableColumn<Product, Category> proCategoryColumn = new TableColumn<>("Loại sản phẩm");
        TableColumn<Product, Manufacturer> proManufacturerColumn = new TableColumn<>("Nhà sản xuất");
        TableColumn<Product, String> proIsActive = new TableColumn<>("Trạng thái");
        proIdColumn.setCellValueFactory(new PropertyValueFactory<>("proId"));
        proNameColumn.setCellValueFactory(new PropertyValueFactory<>("proName"));
        proCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        proManufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        proIsActive.setCellValueFactory(new PropertyValueFactory<>("proIsActive"));
        proIdColumn.setPrefWidth(200);
        proNameColumn.setPrefWidth(400);
        proCategoryColumn.setPrefWidth(300);
        proManufacturerColumn.setPrefWidth(400);
        proIsActive.setPrefWidth(195);
        proIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvProduct.getColumns().addAll(proIdColumn, proNameColumn, proCategoryColumn, proManufacturerColumn,
                proIsActive);
    }

    // Lấy số lượng chi nhánh
    private void loadProductAmount() {
        try {
            this.textProAmount.setText(String.valueOf(PRODUCT_SERVICE.getProductAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin đơn giá sản phẩm
    private void loadProUnitsData(){
        try {
            List<Unit> units = UNIT_SERVICE.getAllActiveUnit();
            units.forEach(u->{
                HBox hBox = new HBox();
                CheckBox checkBox = new CheckBox();
                TextField textField = new TextField("");
                Text text1 = new Text(" / ");
                Text text2 = new Text("VNĐ");
                textField.setDisable(true);
                hBox.getChildren().add(checkBox);
                checkBox.setText(u.getUniName());
                hBox.getChildren().add(text1);
                hBox.getChildren().add(textField);
                hBox.getChildren().add(text2);
                this.vbxProUnits.getChildren().add(hBox);
                checkBox.setOnMouseClicked(event->{
                    if(checkBox.isSelected())
                        textField.setDisable(false);
                    else{
                        textField.setDisable(true);
                        textField.setText("");
                    }
                });
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin chi nhánh
    private void loadProBranchData(){
        try {
            List<Branch> branches = BRANCH_SERVICE.getAllActiveBranch();
            branches.forEach(b->{
                HBox hBox = new HBox();
                CheckBox checkBox = new CheckBox();
                hBox.getChildren().add(checkBox);
                checkBox.setText(b.getBraName());
                this.vbxProBranches.getChildren().add(hBox);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy thông tin giảm giá sản phẩm
    private void loadProLimitSaleData(){
        try {
            List<LimitSale> limitSales = LIMIT_SALE_SERVICE.getLimitSales();
            limitSales.forEach(lsal->{
                HBox hBox = new HBox();
                CheckBox checkBox = new CheckBox();
                hBox.getChildren().add(checkBox);
                checkBox.setText(String.format("%d | %s - %s (%s)",lsal.getSaleId(), lsal.getLsalFromDate(),
                        lsal.getLsalToDate(), lsal.getSalePercent().toString()));
                if (lsal.getLsalToDate().before(new Date()))
                    checkBox.setDisable(true);
                this.vbxProLimitSales.getChildren().add(hBox);
            });
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu đơn giá sản phẩm khi lựa chọn thay đổi dưới table view
    private void changeProUnitData(int proId){
        try {
            List<ProductUnit> productUnits = PRODUCT_SERVICE.getProductUnits(proId);
            vbxProUnits.getChildren().forEach(r->{
                HBox row = (HBox) r;
                CheckBox checkBox = (CheckBox) row.getChildren().get(0);
                TextField textField = (TextField) row.getChildren().get(2);
                textField.setText("");
                textField.setDisable(true);
                checkBox.setSelected(false);
                productUnits.forEach(pu->{
                    if(checkBox.getText().equals(pu.getUnit().getUniName())){
                        checkBox.setSelected(true);
                        textField.setText(pu.getProPrice().toString());
                        textField.setDisable(false);
                    }
                });
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu các chi nhánh bán sản phẩm khi lựa chọn thay đổi dưới table view
    private void changeProBranchData(int proId){
        try {
            List<ProductBranch> productBranches = PRODUCT_SERVICE.getProductBranches(proId);
            vbxProBranches.getChildren().forEach(r->{
                HBox row = (HBox) r;
                CheckBox checkBox = (CheckBox) row.getChildren().get(0);
                checkBox.setSelected(false);
                productBranches.forEach(pb->{
                    if(checkBox.getText().equals(pb.getBranch().getBraName()))
                        checkBox.setSelected(true);
                });
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Thiết lập vùng dữ liệu các khuyến mãi sản phẩm khi lựa chọn thay đổi dưới table view
    private void changeProLimitSaleData(int proId){
        try {
            List<ProductLimitSale> productLimitSales = PRODUCT_SERVICE.getProductLimitSales(proId);
            vbxProLimitSales.getChildren().forEach(r->{
                HBox row = (HBox) r;
                CheckBox checkBox = (CheckBox) row.getChildren().get(0);
                checkBox.setSelected(false);
                int lsalId = Integer.parseInt(checkBox.getText().substring(0,
                        checkBox.getText().indexOf("|")).trim());
                productLimitSales.forEach(pl->{
                    if (lsalId == pl.getLimitSale().getSaleId())
                        checkBox.setSelected(true);
                });
            });
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập dữ liệu loại sản phẩm
    private void changeProCategoryData(Category category){
        int selectedIdx = this.cbxProCategory.getItems().indexOf(category.getCatName());
        this.cbxProCategory.getSelectionModel().select(selectedIdx);
    }

    // Thiết lập dữ liệu loại sản phẩm
    private void changeProManufacturerData(Manufacturer manufacturer){
        int selectedIdx = this.cbxProManufacturer.getItems().indexOf(manufacturer.getManName());
        this.cbxProManufacturer.getSelectionModel().select(selectedIdx);
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Product selectedPro = this.tbvProduct.getSelectionModel().getSelectedItem();
        if (selectedPro != null) {
            this.txtProId.setText(String.valueOf(selectedPro.getProId()));
            this.txtProName.setText(selectedPro.getProName());
            try {
                this.txtProBranchAmount.setText(String.valueOf(
                        PRODUCT_SERVICE.getProductBranchAmount(selectedPro.getProId())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                this.txtProUnitAmount.setText(String.valueOf(
                        PRODUCT_SERVICE.getProductUnitAmount(selectedPro.getProId())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.txtProIsActive.setText(selectedPro.getProIsActive() ? "Đang hoạt động" : "Ngưng hoạt động");

            changeProBranchData(selectedPro.getProId());
            changeProUnitData(selectedPro.getProId());
            changeProLimitSaleData(selectedPro.getProId());
            changeProCategoryData(selectedPro.getCategory());
            changeProManufacturerData(selectedPro.getManufacturer());
        }
    }

    // reset dữ liệu vùng input
    private void clearInputData() {
        this.txtProId.setText("");
        this.txtProName.setText("");
        this.txtProUnitAmount.setText("0");
        this.txtProBranchAmount.setText("0");
        this.txtProIsActive.setText("0");
    }

    // reload dữ liệu
    private void reloadData() {
        loadProductTbvData(this.txtSearchProName.getText());
        loadProductAmount();
        clearInputData();
    }

    private Product getProductInfo() throws SQLException {
        Product product = new Product();
        Category category = CATEGORY_SERVICE.getCategoryByName(
                this.cbxProCategory.getSelectionModel().getSelectedItem());

        Manufacturer manufacturer = MANUFACTURER_SERVICE.getManufacturerByName(
                this.cbxProManufacturer.getSelectionModel().getSelectedItem());
        List<ProductBranch> productBranches = new ArrayList<>();
        List<ProductUnit> productUnits = new ArrayList<>();
        List<ProductLimitSale> productLimitSales = new ArrayList<>();
        this.vbxProBranches.getChildren().forEach(r -> {
            HBox hBox = (HBox) r;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                ProductBranch productBranch = new ProductBranch();
                try {
                    productBranch.setBranch(BRANCH_SERVICE.getBranchByName(checkBox.getText()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                productBranches.add(productBranch);
            }
        });

        this.vbxProUnits.getChildren().forEach(r -> {
            HBox hBox = (HBox) r;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            TextField textField = (TextField) hBox.getChildren().get(2);
            String inpPrice = textField.getText();
            if (inpPrice.contains("."))
                inpPrice = textField.getText().substring(0, textField.getText().length() - 2);
            if (checkBox.isSelected() && inpPrice != null && inpPrice.length() > 0 &&
                    inpPrice.matches("\\d+")) {
                ProductUnit productUnit = new ProductUnit();
                try {
                    productUnit.setUnit(UNIT_SERVICE.getUnitByName(checkBox.getText()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                BigDecimal proPrice = BigDecimal.valueOf(Long.parseLong(inpPrice));
                productUnit.setProPrice(proPrice);
                productUnits.add(productUnit);
            }
        });

        this.vbxProLimitSales.getChildren().forEach(r -> {
            HBox hBox = (HBox) r;
            CheckBox checkBox = (CheckBox) hBox.getChildren().get(0);
            if (checkBox.isSelected()) {
                ProductLimitSale productLimitSale = new ProductLimitSale();
                LimitSale limitSale = new LimitSale();
                limitSale.setSaleId(Integer.valueOf(checkBox.getText().substring(0,
                        checkBox.getText().indexOf("|")).trim()));
                productLimitSale.setLimitSale(limitSale);
                productLimitSales.add(productLimitSale);
            }
        });

        product.setProName(this.txtProName.getText());
        product.setCategory(category);
        product.setManufacturer(manufacturer);
        product.setProductBranches(productBranches);
        product.setProductUnits(productUnits);
        product.setProductLimitSales(productLimitSales);
        return product;
    }

    // Thêm một sản phẩm mới
    private void addProduct() {
        try {
           Product product = getProductInfo();
            if (PRODUCT_SERVICE.addProduct(product)) {
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
    private void updateProduct() {
        try {
            Product product = getProductInfo();
            product.setProId(Integer.parseInt(this.txtProId.getText()));
            product.setProIsActive(this.txtProIsActive.getText().equals("Đang hoạt động"));

            if (PRODUCT_SERVICE.updateProduct(product)) {
                AlertUtils.showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Sửa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException numberFormatException) {
            AlertUtils.showAlert("Sửa thất bại! Vui lòng chọn sản phẩm để sửa", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xóa một nhà sản xuất
    private void deleteProduct() {
        try {
            Product product = new Product();
            product.setProId(Integer.parseInt(this.txtProId.getText()));
            if (PRODUCT_SERVICE.deleteProduct(product)) {
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
