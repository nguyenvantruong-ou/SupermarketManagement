package com.ou.controllers;

import com.ou.pojos.Staff;
import com.ou.services.StaffService;
import com.ou.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    private final static StaffService STAFF_SERVICE;
    private final static StringConverter<LocalDate> STRING_CONVERTER;
    static {
        STAFF_SERVICE = new StaffService();
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
    private AnchorPane apId;

    @FXML
    private Label lblId;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtIdCard;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private ComboBox cbSex;

    @FXML
    private DatePicker dpDateOfBirth;

    @FXML
    private TextField txtActive;

    @FXML
    private ComboBox cbBranch;

    @FXML
    private ComboBox cbRole;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Staff> tbvStaff;

    @FXML
    private TextField txtUsername;

    @FXML
    private CheckBox chbAdd;

    @FXML
    private CheckBox chbEdit;

    @FXML
    private CheckBox chbDelete;

    @FXML
    private PasswordField pwPassword;

    @FXML
    private PasswordField pwConfirmPw;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnDelete;

    @FXML
    private  Button btnBack;

    @FXML
    private CheckBox resetPw;

    @FXML
    private AnchorPane apPassword;

    private Integer id = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ((Stage)App.window).setTitle("Quản lý nhân viên - OU Market");
        this.loadStaffTbvColumns();
        this.setTbvStaff();
        this.loadTbvData();
        this.setCbRole();
        this.setCbSex();
        this.loadListBranch();
        this.tbvStaff.getSelectionModel().getSelectedItems()
                .addListener((ListChangeListener<? super Staff>) e -> setTextData());

        // gán sự kiện cho các button thêm, sửa , xóa
        this.btnAdd.setOnMouseClicked(mouseEvent -> addStaff());
        this.btnEdit.setOnMouseClicked(mouseEvent -> updateStaff());
        this.btnDelete.setOnMouseClicked(mouseEvent -> deleteStaff());

        // gán sự kiện cho nút back
        this.btnBack.setOnMouseClicked(mouseEvent -> goToBack());

        this.apPassword.setDisable(true);

        // gán sự kiện cho 3 ô checkbox
        this.chbAdd.setOnMouseClicked(mouseEvent -> setCheckBoxAdd());
        this.chbEdit.setOnMouseClicked(mouseEvent -> setCheckBoxEdit());
        this.chbDelete.setOnMouseClicked(mouseEvent -> setCheckBoxDelete());
        this.txtSearch.textProperty().addListener(e -> loadTbvData());
        this.dpDateOfBirth.setConverter(STRING_CONVERTER);
        setEditableFalse();
    }

    private void goToBack(){
        try {
            App.setRoot("homepage-admin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // gán giá trị cho combobox quyền
    private  void setCbRole() {
        ObservableList<String> listRole = FXCollections.observableArrayList("Nhân viên", "Quản trị viên");
        cbRole.setItems(listRole);
    }

    // gán giá trị cho combobox quyền
    private  void setCbSex() {
        ObservableList<String> listRole = FXCollections.observableArrayList("Nam", "Nữ");
        cbSex.setItems(listRole);
    }

    // load danh sách tên nhánh
    private void loadListBranch() {
        try {
            List<String> listBr =  STAFF_SERVICE.getListBranch();
            ObservableList<String> listBra = FXCollections.observableArrayList(listBr);
            cbBranch.setItems(listBra);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // cấm chỉnh sửa cac ô input
    private void setEditableFalse() {
        this.txtLastName.setEditable(false);
        this.txtFirstName.setEditable(false);
        this.txtIdCard.setEditable(false);
        this.txtPhoneNumber.setEditable(false);
        this.dpDateOfBirth.setDisable(true);
        this.txtActive.setEditable(false);
        this.txtUsername.setEditable(false);
        this.cbSex.setDisable(true);
        this.cbBranch.setDisable(true);
        this.cbRole.setDisable(true);
        this.apPassword.setDisable(true);
        this.resetPw.setVisible(false);
    }
    // bật setEditable = true cho các ô input
    public void setEditableTrue() {
        this.txtLastName.setEditable(true);
        this.txtFirstName.setEditable(true);
        this.txtIdCard.setEditable(true);
        this.txtPhoneNumber.setEditable(true);
        this.dpDateOfBirth.setDisable(false);
        this.txtUsername.setEditable(true);
        this.cbSex.setDisable(false);
        this.cbBranch.setDisable(false);
        this.cbRole.setDisable(false);
    }

    // xử lí ô checkbox add
    private void setCheckBoxAdd() {
        if (this.chbAdd.isSelected()) {
            // ẩn nút thêm và sửa
            this.chbEdit.setSelected(false);
            this.chbDelete.setSelected(false);
            this.btnEdit.setDisable(true);
            this.btnDelete.setDisable(true);
            this.apId.setVisible(false);
            this.txtActive.setDisable(true);
            this.btnAdd.setDisable(false);
            this.apPassword.setDisable(false);
            setHideCheckboxPw();
            setEditableTrue();
        }
        else {
            this.apId.setVisible(true);
            this.btnAdd.setDisable(true);
            setEditableFalse();
        }
    }

    // Xử lí ô checkbox edit
    private void setCheckBoxEdit() {
        if(this.chbEdit.isSelected()) {
            // ẩn 2 nút thêm và xóa
            this.chbAdd.setSelected(false);
            this.chbDelete.setSelected(false);
            this.btnAdd.setDisable(true);
            this.btnDelete.setDisable(true);

            this.apId.setVisible(true);
            this.btnEdit.setDisable(false);
            this.apPassword.setDisable(true);
            this.resetPw.setVisible(true);
            this.resetPw.setSelected(false);
            setTextData();
            setEditableTrue();
            this.txtUsername.setEditable(false);
            if (id == 0) {
                clearDataInput();
                AlertUtils.showAlert("Vui lòng chọn tài khoản muốn sửa!", Alert.AlertType.INFORMATION);
            }
            else{
                setTextData();
            }
        }
        else {
            this.btnEdit.setDisable(true);
            setEditableFalse();
        }
    }

    // xử lý ô checkbox delete
    private void setCheckBoxDelete() {
        if(this.chbDelete.isSelected()) {
            // ẩn 2 nút thêm và sửa
            this.chbAdd.setSelected(false);
            this.chbEdit.setSelected(false);
            this.btnAdd.setDisable(true);
            this.btnEdit.setDisable(true);
            this.apId.setVisible(true);            // hiện Id
            this.btnDelete.setDisable(false);
            this.apPassword.setDisable(true);
            setHideCheckboxPw();
            setEditableFalse();
            if (id == 0) {
                clearDataInput();
                AlertUtils.showAlert("Vui lòng chọn tài khoản muốn xóa!", Alert.AlertType.INFORMATION);
            }
            else{
                setTextData();
            }
        }
        else {
            this.btnDelete.setDisable(true);
        }
    }

    // ẩn ô reset password
    public void setHideCheckboxPw(){
        this.resetPw.setVisible(false);
        this.resetPw.setSelected(false);
    }

    // set label cho trường hợp không có nhân viên
    private void setTbvStaff() {
        this.tbvStaff.setPlaceholder(new Label("Không có nhân viên nào!"));
        this.tbvStaff.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.tbvStaff.setEditable(false);
    }

    // Tạo các cột và match dữ liệu cho table view
    private void loadStaffTbvColumns() {
        TableColumn<Staff, Integer> idColumn = new TableColumn<>("Mã");
        TableColumn<Staff, Integer> nameStaffColumn = new TableColumn<>("Tên nhân viên");
        TableColumn<Staff, String> lastNameColumn = new TableColumn<>("Họ");
        TableColumn<Staff, String> firstNameColumn = new TableColumn<>("Tên");
        nameStaffColumn.getColumns().addAll(lastNameColumn, firstNameColumn);
        TableColumn<Staff, String> cardColumn = new TableColumn<>("Số CMND");
        TableColumn<Staff, String> usernameColum = new TableColumn<>("Tên tài khoản");
        TableColumn<Staff, String> phoneNumberColumn = new TableColumn<>("Số điện thoại");
        TableColumn<Staff, String> sexColumn = new TableColumn<>("Giới tính");
        TableColumn<Staff, Date> dateOfBirthColumn = new TableColumn<>("Ngày sinh");
        TableColumn<Staff, Date> joinedDateColumn = new TableColumn<>("Ngày tham gia");
        TableColumn<Staff, String> branchColumn = new TableColumn<>("Nhánh");
        TableColumn<Staff, String> roleColumn = new TableColumn<>("Quyền");
        TableColumn<Staff, Boolean> activeColumn = new TableColumn<>("Trạng thái");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("persId"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("persLastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("persFirstName"));
        usernameColum.setCellValueFactory(new PropertyValueFactory<>("staUsername"));
        cardColumn.setCellValueFactory(new PropertyValueFactory<>("persIdCard"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("persPhoneNumber"));
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        dateOfBirthColumn.setCellValueFactory(new PropertyValueFactory<>("persDateOfBirth"));
        joinedDateColumn.setCellValueFactory(new PropertyValueFactory<>("persJoinedDate"));
        branchColumn.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("activeName"));

        idColumn.setPrefWidth(50);
        lastNameColumn.setPrefWidth(120);
        firstNameColumn.setPrefWidth(120);
        usernameColum.setPrefWidth(120);
        cardColumn.setPrefWidth(150);
        phoneNumberColumn.setPrefWidth(150);
        sexColumn.setPrefWidth(80);
        dateOfBirthColumn.setPrefWidth(120);
        joinedDateColumn.setPrefWidth(120);
        branchColumn.setPrefWidth(200);
        roleColumn.setPrefWidth(120);
        activeColumn.setPrefWidth(150);
        idColumn.setSortType(TableColumn.SortType.DESCENDING);
        this.tbvStaff.getColumns().addAll(idColumn,nameStaffColumn, usernameColum, cardColumn, phoneNumberColumn,
                sexColumn, dateOfBirthColumn, joinedDateColumn, activeColumn, branchColumn, roleColumn);
    }

    // load du lieu cho tableview
    @FXML
    private void loadTbvData() {
        try {
            this.tbvStaff.setItems(FXCollections.observableList(STAFF_SERVICE.getListStaff(this.txtSearch.getText().trim())));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // thiết lập dữ liệu cho các thẻ input
    private void setTextData() {
        Staff selected = this.tbvStaff.getSelectionModel().getSelectedItem();
        if (selected != null) {
            this.id = selected.getPersId();
            this.lblId.setText(String.valueOf(selected.getPersId()));
            this.txtLastName.setText(String.valueOf(selected.getPersLastName()));
            this.txtFirstName.setText(String.valueOf(selected.getPersFirstName()));
            this.txtIdCard.setText(String.valueOf(selected.getPersIdCard()));
            this.txtPhoneNumber.setText(String.valueOf(selected.getPersPhoneNumber()));
            this.cbSex.setValue(String.valueOf(selected.getSex()));
            this.dpDateOfBirth.setValue(LocalDate.parse(String.valueOf(selected.getPersDateOfBirth())));
            this.txtActive.setText(String.valueOf(selected.getActiveName()));
            this.cbBranch.setValue(String.valueOf(selected.getBranchName()));
            this.cbRole.setValue(String.valueOf(selected.getRoleName()));
            this.txtUsername.setText(String.valueOf(selected.getStaUsername()));

        }
    }

    // xóa dữ liệu các thẻ input
    private void clearDataInput() {
        this.lblId.setText("");
        this.txtLastName.setText("");
        this.txtFirstName.setText("");
        this.txtIdCard.setText("");
        this.txtPhoneNumber.setText("");
        this.cbSex.setValue(null);
        this.cbSex.setPromptText("Chọn giới tính");
        this.dpDateOfBirth.setValue(LocalDate.now());
        this.cbBranch.setValue(null);
        this.cbBranch.setPromptText("Chọn nhánh");
        this.cbRole.setValue(null);
        this.cbRole.setPromptText("Chọn quyền");
        this.txtActive.setText("");
        this.txtUsername.setText("");
        this.pwPassword.setText("");
        this.pwConfirmPw.setText("");
    }

    // kiểm tra dữ liệu nhập dể thêm staff
    private boolean checkInvalidStaff() {
        if(isInvalid()) {
            if(isUsername()) {
                if (isActive()) {
                    AlertUtils.showAlert("Tên tài khoản đã tồn tại!", Alert.AlertType.ERROR);
                    return false;
                }
                else {
                    // sửa active
                    try {
                        if(STAFF_SERVICE.setActive(this.txtUsername.getText().trim())) {
                            AlertUtils.showAlert("Hệ thống đã kích hoạt tài khoản " + this.txtUsername.getText().trim()
                                    , Alert.AlertType.INFORMATION);
                            loadTbvData();
                            clearDataInput();
                            id =0;
                            return false;
                        }
                        else {
                            AlertUtils.showAlert("Vui lòng kiểm tra lại!", Alert.AlertType.ERROR);
                            return false;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        }
                }
            }
            else {
                if (isExistCardId()) {
                    AlertUtils.showAlert("Số CMND đã tồn tại!", Alert.AlertType.ERROR);
                    return false;
                }
                else{
                    return true;
                }
            }
        }
        return false;
    }

    // thêm staff
    public void addStaff(){
        if(checkInvalidStaff()) {
            if (isPasswordMatch()) {
                Staff staff = new Staff();
                staff = createStaff();

                try {
                    if (!STAFF_SERVICE.addStaff(staff)) {
                        AlertUtils.showAlert("Vui lòng kiểm tra lại thông tin!", Alert.AlertType.ERROR);
                    } else {
                        AlertUtils.showAlert("Thêm thành công!", Alert.AlertType.INFORMATION);
                        loadTbvData();
                        clearDataInput();
                        id = 0;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // kiểm tra mật khẩu có trùng khớp, hợp lệ không
    public boolean isPasswordMatch() {
        if (this.pwPassword.getText().isEmpty() || this.pwConfirmPw.getText().isEmpty()){
            AlertUtils.showAlert("Mật khẩu không được để trống !", Alert.AlertType.ERROR);
            return false;
        }
        else if (this.pwPassword.getText().length() < 6) {
            AlertUtils.showAlert("Độ dài mật khẩu phải từ 6 kí tự trở lên!", Alert.AlertType.ERROR);
            return false;
        }
        else if(!this.pwPassword.getText().equals(this.pwConfirmPw.getText())){
            AlertUtils.showAlert("Mật khẩu không khớp !", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    // tạo đối tượng staff
    private Staff createStaff() {
        Staff staff = new Staff();

        staff.setPersLastName(this.txtLastName.getText().trim());
        staff.setPersFirstName(this.txtFirstName.getText().trim());
        staff.setPersIdCard(this.txtIdCard.getText().trim());
        staff.setPersPhoneNumber(this.txtPhoneNumber.getText().trim());
        staff.setPersSex(this.cbSex.getValue() == "Nam" ? Byte.parseByte("1") : Byte.parseByte("0"));
        Date dateOB = new Date(this.dpDateOfBirth.getValue().atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toEpochSecond() * 1000);
        staff.setPersDateOfBirth(dateOB);
        //
        staff.setBranchName(this.cbBranch.getValue().toString());
        staff.setStaIsAdmin(this.cbRole.getValue().toString() == "Quản trị viên" ? true : false);
        staff.setStaUsername(this.txtUsername.getText().trim());
        staff.setStaPassword(this.pwPassword.getText());

        return staff;
    }

    // kiểm tra dữ liệu nhập
    private boolean isInvalid() {
        if(this.txtLastName.getText().trim().isEmpty() ||
            this.txtFirstName.getText().trim().isEmpty() || this.txtIdCard.getText().trim().isEmpty() ||
                this.cbSex.getValue() == null || this.dpDateOfBirth.getValue() == null ||
            this.cbBranch.getValue() == null || this.cbRole.getValue() == null){
                AlertUtils.showAlert("Vui lòng kiểm tra lại!", Alert.AlertType.ERROR);
                return false;
        }
        if (this.txtIdCard.getText().trim().length() > 12 || this.txtIdCard.getText().trim().length() < 9) {
                AlertUtils.showAlert("Số CMND phải từ 9 tới 12 kí tự!", Alert.AlertType.ERROR);
                return false;
        }
        if (this.txtPhoneNumber.getText().trim().length() != 10) {
                    AlertUtils.showAlert("Số điện thoại phải có 10 chữ số!", Alert.AlertType.ERROR);
                    return false;
        }
        if (this.txtUsername.getText().trim().isEmpty() ) {
            AlertUtils.showAlert("Vui lòng nhập tên tài khoản!", Alert.AlertType.ERROR);
            return false;
        }
        if (this.txtUsername.getText().trim().length() > 10) {
            AlertUtils.showAlert("Tên tài khoản không vượt quá 10 kí tự!", Alert.AlertType.ERROR);
            return false;
        }
        if (this.txtUsername.getText().trim().length() < 6) {
            AlertUtils.showAlert("Tên tài khoản ít nhất có 6 kí tự!", Alert.AlertType.ERROR);
            return false;
        }
        try {
            Long cmnd = Long.parseLong(this.txtIdCard.getText().trim());
        }catch(NumberFormatException e) {
            AlertUtils.showAlert("Số CMND sai định dạng!", Alert.AlertType.ERROR);
            return false;
        }

        try {
            Integer phoneNumber = Integer.parseInt(this.txtPhoneNumber.getText().trim());
        }catch(NumberFormatException e) {
            AlertUtils.showAlert("Số điện thoại sai định dạng!", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    // kiểm tra username có tồn tại hay không
    private boolean isUsername() {
        try {
            return STAFF_SERVICE.isUsername(this.txtUsername.getText().trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra tài khoản có đang còn hoạt động hay không
    private boolean isActive(){
        try {
            return STAFF_SERVICE.isActive(this.txtUsername.getText().trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // kiem tra so CMND trung khong
    private boolean isExistCardId() {
        try {
            return  STAFF_SERVICE.isExistCardId(this.txtIdCard.getText().trim());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // so sánh 2 staff
    public boolean isCompareStaff(Staff staffInput, Staff staffDatabase) {
        if (staffInput.getPersLastName().equals(staffDatabase.getPersLastName()) &&
                staffInput.getPersFirstName().equals(staffDatabase.getPersFirstName()) &&
                staffInput.getPersIdCard().equals(staffDatabase.getPersIdCard()) &&
                staffInput.getPersPhoneNumber().equals(staffDatabase.getPersPhoneNumber()) &&
                staffInput.getPersSex() == staffDatabase.getPersSex() &&
                staffInput.getPersDateOfBirth().compareTo(staffDatabase.getPersDateOfBirth()) == 0 &&
                staffInput.getBranchName().equals(staffDatabase.getBranchName()) &&
                staffInput.getStaIsAdmin() == staffDatabase.getStaIsAdmin())
            return true;
        return false;
    }

    // sửa
    public void updateStaff() {
        Staff staffInData = new Staff();
        staffInData = STAFF_SERVICE.getStaffDataById(id);

        if (id != 0) {
            if(isInvalid()) {
                if(staffInData.getPersIsActive()) {
                    Staff staff = new Staff();
                    staff = createStaff();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    staff.setPersDateOfBirth(java.sql.Date.valueOf(sdf.format(staff.getPersDateOfBirth())));

                    if (isCompareStaff(staff, staffInData) && this.resetPw.isSelected() == false) {
                        AlertUtils.showAlert("Thông tin không có gì thay đổi!", Alert.AlertType.ERROR);
                    }
                    else {
                        if (!staff.getPersIdCard().equals(staffInData.getPersIdCard())) {
                            if (!isExistCardId()) {
                                if (STAFF_SERVICE.updateStaff(staff)) {
                                    AlertUtils.showAlert("Cập nhật thành công", Alert.AlertType.INFORMATION);
                                    this.resetPw.setSelected(false);
                                    clearDataInput();
                                    loadTbvData();
                                    id = 0;
                                }
                                else
                                    AlertUtils.showAlert("Hệ thống đang bảo trì!", Alert.AlertType.ERROR);
                            }
                            else
                                AlertUtils.showAlert("Số CMND đã tồn tại!", Alert.AlertType.ERROR);
                        }
                        else {
                            if (resetPw.isSelected()) {
                                STAFF_SERVICE.resetPassword(staff.getStaUsername());
                            }
                            if (STAFF_SERVICE.updateStaff(staff)) {
                                AlertUtils.showAlert("Cập nhật thành công", Alert.AlertType.INFORMATION);
                                this.resetPw.setSelected(false);
                                clearDataInput();
                                loadTbvData();
                                id = 0;
                            }
                            else
                                AlertUtils.showAlert("Hệ thống đang bảo trì!", Alert.AlertType.ERROR);
                        }
                    }
                }
                else
                    AlertUtils.showAlert("Tài khoản này đã ngưng hoạt động!", Alert.AlertType.ERROR);
            }
        }
        else
            AlertUtils.showAlert("Vui lòng chọn tài khoản muốn sửa!", Alert.AlertType.ERROR);
    }

    // xóa
    public void deleteStaff() {
        if (id != 0) {
            if(isActive()) {
                if (STAFF_SERVICE.deleteSatff(id)) {
                    AlertUtils.showAlert("Xóa thành công !", Alert.AlertType.INFORMATION);
                    loadTbvData();
                    clearDataInput();
                    id= 0;
                }
                else
                    AlertUtils.showAlert("Hệ thống đang bảo trì!", Alert.AlertType.ERROR);
            }
            else {
                AlertUtils.showAlert("Tài khoản này đã này đã xóa từ trước!", Alert.AlertType.ERROR);
            }
        }
        else
            AlertUtils.showAlert("Vui lòng chọn tài khoản muốn xóa!", Alert.AlertType.ERROR);
    }
}
