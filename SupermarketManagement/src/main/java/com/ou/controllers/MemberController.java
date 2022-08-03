package com.ou.controllers;

import com.ou.pojos.Member;
import com.ou.pojos.MemberType;
import com.ou.services.MemberService;
import com.ou.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class MemberController implements Initializable {

    private static final MemberService MEMBER_SERVICE;
    private static final StringConverter<LocalDate> STRING_CONVERTER;
    static {
        MEMBER_SERVICE = new MemberService();
        STRING_CONVERTER = new StringConverter<>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return null;
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
    TextField txtMemId;
    @FXML
    TextField txtMemFirstName;
    @FXML
    TextField txtMemLastName;
    @FXML
    ComboBox cbMemSex;
    @FXML
    TextField txtMemCardId;
    @FXML
    TextField txtMemPhoneNumber;
    @FXML
    TextField txtMemTotalPurchase;
    @FXML
    TextField txtSearchMemName;
    @FXML
    TextField txtMemType;
    @FXML
    TextField txtMemIsActive;
    @FXML
    DatePicker dpMemDoB;
    @FXML
    DatePicker dpMemJoinedDate;
    @FXML
    Button btnAdd;
    @FXML
    Button btnEdit;
    @FXML
    Button btnDelete;
    @FXML
    Button btnBack;
    @FXML
    TableView tbvMember;
    @FXML
    Text textMemAmount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý thành viên - OU Market");
        this.initInputData();
        this.initComboBoxSexData();
        this.initMemberTbv();
        this.loadMemberTbvColumns();
        this.loadMemberTbvData(txtSearchMemName.getText());
        this.loadMemberAmount();
        this.tbvMember.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Member>) e -> changeInputData());
        this.btnAdd.setOnMouseClicked(e -> addMember());
        this.btnEdit.setOnMouseClicked(e -> updateMember());
        this.btnDelete.setOnMouseClicked(e -> deleteMember());
        this.btnBack.setOnMouseClicked(e->backToMenu());
        this.txtSearchMemName.textProperty().addListener(e -> loadMemberTbvData(this.txtSearchMemName.getText().trim()));
    }

    // KHởi tạo các thuộc tính của vùng input
    private void initInputData() {
        this.txtMemIsActive.setDisable(true);
        this.txtMemId.setDisable(true);
        this.txtMemType.setDisable(true);
        this.txtMemTotalPurchase.setDisable(true);
        this.dpMemJoinedDate.setDisable(true);
        this.dpMemJoinedDate.setConverter(STRING_CONVERTER);
        this.dpMemDoB.setConverter(STRING_CONVERTER);
    }

    //khởi tạo giá trị combobox giới tính
    private void initComboBoxSexData(){
        cbMemSex.getItems().add("Nam");
        cbMemSex.getItems().add("Nữ");
        cbMemSex.getSelectionModel().selectFirst();
    }

    // Lấy dữ liệu cho table view
    private void loadMemberTbvData(String kw) {
        try{
            this.tbvMember.setItems(FXCollections.observableList(MEMBER_SERVICE.getMembers(kw)));
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Khởi tạo các thuộc tính của table view thành viên
    private void initMemberTbv() {
        this.tbvMember.setPlaceholder(new Label("Không có thành viên nào!"));
        this.tbvMember.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvMember.setEditable(false);
    }

    // reset dữ liệu vùng input
    private void clearInputData() {
        this.txtMemId.setText("");
        this.txtMemType.setText("");
        this.txtMemIsActive.setText("");
        this.txtMemFirstName.setText("");
        this.txtMemLastName.setText("");
        this.txtMemTotalPurchase.setText("0");
        this.txtMemPhoneNumber.setText("");
        this.txtMemCardId.setText("");
        this.cbMemSex.getSelectionModel().selectFirst();
        this.dpMemDoB.setValue(LocalDate.now());
        this.dpMemJoinedDate.setValue(LocalDate.now());
    }

    // Thiết lập vùng dữ liệu input khi lựa chọn thay đổi dưới table view
    private void changeInputData() {
        Member selectedMem = (Member) this.tbvMember.getSelectionModel().getSelectedItem();
        if (selectedMem != null) {
            this.txtMemId.setText(String.valueOf(selectedMem.getPersId()));
            this.txtMemLastName.setText(selectedMem.getPersLastName());
            this.txtMemFirstName.setText(selectedMem.getPersFirstName());
            this.txtMemType.setText(selectedMem.getMemberType().getMemtName() == null ? "Chưa đủ điểm tích lũy" :
                    selectedMem.getMemberType().getMemtName());
            this.txtMemTotalPurchase.setText(String.valueOf(selectedMem.getMemTotalPurchase()));
            this.txtMemCardId.setText(selectedMem.getPersIdCard());
            this.txtMemPhoneNumber.setText(selectedMem.getPersPhoneNumber());
            this.cbMemSex.setValue(selectedMem.getPersSex() == 1 ? "Nam" : "Nữ");
            this.dpMemDoB.setValue(LocalDate.parse(selectedMem.getPersDateOfBirth().toString()));
            this.dpMemJoinedDate.setValue(LocalDate.parse(selectedMem.getPersJoinedDate().toString()));
            this.txtMemIsActive.setText(selectedMem.getPersIsActive() ? "Đang hoạt động" : "Ngưng hoạt động");

        }
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadMemberTbvColumns() {
        TableColumn<Member, Integer> memIdColumn = new TableColumn<>("Mã");
        TableColumn<Member, String> memLastNameColumn = new TableColumn<>("Họ và đệm");
        TableColumn<Member, String> memFirstNameColumn = new TableColumn<>("Tên");
        TableColumn<Member, String> memSexColumn = new TableColumn<>("Giới tính");
        TableColumn<Member, String> memIsActiveColumn = new TableColumn<>("Trạng thái");
        TableColumn<Member, String> memCardIdColumn = new TableColumn<>("Số tài khoản");
        TableColumn<Member, String> memPhoneNumberColumn = new TableColumn<>("Số điện thoại");
        TableColumn<Member, String> memDoBColumn = new TableColumn<>("Ngày sinh");
        TableColumn<Member, String> memJoinedDateColumn = new TableColumn<>("Ngày tham gia");
        TableColumn<Member, String> memTotalPurchaseColumn = new TableColumn<>("Tổng tiền đã mua");
        TableColumn<Member, MemberType> memTypeColumn = new TableColumn<>("Loại thành viên");
        memIdColumn.setCellValueFactory(new PropertyValueFactory<>("persId"));
        memFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("persFirstName"));
        memLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("persLastName"));
        memSexColumn.setCellValueFactory(new PropertyValueFactory<>("persSex"));
        memCardIdColumn.setCellValueFactory(new PropertyValueFactory<>("persIdCard"));
        memPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("persPhoneNumber"));
        memJoinedDateColumn.setCellValueFactory(new PropertyValueFactory<>("persJoinedDate"));
        memDoBColumn.setCellValueFactory(new PropertyValueFactory<>("persDateOfBirth"));
        memIsActiveColumn.setCellValueFactory(new PropertyValueFactory<>("persIsActive"));
        memTypeColumn.setCellValueFactory(new PropertyValueFactory<>("memberType"));
        memTotalPurchaseColumn.setCellValueFactory(new PropertyValueFactory<>("memTotalPurchase"));
        memIdColumn.setPrefWidth(50);
        memFirstNameColumn.setPrefWidth(100);
        memLastNameColumn.setPrefWidth(150);
        memSexColumn.setPrefWidth(100);
        memCardIdColumn.setPrefWidth(150);
        memPhoneNumberColumn.setPrefWidth(150);
        memJoinedDateColumn.setPrefWidth(150);
        memDoBColumn.setPrefWidth(150);
        memIsActiveColumn.setPrefWidth(100);
        memTypeColumn.setPrefWidth(250);
        memTotalPurchaseColumn.setPrefWidth(150);

        memIdColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvMember.getColumns().addAll(memIdColumn, memLastNameColumn, memFirstNameColumn, memSexColumn,
                memIsActiveColumn, memCardIdColumn, memPhoneNumberColumn, memDoBColumn, memJoinedDateColumn,
                memTotalPurchaseColumn, memTypeColumn);
    }

    // load data
    private void reloadData() throws SQLException {
        loadMemberTbvData(this.txtSearchMemName.getText());
        loadMemberAmount();
        clearInputData();
    }

    // Lấy số lượng thành viên
    private void loadMemberAmount() {
        try {
            this.textMemAmount.setText(String.valueOf(MEMBER_SERVICE.getMemberAmount()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // thêm mới 1 thành viên
    private void addMember() {
        Member member = new Member();
        if(txtMemFirstName.getText().length() > 20){
            AlertUtils.showAlert("Thêm thất bại! Tên chỉ được phép 20 kí tự", Alert.AlertType.ERROR);
            return;
        }
        if(txtMemLastName.getText().length() > 50){
            AlertUtils.showAlert("Thêm thất bại! Họ và đệm chỉ được phép 50 kí tự", Alert.AlertType.ERROR);
            return;
        }
        setInfo(member);
        if(dpMemDoB.getValue() != null)
            member.setPersDateOfBirth(Date.valueOf(dpMemDoB.getValue()));
        try {
            if (MEMBER_SERVICE.addMember(member)) {
                AlertUtils.showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Thêm thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // sửa thông tin thành viên
    private void updateMember(){
        Member member = new Member();
        if(txtMemFirstName.getText().length() > 20){
            AlertUtils.showAlert("Sửa thất bại! Tên chỉ được phép 20 kí tự", Alert.AlertType.ERROR);
            return;
        }
        if(txtMemLastName.getText().length() > 50){
            AlertUtils.showAlert("Sửa thất bại! Họ và đệm chỉ được phép 50 kí tự", Alert.AlertType.ERROR);
            return;
        }
        if(txtMemId.getText() != null && !txtMemId.getText().isEmpty())
            member.setPersId(Integer.parseInt(txtMemId.getText()));
        setInfo(member);
        member.setPersIsActive(Objects.equals(txtMemIsActive.getText(), "Đang hoạt động"));
        if(dpMemDoB.getValue() != null)
            member.setPersDateOfBirth(Date.valueOf(dpMemDoB.getValue()));
        try {
            if(Objects.equals(txtMemIsActive.getText(), "Ngưng hoạt động"))
                AlertUtils.showAlert("Cập nhật thất bại! vì thành viên đã ngưng hoạt động!!!", Alert.AlertType.ERROR);
            else
                if (MEMBER_SERVICE.updateMember(member)) {
                    AlertUtils.showAlert("Cập nhật thành công", Alert.AlertType.INFORMATION);
                    reloadData();
                } else {
                    AlertUtils.showAlert("Cập nhật thất bại!", Alert.AlertType.ERROR);
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //xoa 1 thanh vien
    private void deleteMember() {
        Member member = new Member();
        if(Objects.equals(txtMemIsActive.getText(), "Ngưng hoạt động")){
            AlertUtils.showAlert("Xóa thất bại! Thành viên đã ngưng hoạt động!", Alert.AlertType.ERROR);
            return;
        }
        try {
            member.setPersId(Integer.parseInt(this.txtMemId.getText()));
        } catch (NumberFormatException inputMismatchException) {
            AlertUtils.showAlert("Xóa thất bại! Vui lòng chọn thành viên cần xóa!", Alert.AlertType.ERROR);
            return;
        }
        try {
            if (MEMBER_SERVICE.deleteMember(member)) {
                AlertUtils.showAlert("Xoá thành công", Alert.AlertType.INFORMATION);
                reloadData();
            } else {
                AlertUtils.showAlert("Xóa thất bại!", Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void backToMenu() {
        try {
            App.setRoot("homepage-admin");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setInfo(Member member){
        member.setPersFirstName(txtMemFirstName.getText().trim());
        member.setPersLastName(txtMemLastName.getText().trim());
        String phoneNumber = txtMemPhoneNumber.getText().trim();
        String idCard = txtMemCardId.getText().trim();
        if(phoneNumber.matches("\\d+") && idCard.matches("\\d+") &&
                phoneNumber.length() == 10 && idCard.length() <= 12 && idCard.length() >= 9) {
            member.setPersPhoneNumber(phoneNumber);
            member.setPersIdCard(idCard);
        }
        member.setPersSex((byte) (Objects.equals(cbMemSex.getValue().toString(), "Nam") ? 1 : 0));
        member.setPersIsActive(!Objects.equals(txtMemIsActive.getText(), "Ngưng hoạt động"));
    }
}
