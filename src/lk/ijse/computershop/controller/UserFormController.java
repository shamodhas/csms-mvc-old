package lk.ijse.computershop.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lk.ijse.computershop.model.UserModel;
import lk.ijse.computershop.to.User;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserFormController {
    @FXML
    public FontAwesomeIconView fxIconEye;
    @FXML
    public PasswordField psPassword;
    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private ComboBox<String> cmbRank;

    @FXML
    private TableColumn<User, String> colAction;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colId;

    @FXML
    private TableColumn<User, String> colName;

    @FXML
    private TableColumn<User, String> colNic;

    @FXML
    private TableColumn<User, String> colRank;

    @FXML
    private TableColumn<User, String> colTelNumber;

    @FXML
    private TableView<User> tbl;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtNic;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtTelNumber;

    ObservableList<User> userObservableList = FXCollections.observableArrayList();
    User user=null;
    boolean isValidName=false;
    boolean isValidPassword=false;
    boolean isValidNic=false;
    boolean isValidTelephoneNumber=false;
    boolean isValidEmail=false;
    boolean isUpdate=false;
    boolean isPasswordHide = true;
    public void initialize(){
        loadCmb();
        loadTable();
        reset();
        loadToolTips();
    }

    private void loadToolTips() {
        Tooltip nameTip=new Tooltip("ex: Pronet@20");
        txtName.setTooltip(nameTip);

        Tooltip psTip=new Tooltip("ex: Pronet@20");
        psPassword.setTooltip(psTip);
        txtPassword.setTooltip(psTip);

        Tooltip nicTip=new Tooltip("ex: Pronet@20");
        txtNic.setTooltip(nicTip);

        Tooltip telNumTip=new Tooltip("ex: Pronet@20");
        txtTelNumber.setTooltip(telNumTip);

        Tooltip emailTip=new Tooltip("ex: Pronet@20");
        txtEmail.setTooltip(emailTip);
    }

    private void loadCmb() {
        String[]rank={"Admin","Cashier"};
        cmbRank.getItems().addAll(rank);
    }

    private void reset() {
        btnUpdate.setDisable(true);
        btnAdd.setDisable(false);
        try {
            txtId.setText(UserModel.getNextUserId());
            txtName.clear();
            txtPassword.clear();
            psPassword.clear();
            txtNic.clear();
            txtTelNumber.clear();
            txtEmail.clear();
            cmbRank.getSelectionModel().clearSelection();
            refreshTable();
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void refreshTable() {
        userObservableList.clear();
        try {
            ArrayList<User> allUsers= UserModel.getAllUser();
            for (User user1:allUsers){
                userObservableList.add(user1);
            }
            tbl.setItems(userObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void loadTable() {
        refreshTable();
        colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colTelNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        Callback<TableColumn<User, String>, TableCell<User, String>> cellFactory = (TableColumn<User, String> param) -> {
            // make cell containing buttons
            final TableCell<User, String> cell = new TableCell<User, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        FontAwesomeIconView lock = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
                        deleteIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#ff1744;");
                        editIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#00E676;");
                        lock.setStyle(" -fx-cursor: hand ;" + "-glyph-size:30px;" + "-fx-fill:#f1c40f;");
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            user=tbl.getSelectionModel().getSelectedItem();
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Deleting a user named "+user.getUserName(),"Are You Sure ? ");
                            if (result==ButtonType.OK){
                                try {
                                    if (UserModel.deleteUser(user.getUserId())){
                                        reset();
                                        refreshTable();
                                        Project.showError(Alert.AlertType.INFORMATION, "Deleting Successfully",user.getUserName()+" was deleted");
                                    }
                                } catch (SQLException | ClassNotFoundException e) {
                                    ButtonType result2=Project.showError(Alert.AlertType.CONFIRMATION, "Deleting error",e+"\nyou want exit program ? ");
                                    if (result2==ButtonType.OK){
                                        Platform.exit();
                                    }
                                }
                            }
                        });
                        editIcon.setOnMouseClicked((MouseEvent event) -> {
                            user=tbl.getSelectionModel().getSelectedItem();
                            txtId.setText(user.getUserId());
                            txtName.setText(user.getUserName());
                            psPassword.setText(user.getUserPassword());
                            txtPassword.setText(user.getUserPassword());
                            txtNic.setText(user.getNic());
                            txtTelNumber.setText(user.getTelephoneNumber());
                            txtEmail.setText(user.getEmail());
                            cmbRank.getSelectionModel().select(user.getRank());
                            btnUpdate.setDisable(false);
                            btnAdd.setDisable(true);
                            isUpdate=true;
                        });
                        lock.setOnMouseClicked((MouseEvent event)->{
                            user=tbl.getSelectionModel().getSelectedItem();
                            txtId.setText(user.getUserId());
                            txtName.setText(user.getUserName());
                            psPassword.setText(user.getUserPassword());
                            txtPassword.setText(user.getUserPassword());
                            txtNic.setText(user.getNic());
                            txtTelNumber.setText(user.getTelephoneNumber());
                            txtEmail.setText(user.getEmail());
                            cmbRank.getSelectionModel().select(user.getRank());
                            btnUpdate.setDisable(true);
                            btnAdd.setDisable(true);
                        });
                        HBox hBox = new HBox(lock,editIcon, deleteIcon);
                        hBox.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));
                        HBox.setMargin(editIcon, new Insets(2, 2, 0, 2));
                        HBox.setMargin(lock, new Insets(2, 3, 0, 2));
                        setGraphic(hBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        colAction.setCellFactory(cellFactory);
        tbl.setItems(userObservableList);
    }

    @FXML
    void addOnAction(ActionEvent event) {
        validateAll();
        if (!isValidName)
            txtName.requestFocus();
        else if (!isValidPassword)
            (isPasswordHide?psPassword:txtPassword).requestFocus();
        else if (!isValidNic)
            txtNic.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (!isValidEmail)
            txtEmail.requestFocus();
        else if (cmbRank.getSelectionModel().isEmpty()) {
            cmbRank.setStyle("-fx-border-color: #ff0000");
            cmbRank.requestFocus();
        } else {
            String userId=txtId.getText();
            String name=txtName.getText();
            String password=(isPasswordHide?psPassword:txtPassword).getText();
            String nic=txtNic.getText();
            String telNumber=txtTelNumber.getText();
            String email=txtEmail.getText();
            String rank=cmbRank.getValue();
            try {
                String existsUserId= UserModel.existsUser(nic);
                if (existsUserId==null){
                    user = new User(userId,name,password,nic,telNumber,email,rank);
                    if (UserModel.addUser(user)){
                        reset();
                        txtName.requestFocus();
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","user data added.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "adding Error","data not added...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This nic is the same as as another user's nic,user id "+existsUserId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    @FXML
    void updateOnAction(ActionEvent event) {
        validateAll();
        if (!isValidName)
            txtName.requestFocus();
        else if (!isValidPassword)
            (isPasswordHide?psPassword:txtPassword).requestFocus();
        else if (!isValidNic)
            txtNic.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (!isValidEmail)
            txtEmail.requestFocus();
        else if (cmbRank.getSelectionModel().isEmpty()) {
            cmbRank.setStyle("-fx-border-color: #ff0000");
            cmbRank.requestFocus();
        }else{
            String userId=txtId.getText();
            String name=txtName.getText();
            String password=(isPasswordHide?psPassword:txtPassword).getText();
            String nic=txtNic.getText();
            String telNumber=txtTelNumber.getText();
            String email=txtEmail.getText();
            String rank=cmbRank.getValue();
            try {
                String existsUserId= UserModel.existsUser(nic);
                if (existsUserId==null || existsUserId.equals(userId)){
                    user = new User(userId,name,password,nic,telNumber,email,rank);
                    if (UserModel.updateUser(user)){
                        reset();
                        txtName.requestFocus();
                        isUpdate=false;
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","user data updated.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "update Error","data not updated...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This nic is the same as as another user's nic,user id "+existsUserId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    @FXML
    void resetOnAction(ActionEvent event) {
        reset();
    }

    @FXML
    void nameOnAction(ActionEvent event) {
        if (isValidName)
            (isPasswordHide?psPassword:txtPassword).requestFocus();
    }

    @FXML
    void passwordOnAction(ActionEvent event) {
        if (isValidPassword)
            txtNic.requestFocus();
    }

    @FXML
    void nicOnAction(ActionEvent event) {
        if (isValidNic)
            txtTelNumber.requestFocus();
    }

    @FXML
    void telNumberOnAction(ActionEvent event) {
        if (isValidTelephoneNumber)
            txtEmail.requestFocus();
    }

    @FXML
    void emailOnAction(ActionEvent event) {
        if (isValidEmail)
            cmbRank.requestFocus();
    }

    @FXML
    void cmbRankOnAction(ActionEvent event) {
        cmbRank.setStyle("-fx-border-color: #76ff03");
    }

    @FXML
    void nameKeyPressedOnAction(KeyEvent event) {
        nameValidation();
    }

    @FXML
    void passwordKeyPressedOnAction(KeyEvent event) {
        passwordValidation();
    }

    @FXML
    void nicKeyPressedOnAction(KeyEvent event) {
        nicValidation();
    }

    @FXML
    void telNumKeyPressedOnAction(KeyEvent event) {
        telephoneNumberValidation();
    }

    @FXML
    void emailKeyPressedOnAction(KeyEvent event) {
        emailValidation();
    }

    @FXML
    public void showHidePasswordOnAction(ActionEvent actionEvent) {
        if (isPasswordHide){
            isPasswordHide=false;
            txtPassword.setText(psPassword.getText());
            fxIconEye.setStyle("-glyph-name:EYE;");
            psPassword.setVisible(false);
            txtPassword.setVisible(true);
        }else {
            isPasswordHide=true;
            psPassword.setText(txtPassword.getText());
            fxIconEye.setStyle("-glyph-name:EYE_SLASH;");
            txtPassword.setVisible(false);
            psPassword.setVisible(true);
        }
    }
    private void nameValidation(){
        try {
            boolean isExitsUserName=UserModel.searchExistsUserName(txtId.getText(),txtName.getText());
            if (Project.isValidName(txtName.getText()) && !isExitsUserName){
                txtName.setStyle("-fx-border-color: #76ff03");
                isValidName=true;
            }else {
                txtName.setStyle("-fx-border-color: #ff0000");
                isValidName=false;
            }
        } catch (SQLException | ClassNotFoundException e) {

        }

    }
    private void passwordValidation(){
        String password=(isPasswordHide?psPassword:txtPassword).getText();
        if (password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")){
            (isPasswordHide?psPassword:txtPassword).setStyle("-fx-border-color: #76ff03");
            isValidPassword=true;
        }else {
            (isPasswordHide?psPassword:txtPassword).setStyle("-fx-border-color: #ff0000");
            isValidPassword=false;
        }
    }
    private void nicValidation(){
        if (Project.isValidNic(txtNic.getText())){
            txtNic.setStyle("-fx-border-color: #76ff03");
            isValidNic=true;
        }else {
            txtNic.setStyle("-fx-border-color: #ff0000");
            isValidNic=false;
        }
    }
    private void telephoneNumberValidation() {
        if (Project.isValidTelephoneNumber(txtTelNumber.getText())){
            txtTelNumber.setStyle("-fx-border-color: #76ff03");
            isValidTelephoneNumber=true;
        }else {
            txtTelNumber.setStyle("-fx-border-color: #ff0000");
            isValidTelephoneNumber=false;
        }
    }

    private void emailValidation(){
        if (Project.isValidEmail(txtEmail.getText())){
            txtEmail.setStyle("-fx-border-color: #76ff03");
            isValidEmail=true;
        }else {
            txtEmail.setStyle("-fx-border-color: #ff0000");
            isValidEmail=false;
        }
    }

    private void validateAll() {
        nameValidation();
        passwordValidation();
        nicValidation();
        telephoneNumberValidation();
        emailValidation();
    }

}
