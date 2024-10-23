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
import lk.ijse.computershop.model.EmployeeModel;
import lk.ijse.computershop.to.Employee;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class EmployeeFormController {

    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private ComboBox<String> cmbRank;

    @FXML
    private TableColumn<Employee, String> colAction;

    @FXML
    private TableColumn<Employee, String> colAddress;

    @FXML
    private TableColumn<Employee, String> colEmployeeId;

    @FXML
    private TableColumn<Employee, String> colName;

    @FXML
    private TableColumn<Employee, String> colNic;

    @FXML
    private TableColumn<Employee, String> colRank;

    @FXML
    private TableColumn<Employee, String> colTelNumber;

    @FXML
    private TableView<Employee> tblEmployee;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtEmployeeId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtNic;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtTelNumber;

    ObservableList<Employee> employeeObservableList = FXCollections.observableArrayList();
    Employee employee=null;
    boolean isValidName=false;
    boolean isValidAddress=false;
    boolean isValidNic=false;
    boolean isValidTelephoneNumber=false;
    boolean isUpdate=false;
    public void initialize(){
        btnUpdate.setDisable(true);
        loadCmb();
        loadTable();
        resetOnAction(new ActionEvent());
    }

    private void loadCmb() {
        String[] ranks={"Manager","Repairer","Technical Supporters","Cashier","Minor Employees"};
        cmbRank.getItems().addAll(ranks);
    }

    private void refreshTable() {
        employeeObservableList.clear();
        try {
            ArrayList<Employee> employeeArrayList= EmployeeModel.getAllEmployee();
            for (Employee employee:employeeArrayList){
                employeeObservableList.add(employee);
            }
            tblEmployee.setItems(employeeObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void loadTable() {
        refreshTable();
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colTelNumber.setCellValueFactory(new PropertyValueFactory<>("telephoneNumber"));
        colRank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        Callback<TableColumn<Employee, String>, TableCell<Employee, String>> cellFactory = (TableColumn<Employee, String> param) -> {
            // make cell containing buttons
            final TableCell<Employee, String> cell = new TableCell<Employee, String>() {
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
                            employee=tblEmployee.getSelectionModel().getSelectedItem();
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Deleting a employee named "+employee.getName(),"Are You Sure ? ");
                            if (result==ButtonType.OK){
                                try {
                                    if (EmployeeModel.deleteEmployee(employee.getEmployeeId())){
                                        clearAll();
                                        refreshTable();
                                        Project.showError(Alert.AlertType.INFORMATION, "Deleting Successfully",employee.getName()+" was deleted");
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
                            employee=tblEmployee.getSelectionModel().getSelectedItem();
                            txtEmployeeId.setText(employee.getEmployeeId());
                            txtName.setText(employee.getName());
                            txtAddress.setText(employee.getAddress());
                            txtNic.setText(employee.getNic());
                            txtTelNumber.setText(employee.getTelephoneNumber());
                            cmbRank.getSelectionModel().select(employee.getRank());
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
        tblEmployee.setItems(employeeObservableList);
    }

    private void clearAll() {
        try {
            txtEmployeeId.setText(EmployeeModel.getNextEmployeeId());
            txtName.setText("");
            txtAddress.setText("");
            txtNic.setText("");
            txtTelNumber.setText("");
            cmbRank.getSelectionModel().clearSelection();
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


    @FXML
    void addOnAction(ActionEvent event) {
        validateAll();
        if (!isValidName)
            txtName.requestFocus();
        else if (!isValidAddress)
            txtAddress.requestFocus();
        else if (!isValidNic)
            txtNic.requestFocus();
        else if (!isValidTelephoneNumber)
            txtTelNumber.requestFocus();
        else if (cmbRank.getSelectionModel().isEmpty())
            cmbRank.setStyle("-fx-border-color: #ff0000");
        else {
            String employeeId=txtEmployeeId.getText();
            String name=txtName.getText();
            String address=txtAddress.getText();
            String nic=txtNic.getText();
            String telNumber=txtTelNumber.getText();
            String rank=cmbRank.getSelectionModel().getSelectedItem();
            try {
                String existsCusId=EmployeeModel.existsEmployee(nic);
                if (existsCusId==null){
                    employee = new Employee(employeeId,name,address,nic,telNumber,rank);
                    System.out.println(employee.toString());
                    if (EmployeeModel.addEmployee(employee)){
                        clearAll();
                        refreshTable();
                        txtName.requestFocus();
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","Employee data added.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "adding Error","data not added...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This nic is the same as as another employee's nic,customer id "+existsCusId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    @FXML
    void addressKeyPressedOnAction(KeyEvent event) {
        addressValidation();
    }

    @FXML
    void addressOnAction(ActionEvent event) {
        if (isValidAddress)
            txtNic.requestFocus();
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
    void nameOnAction(ActionEvent event) {
        if (isValidName)
            txtAddress.requestFocus();
    }

    @FXML
    void nicKeyPressedOnAction(KeyEvent event) {
        nicValidation();
    }

    @FXML
    void nicOnAction(ActionEvent event) {
        if (isValidNic)
            txtTelNumber.requestFocus();
    }

    @FXML
    void resetOnAction(ActionEvent event) {
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        clearAll();
    }

    @FXML
    void telNumKeyPressedOnAction(KeyEvent event) {
        telephoneNumberValidation();
    }

    @FXML
    void txtSearchMouseOnClick(MouseEvent event) {
        txtSearch.clear();
        FilteredList<Employee> filteredList=new FilteredList<>(employeeObservableList, b -> true);
        txtSearch.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(Employee ->{
                if (newValue.isEmpty() || newValue==null ){
                    return true;
                }
                String searchKeyword=newValue.toLowerCase();
                if (Employee.getEmployeeId().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Employee.getName().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Employee.getAddress().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Employee.getNic().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Employee.getTelephoneNumber().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Employee.getRank().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else {
                    return false;
                }
            });
        });
        SortedList<Employee> sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tblEmployee.comparatorProperty());
        tblEmployee.setItems(sortedList);
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
        else if (cmbRank.getSelectionModel().isEmpty())
            cmbRank.setStyle("-fx-border-color: #ff0000");
        else {
            String employeeId=txtEmployeeId.getText();
            String name=txtName.getText();
            String address=txtAddress.getText();
            String nic=txtNic.getText();
            String telNumber=txtTelNumber.getText();
            String rank=cmbRank.getSelectionModel().getSelectedItem();
            try {
                String existsEmployeeId=EmployeeModel.existsEmployee(nic);
                if (existsEmployeeId==null || existsEmployeeId.equals(employeeId)){
                    employee = new Employee(employeeId,name,address,nic,telNumber,rank);
                    if (EmployeeModel.updateEmployee(employee)){
                        btnUpdate.setDisable(true);
                        btnAdd.setDisable(false);
                        clearAll();
                        refreshTable();
                        txtName.requestFocus();
                        isUpdate=false;
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","Employee data updated.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "update Error","data not updated...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","This nic is the same as as another employee's nic,Employee id "+existsEmployeeId);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    private void validateAll() {
        nameValidation();
        addressValidation();
        nicValidation();
        telephoneNumberValidation();
    }

}
