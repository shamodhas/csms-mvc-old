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
import lk.ijse.computershop.model.CustomerModel;
import lk.ijse.computershop.to.Customer;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerFormController {

    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private TableColumn<Customer, String> colAction;

    @FXML
    private TableColumn<Customer, String> colAddress;

    @FXML
    private TableColumn<Customer, String> colCustomerId;

    @FXML
    private TableColumn<Customer, String> colEmail;

    @FXML
    private TableColumn<Customer, String> colName;

    @FXML
    private TableColumn<Customer, String> colNic;

    @FXML
    private TableColumn<Customer, String> colTelNumber;

    @FXML
    private TableView<Customer> tblCustomer;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtCusId;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtNic;

    @FXML
    private TextField txtTelNumber;
    ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
    Customer customer=null;
    boolean isValidName=false;
    boolean isValidAddress=false;
    boolean isValidNic=false;
    boolean isValidTelephoneNumber=false;
    boolean isValidEmail=false;
    boolean isUpdate=false;
    public void initialize(){
        btnUpdate.setDisable(true);
        loadTable();
        resetOnAction(new ActionEvent());
    }
    @FXML
    public void txtSearchMouseOnClick(MouseEvent mouseEvent) {
        txtSearch.clear();
        FilteredList<Customer>filteredList=new FilteredList<>(customerObservableList,b -> true);
        txtSearch.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(Customer ->{
                if (newValue.isEmpty() || newValue==null ){
                    return true;
                }
                String searchKeyword=newValue.toLowerCase();
                if (Customer.getCustomerId().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Customer.getName().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Customer.getAddress().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Customer.getNic().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Customer.getTelephoneNumber().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Customer.getEmail().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else {
                    return false;
                }
            });
        });
        SortedList<Customer>sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tblCustomer.comparatorProperty());
        tblCustomer.setItems(sortedList);
    }

    @FXML
    void resetOnAction(ActionEvent event) {
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        clearAllTextField();
    }
    @FXML
    public void addOnAction(ActionEvent actionEvent) {
        validateAll();
        if (!isValidName)
            txtName.requestFocus();
        else if (!isValidAddress)
            txtAddress.requestFocus();
        else if (!isValidNic)
            txtNic.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (!isValidEmail)
            txtEmail.requestFocus();
        else {
            String cusId=txtCusId.getText();
            String name=txtName.getText();
            String address=txtAddress.getText();
            String nic=txtNic.getText();
            String telNumber=txtTelNumber.getText();
            String email=txtEmail.getText();
            try {
                String existsCusId=CustomerModel.existsCustomer(nic);
                if (existsCusId==null){
                    customer = new Customer(cusId,name,address,nic,telNumber,email);
                    if (CustomerModel.addCustomer(customer)){
                        clearAllTextField();
                        txtName.requestFocus();
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","customer data added.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "adding Error","data not added...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This nic is the same as as another customer's nic,customer id "+existsCusId);
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
        else if (!isValidAddress)
            txtAddress.requestFocus();
        else if (!isValidNic)
            txtNic.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (!isValidEmail)
            txtEmail.requestFocus();
        else {
            String cusId=txtCusId.getText();
            String name=txtName.getText();
            String address=txtAddress.getText();
            String nic=txtNic.getText();
            String telNumber=txtTelNumber.getText();
            String email=txtEmail.getText();
            try {
                String existsCusId=CustomerModel.existsCustomer(nic);
                if (existsCusId==null || existsCusId.equals(cusId)){
                    customer = new Customer(cusId,name,address,nic,telNumber,email);
                    if (CustomerModel.updateCustomer(customer)){
                        btnUpdate.setDisable(true);
                        btnAdd.setDisable(false);
                        clearAllTextField();
                        txtName.requestFocus();
                        isUpdate=false;
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","customer data updated.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "update Error","data not updated...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This nic is the same as as another customer's nic,customer id "+existsCusId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }

    }

    @FXML
    void telNumberOnAction(ActionEvent event) {
        if (isValidTelephoneNumber)
            txtEmail.requestFocus();
    }

    @FXML
    public void nameOnAction(ActionEvent actionEvent) {
        if (isValidName)
            txtAddress.requestFocus();
    }

    @FXML
    public void addressOnAction(ActionEvent actionEvent) {
        if (isValidAddress)
            txtNic.requestFocus();
    }

    @FXML
    public void nicOnAction(ActionEvent actionEvent) {
        if (isValidNic)
            txtTelNumber.requestFocus();
    }
    @FXML
    public void emailOnAction(ActionEvent actionEvent) {
        if (isValidEmail) {
            if (isUpdate){
                updateOnAction(actionEvent);
            }else {
                addOnAction(actionEvent);
            }
        }
    }
    @FXML
    void telNumKeyPressedOnAction(KeyEvent event) {
        telephoneNumberValidation();
    }
    @FXML
    public void nameKeyPressedOnAction(KeyEvent keyEvent) {
        nameValidation();
    }
    @FXML
    public void addressKeyPressedOnAction(KeyEvent keyEvent) {
        addressValidation();
    }
    @FXML
    public void nicKeyPressedOnAction(KeyEvent keyEvent) {
        nicValidation();
    }
    @FXML
    public void emailKeyPressedOnAction(KeyEvent keyEvent) {
        emailValidation();
    }

    private void refreshTable() {
        customerObservableList.clear();
        try {
            ArrayList<Customer>customerArrayList= CustomerModel.getAllCustomer();
            for (Customer customer:customerArrayList){
                customerObservableList.add(customer);
            }
            tblCustomer.setItems(customerObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void loadTable() {
        refreshTable();
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colTelNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        Callback<TableColumn<Customer, String>, TableCell<Customer, String>> cellFactory = (TableColumn<Customer, String> param) -> {
            // make cell containing buttons
            final TableCell<Customer, String> cell = new TableCell<Customer, String>() {
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
                            customer=tblCustomer.getSelectionModel().getSelectedItem();
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Deleting a customer named "+customer.getName(),"When the customer is deleted,The order is also deleted\nAre You Sure ? ");
                            if (result==ButtonType.OK){
                                try {
                                    if (CustomerModel.deleteCustomer(customer.getCustomerId())){
                                        clearAllTextField();
                                        refreshTable();
                                        Project.showError(Alert.AlertType.INFORMATION, "Deleting Successfully",customer.getName()+" was deleted");
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
                            customer=tblCustomer.getSelectionModel().getSelectedItem();
                            txtCusId.setText(customer.getCustomerId());
                            txtName.setText(customer.getName());
                            txtAddress.setText(customer.getAddress());
                            txtNic.setText(customer.getNic());
                            txtTelNumber.setText(customer.getTelephoneNumber());
                            txtEmail.setText(customer.getEmail());
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
        tblCustomer.setItems(customerObservableList);
    }
    private void clearAllTextField() {
        try {
            txtCusId.setText(CustomerModel.getNextCustomerId());
            txtName.setText("");
            txtAddress.setText("");
            txtNic.setText("");
            txtTelNumber.setText("");
            txtEmail.setText("");
            refreshTable();
        } catch (SQLException | ClassNotFoundException e) {
            Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
        }
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
        addressValidation();
        nicValidation();
        telephoneNumberValidation();
        emailValidation();
    }
}
