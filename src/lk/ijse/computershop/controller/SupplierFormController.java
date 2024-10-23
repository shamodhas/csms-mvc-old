package lk.ijse.computershop.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import lk.ijse.computershop.model.SupplierModel;
import lk.ijse.computershop.to.Supplier;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class SupplierFormController {

    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private TableColumn<Supplier, String> colAction;

    @FXML
    private TableColumn<Supplier, String> colAddress;

    @FXML
    private TableColumn<Supplier, String> colEmail;

    @FXML
    private TableColumn<Supplier, String> colId;

    @FXML
    private TableColumn<Supplier, String> colName;

    @FXML
    private TableColumn<Supplier, String> colTelNumber;

    @FXML
    private TableView<Supplier> tbl;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtTelNumber;
    ObservableList<Supplier> supplierObservableList = FXCollections.observableArrayList();
    Supplier supplier=null;
    boolean isValidName=false;
    boolean isValidAddress=false;
    boolean isValidTelephoneNumber=false;
    boolean isValidEmail=false;
    boolean isUpdate=false;
    public void initialize(){
        loadTable();
        reset();
    }

    private void loadTable(){
        refreshTable();
        colId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colTelNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        Callback<TableColumn<Supplier, String>, TableCell<Supplier, String>> cellFactory = (TableColumn<Supplier, String> param) -> {
            // make cell containing buttons
            final TableCell<Supplier, String> cell = new TableCell<Supplier, String>() {
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
                        deleteIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#ff1744;");
                        editIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#00E676;");
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            supplier=tbl.getSelectionModel().getSelectedItem();
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Deleting a supplier named "+supplier.getName(),"When the supplier is deleted,The supplies is also deleted\nAre You Sure ? ");
                            if (result==ButtonType.OK){
                                try {
                                    if (SupplierModel.deleteSupplier(supplier.getSupplierId())){
                                        reset();
                                        refreshTable();
                                        Project.showError(Alert.AlertType.INFORMATION, "Deleting Successfully",supplier.getName()+" was deleted");
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
                            supplier=tbl.getSelectionModel().getSelectedItem();
                            txtId.setText(supplier.getSupplierId());
                            txtName.setText(supplier.getName());
                            txtAddress.setText(supplier.getAddress());
                            txtTelNumber.setText(supplier.getTelephoneNumber());
                            txtEmail.setText(supplier.getEmail());

                            btnUpdate.setDisable(false);
                            btnAdd.setDisable(true);
                            isUpdate=true;
                        });
                        HBox hBox = new HBox(editIcon, deleteIcon);
                        hBox.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));
                        HBox.setMargin(editIcon, new Insets(2, 3, 0, 2));
                        setGraphic(hBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        colAction.setCellFactory(cellFactory);
        tbl.setItems(supplierObservableList);
    }

    private void refreshTable() {
        supplierObservableList.clear();
        try {
            ArrayList<Supplier>allSupplier=SupplierModel.getAllSupplier();
            for (Supplier supplier1:allSupplier){
                supplierObservableList.add(supplier1);
            }
            tbl.setItems(supplierObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }

    }

    private void reset() {
        try {
            txtId.setText(SupplierModel.getNextSupplierId());
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
        txtName.clear();
        txtAddress.clear();
        txtTelNumber.clear();
        txtEmail.clear();
        btnUpdate.setDisable(true);
        btnAdd.setDisable(false);
    }

    @FXML
    void addOnAction(ActionEvent event) {
        validateAll();
        if (!isValidName)
            txtName.requestFocus();
        else if (!isValidAddress)
            txtAddress.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (!isValidEmail)
            txtEmail.requestFocus();
        else {
            String supplierId=txtId.getText();
            String name=txtName.getText();
            String address=txtAddress.getText();
            String telNumber=txtTelNumber.getText();
            String email=txtEmail.getText();
            try {
                String existsSupplierId=SupplierModel.searchName(name);
                if (existsSupplierId==null){
                    supplier = new Supplier(supplierId,name,address,telNumber,email);
                    if (SupplierModel.addSupplier(supplier)){
                        reset();
                        refreshTable();
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","supplier data added.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "adding Error","data not added...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This name is the same as as another supplier's name,supplier id "+existsSupplierId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    private void validateAll() {
        nameValidation();
        addressValidation();
        telephoneNumberValidation();
        emailValidation();
    }
    private void nameValidation(){
        if (Project.isValidName(txtName.getText())){
            txtName.setStyle("-fx-border-color: #76ff03");
            isValidName=true;
        }else {
            txtName.setStyle("-fx-border-color: #ff0000");
            isValidName=false;
        }
    }
    private void addressValidation(){
        if (!txtAddress.equals("")){
            txtAddress.setStyle("-fx-border-color: #76ff03");
            isValidAddress=true;
        }else {
            txtAddress.setStyle("-fx-border-color: #ff0000");
            isValidAddress=false;
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

    @FXML
    void addressKeyPressedOnAction(KeyEvent event) {
        addressValidation();
    }

    @FXML
    void addressOnAction(ActionEvent event) {
        if (isValidAddress)
            txtTelNumber.requestFocus();
    }

    @FXML
    void emailKeyPressedOnAction(KeyEvent event) {
        emailValidation();
    }

    @FXML
    void emailOnAction(ActionEvent event) {
        if (isValidEmail) {
            if (isUpdate){
                updateOnAction(event);
            }else {
                addOnAction(event);
            }
        }
    }

    @FXML
    void nameKeyPressedOnAction(KeyEvent event) {
        nameValidation();
    }

    @FXML
    void nameOnAction(ActionEvent event) {
        if (isValidName)
            txtAddress.requestFocus();
    }

    @FXML
    void resetOnAction(ActionEvent event) {
        reset();
    }

    @FXML
    void telNumKeyPressedOnAction(KeyEvent event) {
        telephoneNumberValidation();
    }

    @FXML
    void telNumberOnAction(ActionEvent event) {
        if (isValidTelephoneNumber)
            txtEmail.requestFocus();
    }

    @FXML
    void txtSearchMouseOnClick(MouseEvent event) {
        txtSearch.clear();
        FilteredList<Supplier> filteredList=new FilteredList<>(supplierObservableList, b -> true);
        txtSearch.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(supplierSearchModel ->{
                if (newValue.isEmpty() || newValue==null ){
                    return true;
                }
                String searchKeyword=newValue.toLowerCase();
                if (supplierSearchModel.getSupplierId().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (supplierSearchModel.getName().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (supplierSearchModel.getAddress().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (supplierSearchModel.getTelephoneNumber().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (supplierSearchModel.getEmail().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else {
                    return false;
                }
            });
        });
        SortedList<Supplier> sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tbl.comparatorProperty());
        tbl.setItems(sortedList);
    }

    @FXML
    void updateOnAction(ActionEvent event) {
        validateAll();
        if (!isValidName)
            txtName.requestFocus();
        else if (!isValidAddress)
            txtAddress.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (!isValidEmail)
            txtEmail.requestFocus();
        else {
            String supplierId=txtId.getText();
            String name=txtName.getText();
            String address=txtAddress.getText();
            String telNumber=txtTelNumber.getText();
            String email=txtEmail.getText();
            try {
                String existsSupplierId=SupplierModel.searchName(name);
                if (existsSupplierId==null || existsSupplierId.equals(supplierId)){
                    supplier = new Supplier(supplierId,name,address,telNumber,email);
                    if (SupplierModel.updateSupplier(supplier)){
                        btnUpdate.setDisable(true);
                        btnAdd.setDisable(false);
                        reset();
                        refreshTable();
                        isUpdate=false;
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","supplier data updated.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "update Error","data not updated...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This name is the same as as another supplier's name,supplier id "+existsSupplierId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }
}
