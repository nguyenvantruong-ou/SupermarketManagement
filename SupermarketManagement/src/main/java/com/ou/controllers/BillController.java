package com.ou.controllers;

import com.ou.pojos.*;
import com.ou.services.BillService;
import com.ou.services.ProductService;
import com.ou.utils.AlertUtils;
import com.ou.utils.PersonType;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BillController implements Initializable {
    private final static BillService BILL_SERVICE;
    private final static ProductService PRODUCT_SERVICE;
    private final static StringConverter<LocalDate> STRING_CONVERTER;
    static {
        BILL_SERVICE = new BillService();
        PRODUCT_SERVICE = new ProductService();
        STRING_CONVERTER = new StringConverter<>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String s) {
                if (s == null || s.trim().isEmpty())
                    return null;
                try{
                    return LocalDate.parse(s, dateTimeFormatter);
                }catch (DateTimeException dateTimeException){
                    return null;
                }

            }
        };
    }

    @FXML
    private TableView<Bill> tbvBill;

    @FXML
    private HBox hbxBillProduct;

    @FXML
    private Text textBillAmount;

    @FXML
    private TextField txtBillId;

    @FXML
    private DatePicker dtpBillCreatedDate;

    @FXML
    private TextField txtBillStaName;

    @FXML
    private TextField txtBillMemName;

    @FXML
    private TextField txtBillTotalMoney;

    @FXML
    private TextField txtBillCustomerMoney;

    @FXML
    private TextField txtBillToTalSaleMoney;

    @FXML
    private TextField txtSearchBillStaName;

    @FXML
    private TextField txtSearchBillMemName;

    @FXML
    private DatePicker dtpSearchBillCreatedDate;

    @FXML
    private DatePicker dtpSearchBillFromCreatedDate;

    @FXML
    private DatePicker dtpSearchBillToCreatedDate;

    @FXML
    private Button btnSearchBillCreatedDate;

    @FXML
    private Button btnSearchBillPeriodCreatedDate;

    @FXML
    private Button btnBack;


    // Lấy thông tin điều kiện lọc theo thời gian
    private List<String> getTimeCondition() {
        List<String> dates = null;
        if(this.dtpSearchBillCreatedDate.getValue()!=null){
            dates= new ArrayList<>();
            dates.add(this.dtpSearchBillCreatedDate.getValue().toString());
        }else if (this.dtpSearchBillFromCreatedDate.getValue()!=null &&
                this.dtpSearchBillToCreatedDate.getValue()!=null){
            dates = new ArrayList<>();
            dates.add(this.dtpSearchBillFromCreatedDate.getValue().toString());
            dates.add(this.dtpSearchBillToCreatedDate.getValue().toString());
        }
        return dates;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý hoá đơn - OU Market");
        this.initInputData();
        this.initBillTbv();
        this.loadBillTbvColumns();
        this.loadBillTbvData(null, null, null);
        this.loadBillAmount();
        this.tbvBill.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Bill>) e -> changeInputData());
        this.txtSearchBillStaName.textProperty().addListener(e -> {
            if (!this.txtSearchBillMemName.getText().isEmpty())
                this.txtSearchBillMemName.setText("");
            List<String> dates = getTimeCondition();
            this.loadBillTbvData(this.txtSearchBillStaName.getText(), PersonType.STAFF, dates);
        });
        this.txtSearchBillMemName.textProperty().addListener(e -> {
            if (!this.txtSearchBillStaName.getText().isEmpty())
                this.txtSearchBillStaName.setText("");
            List<String> dates = getTimeCondition();
            this.loadBillTbvData(this.txtSearchBillMemName.getText(), PersonType.MEMBER, dates);
        });
        this.btnSearchBillCreatedDate.setOnMouseClicked(e->searchByBillCreatedDate());
        this.btnSearchBillPeriodCreatedDate.setOnMouseClicked(e->searchByPeriodBillCreatedDate());
        this.btnBack.setOnMouseClicked(e -> backMenu());
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtBillId.setDisable(true);
        this.dtpBillCreatedDate.setDisable(true);
        this.dtpBillCreatedDate.setConverter(STRING_CONVERTER);
        this.dtpSearchBillCreatedDate.setConverter(STRING_CONVERTER);
        this.dtpSearchBillFromCreatedDate.setConverter(STRING_CONVERTER);
        this.dtpSearchBillToCreatedDate.setConverter(STRING_CONVERTER);
        this.txtBillStaName.setDisable(true);
        this.txtBillMemName.setDisable(true);
        this.txtBillTotalMoney.setDisable(true);
        this.txtBillCustomerMoney.setDisable(true);
        this.txtBillToTalSaleMoney.setDisable(true);

    }

    // Khởi tạo các thuộc tính của table view liệu chi nhánh
    private void initBillTbv() {
        this.tbvBill.setPlaceholder(new Label("Không có hóa đơn nào!"));
        this.tbvBill.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvBill.setEditable(false);
    }

    // Lấy dữ liệu cho table view
    private void loadBillTbvData(String kw, PersonType personType, List<String> dates) {
        try {
            this.tbvBill.setItems(FXCollections.observableList(BILL_SERVICE.getBills(kw, personType, dates)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadBillTbvColumns() {
        TableColumn<Bill, Integer> billIdColumn = new TableColumn<>("Mã hóa đơn");
        TableColumn<Bill, Member> billCustomerNameColumn = new TableColumn<>("Tên thành viên");
        TableColumn<Bill, Staff> billStaffNameColumn = new TableColumn<>("Tên nhân viên");
        TableColumn<Bill, Date> billCreatedDateColumn = new TableColumn<>("Ngày lập");
        TableColumn<Bill, Bill> billTotalMoneyColumn = new TableColumn<>("Tổng tiền thanh toán");
        TableColumn<Bill, BigDecimal> billCustomerMoneyColumn = new TableColumn<>("Tiền khách hàng đưa");
        TableColumn<Bill, BigDecimal> billTotalSaleMoneyColumn = new TableColumn<>("Tổng tiền khuyến mãi");
        billIdColumn.setCellValueFactory(new PropertyValueFactory<>("billId"));
        billCustomerNameColumn.setCellValueFactory(new PropertyValueFactory<>("member"));
        billStaffNameColumn.setCellValueFactory(new PropertyValueFactory<>("staff"));
        billCreatedDateColumn.setCellValueFactory(new PropertyValueFactory<>("billCreatedDate"));
        billTotalMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("billTotalMoney"));
        billCustomerMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("billCustomerMoney"));
        billTotalSaleMoneyColumn.setCellValueFactory(new PropertyValueFactory<>("billTotalSaleMoney"));

        billIdColumn.setPrefWidth(100);
        billCustomerNameColumn.setPrefWidth(270);
        billStaffNameColumn.setPrefWidth(300);
        billCreatedDateColumn.setPrefWidth(200);
        billTotalMoneyColumn.setPrefWidth(200);
        billCustomerMoneyColumn.setPrefWidth(200);
        billTotalSaleMoneyColumn.setPrefWidth(195);

        billIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvBill.getColumns().addAll(billIdColumn, billCustomerNameColumn, billStaffNameColumn,
                billCreatedDateColumn, billTotalMoneyColumn, billCustomerMoneyColumn,
                billTotalSaleMoneyColumn);
    }


    // Lấy dữ liệu cho product bill
    private void loadBillProductVboxData(Bill bill) {
        try {
            VBox vbxProId = (VBox) this.hbxBillProduct.getChildren().get(0);
            VBox vbxProName = (VBox) this.hbxBillProduct.getChildren().get(1);
            VBox vbxProCat = (VBox) this.hbxBillProduct.getChildren().get(2);
            VBox vbxProMan = (VBox) this.hbxBillProduct.getChildren().get(3);
            VBox vbxProAmo = (VBox) this.hbxBillProduct.getChildren().get(4);
            VBox vbxProUniName = (VBox) this.hbxBillProduct.getChildren().get(5);
            VBox vbxProPrice = (VBox) this.hbxBillProduct.getChildren().get(6);
            VBox vbxProSalePercent =  (VBox) this.hbxBillProduct.getChildren().get(7);
            this.hbxBillProduct.getChildren().forEach(col->{
                VBox vbxCol = (VBox) col;
                int amount = vbxCol.getChildren().size();
                if (amount > 1)
                    vbxCol.getChildren().subList(1, amount).clear();
            });
            BILL_SERVICE.getProductBillsByBillId(bill.getBillId()).forEach(pb -> {

                AnchorPane acpProId = new AnchorPane();
                acpProId.setPrefHeight(50);
                acpProId.setPrefWidth(100);
                Text textProId = new Text();
                Separator sptProId = new Separator();
                textProId.setLayoutY(25.0);
                sptProId.setPrefHeight(6.0);
                sptProId.setPrefWidth(100.0);
                sptProId.setLayoutY(35.0);
                textProId.setText(String.valueOf(pb.getProductUnit().getProduct().getProId()));
                textProId.setLayoutX((acpProId.getPrefWidth() -
                        textProId.getLayoutBounds().getWidth())/2);
                acpProId.getChildren().add(textProId);
                acpProId.getChildren().add(sptProId);


                AnchorPane acpProName = new AnchorPane();
                acpProName.setPrefHeight(50);
                acpProName.setPrefWidth(266);
                Text textProName = new Text();
                Separator sptProName = new Separator();
                textProName.setLayoutY(25.0);
                sptProName.setPrefHeight(6.0);
                sptProName.setPrefWidth(266.0);
                sptProName.setLayoutY(35.0);
                textProName.setText(pb.getProductUnit().getProduct().getProName());
                textProName.setLayoutX((acpProName.getPrefWidth() -
                        textProName.getLayoutBounds().getWidth())/2);
                acpProName.getChildren().add(textProName);
                acpProName.getChildren().add(sptProName);


                AnchorPane acpProCat = new AnchorPane();
                acpProCat.setPrefHeight(50);
                acpProCat.setPrefWidth(219);
                Text textProCat = new Text();
                Separator sptProCat = new Separator();
                textProCat.setLayoutY(25.0);
                sptProCat.setPrefHeight(6.0);
                sptProCat.setPrefWidth(219.0);
                sptProCat.setLayoutY(35.0);
                textProCat.setText(pb.getProductUnit().getProduct().getCategory().toString());
                textProCat.setLayoutX((acpProCat.getPrefWidth() -
                        textProCat.getLayoutBounds().getWidth())/2);
                acpProCat.getChildren().add(textProCat);
                acpProCat.getChildren().add(sptProCat);

                AnchorPane acpProMan = new AnchorPane();
                acpProMan.setPrefHeight(50);
                acpProMan.setPrefWidth(233);
                Text textProMan = new Text();
                Separator sptProMan = new Separator();
                textProMan.setLayoutY(25.0);
                sptProMan.setPrefHeight(6.0);
                sptProMan.setPrefWidth(233.0);
                sptProMan.setLayoutY(35.0);
                textProMan.setText(pb.getProductUnit().getProduct().getManufacturer().toString());
                textProMan.setLayoutX((acpProMan.getPrefWidth() -
                        textProMan.getLayoutBounds().getWidth())/2);
                acpProMan.getChildren().add(textProMan);
                acpProMan.getChildren().add(sptProMan);

                AnchorPane acpProAmo = new AnchorPane();
                acpProAmo.setPrefHeight(50);
                acpProAmo.setPrefWidth(99);
                Text textProAmo = new Text();
                Separator sptProAmo = new Separator();
                textProAmo.setLayoutY(25.0);
                sptProAmo.setPrefHeight(6.0);
                sptProAmo.setPrefWidth(99.0);
                sptProAmo.setLayoutY(35.0);
                textProAmo.setText(pb.getProAmount().toString());
                textProAmo.setLayoutX((acpProAmo.getPrefWidth() -
                        textProAmo.getLayoutBounds().getWidth())/2);
                acpProAmo.getChildren().add(textProAmo);
                acpProAmo.getChildren().add(sptProAmo);

                AnchorPane acpProUniName = new AnchorPane();
                acpProUniName.setPrefHeight(50);
                acpProUniName.setPrefWidth(134);
                Text textProUniName = new Text();
                Separator sptProUniName = new Separator();
                textProUniName.setLayoutY(25.0);
                sptProUniName.setPrefHeight(6.0);
                sptProUniName.setPrefWidth(134.0);
                sptProUniName.setLayoutY(35.0);
                textProUniName.setText(pb.getProductUnit().getUnit().getUniName());
                textProUniName.setLayoutX((acpProUniName.getPrefWidth() -
                        textProUniName.getLayoutBounds().getWidth())/2);
                acpProUniName.getChildren().add(textProUniName);
                acpProUniName.getChildren().add(sptProUniName);

                AnchorPane acpProPrice = new AnchorPane();
                acpProPrice.setPrefHeight(50);
                acpProPrice.setPrefWidth(164);
                Text textProPrice = new Text();
                Separator sptProPrice = new Separator();
                textProPrice.setLayoutY(25.0);
                sptProPrice.setPrefHeight(6.0);
                sptProPrice.setPrefWidth(164.0);
                sptProPrice.setLayoutY(35.0);
                textProPrice.setText(pb.getProductUnit().getProPrice() + " VNĐ");
                textProPrice.setLayoutX((acpProPrice.getPrefWidth() -
                        textProPrice.getLayoutBounds().getWidth())/2);
                acpProPrice.getChildren().add(textProPrice);
                acpProPrice.getChildren().add(sptProPrice);

                AnchorPane acpProSalePercent = new AnchorPane();
                acpProSalePercent.setPrefHeight(50);
                acpProSalePercent.setPrefWidth(126);
                Text textProSalePercent = new Text();
                Separator sptProSalePercent = new Separator();
                textProSalePercent.setLayoutY(25.0);
                sptProSalePercent.setPrefHeight(6.0);
                sptProSalePercent.setPrefWidth(126.0);
                sptProSalePercent.setLayoutY(35.0);
                try {
                    ProductLimitSale productLimitSale = PRODUCT_SERVICE.getProductLimitSaleOfProduct(
                            pb.getProductUnit().getProduct().getProId(),
                            BILL_SERVICE.getCreatedDateBill(bill.getBillId()));
                    if (productLimitSale != null)
                        textProSalePercent.setText(productLimitSale.getLimitSale().toString());
                    else textProSalePercent.setText("0%");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                textProSalePercent.setLayoutX((acpProSalePercent.getPrefWidth() -
                        textProSalePercent.getLayoutBounds().getWidth())/2);
                acpProSalePercent.getChildren().add(textProSalePercent);
                acpProSalePercent.getChildren().add(sptProSalePercent);

                vbxProId.getChildren().add(acpProId);
                vbxProName.getChildren().add(acpProName);
                vbxProCat.getChildren().add(acpProCat);
                vbxProMan.getChildren().add(acpProMan);
                vbxProAmo.getChildren().add(acpProAmo);
                vbxProUniName.getChildren().add(acpProUniName);
                vbxProPrice.getChildren().add(acpProPrice);
                vbxProSalePercent.getChildren().add(acpProSalePercent);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Lấy số lượng hóa đơn
    private void loadBillAmount() {
        try {
            this.textBillAmount.setText(String.valueOf(BILL_SERVICE.getBillAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Bill selectedBill = this.tbvBill.getSelectionModel().getSelectedItem();
        if (selectedBill != null) {
            this.txtBillId.setText(String.valueOf(selectedBill.getBillId()));
            String createdDate = selectedBill.getBillCreatedDate().toString();
            this.dtpBillCreatedDate.setValue(LocalDate.parse(createdDate.substring(0, createdDate.indexOf(" "))));
            this.txtBillStaName.setText(selectedBill.getStaff().toString());
            if (selectedBill.getMember() != null)
                this.txtBillMemName.setText(selectedBill.getMember().toString());
            else
                this.txtBillMemName.setText("Không có");
            this.txtBillTotalMoney.setText(selectedBill.getBillTotalMoney().toString());
            this.txtBillCustomerMoney.setText(selectedBill.getBillCustomerMoney().toString());
            this.txtBillToTalSaleMoney.setText(selectedBill.getBillTotalSaleMoney().toString());
            loadBillProductVboxData(selectedBill);
        }
    }

    // Tìm kiếm đựa vào ngày tạo hóa đơn
    private void searchByBillCreatedDate(){
        this.txtSearchBillStaName.setText("");
        this.txtSearchBillMemName.setText("");
        this.dtpSearchBillFromCreatedDate.setValue(null);
        this.dtpSearchBillToCreatedDate.setValue(null);
        LocalDate createdDate = this.dtpSearchBillCreatedDate.getValue();
        if (createdDate !=null)
            loadBillTbvData(null, PersonType.STAFF, Collections.singletonList(createdDate.toString().trim()));
        else {
            AlertUtils.showAlert("Dữ liệu không hợp lệ", Alert.AlertType.ERROR);
            this.dtpBillCreatedDate.setValue(null);
            loadBillTbvData(null, null, null);
        }
    }

    // Tìm kiếm theo khoảng thời gian
    private void searchByPeriodBillCreatedDate(){
        this.txtSearchBillMemName.setText("");
        this.txtSearchBillStaName.setText("");
        this.dtpSearchBillCreatedDate.setValue(null);
        LocalDate fromDate = this.dtpSearchBillFromCreatedDate.getValue();
        LocalDate toDate  = this.dtpSearchBillToCreatedDate.getValue();
        if (fromDate !=null && toDate!=null) {
            List<String> dates = new ArrayList<>();
            dates.add(fromDate.toString().trim());
            dates.add(toDate.toString().trim());
            loadBillTbvData(null, PersonType.STAFF, dates);
        } else{
            AlertUtils.showAlert("Dữ liệu không hợp lệ", Alert.AlertType.ERROR);
            this.dtpSearchBillFromCreatedDate.setValue(null);
            this.dtpSearchBillToCreatedDate.setValue(null);
            loadBillTbvData(null, null, null);
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
