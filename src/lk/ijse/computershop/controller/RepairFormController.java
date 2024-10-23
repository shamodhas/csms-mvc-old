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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lk.ijse.computershop.model.*;
import lk.ijse.computershop.tm.ReturnRepairTm;
import lk.ijse.computershop.to.*;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RepairFormController {

    @FXML
    public HBox hbState;
    @FXML
    private AnchorPane ancRepair;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private ComboBox<String> cmbCustomerId;

    @FXML
    private ComboBox<String> cmbCustomerName;

    @FXML
    private ComboBox<String> cmbState;

    @FXML
    private TableColumn<Repair, String> colAction;

    @FXML
    private TableColumn<Repair, String> colCustomerId;

    @FXML
    private TableColumn<Repair, String> colDescription;

    @FXML
    private TableColumn<Repair, LocalDate> colReceiveDate;

    @FXML
    private TableColumn<Repair, String> colRepairId;

    @FXML
    private TableColumn<Repair, LocalDate> colReturnDate;

    @FXML
    private TableColumn<Repair, String> colState;
    @FXML
    private TableColumn<ReturnRepairTm, String> colReturnedCustomerId;

    @FXML
    private TableColumn<ReturnRepairTm, String> colReturnedDescription;

    @FXML
    private TableColumn<ReturnRepairTm, Double> colReturnedPrice;

    @FXML
    private TableColumn<ReturnRepairTm, LocalDate> colReturnedReceiveDate;

    @FXML
    private TableColumn<ReturnRepairTm, String> colReturnedRepairId;

    @FXML
    private TableColumn<ReturnRepairTm, String> colReturnedTransactionId;


    @FXML
    private TableColumn<ReturnRepairTm, LocalDate> colTrueReturnDate;

    @FXML
    private DatePicker dpReceiveDate;

    @FXML
    private DatePicker dpReturnDate;

    @FXML
    private TableView<Repair> tblRepair;

    @FXML
    private TableView<ReturnRepairTm> tblReturnedRepair;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtRepairId;
    ObservableList<Repair> repairObservableList= FXCollections.observableArrayList();
    ObservableList<ReturnRepairTm> returnRepairTmObservableList= FXCollections.observableArrayList();
    Repair repair=null;
    public void initialize(){
        try {
            loadTable();
            loadCmb();
        } catch (SQLException | ClassNotFoundException e) {

        }
        reset();
    }

    private void refreshTable() throws SQLException, ClassNotFoundException {
        repairObservableList.clear();
        ArrayList<Repair>nonReturnRepairList=RepairModel.getAllNonReturnRepair();
        for (Repair repair:nonReturnRepairList){
            repairObservableList.add(repair);
        }
        tblRepair.setItems(repairObservableList);
    }

    private void loadTable() throws SQLException, ClassNotFoundException {
        refreshTable();
        colRepairId.setCellValueFactory(new PropertyValueFactory<>("repairId"));
        colReceiveDate.setCellValueFactory(new PropertyValueFactory<>("receiveDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colState.setCellValueFactory(new PropertyValueFactory<>("state"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        //repair returned Table
        colReturnedRepairId.setCellValueFactory(new PropertyValueFactory<>("repairId"));
        colReturnedTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colReturnedDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colReturnedCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colReturnedPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colReturnedReceiveDate.setCellValueFactory(new PropertyValueFactory<>("receiveDate"));
        colTrueReturnDate.setCellValueFactory(new PropertyValueFactory<>("trueReturnDate"));
        ArrayList<Repair>returnedRepairArrayList=RepairModel.getAllReturnedRepair();
        for (Repair repair:returnedRepairArrayList){
            RepairTransactionDetails rtd= RepairTransactionDetailsModel.searchRepairId(repair.getRepairId());
            LocalDate trueReturnDate=TransactionModel.getDate(rtd.getTransactionId());
            returnRepairTmObservableList.add(new ReturnRepairTm(
                    repair.getRepairId(),
                    rtd.getTransactionId(),
                    repair.getDescription(),
                    repair.getCustomerId(),
                    rtd.getRepairPrice(),
                    repair.getReceiveDate(),
                    trueReturnDate
            ));
        }
        tblReturnedRepair.setItems(returnRepairTmObservableList);
        //action column
        Callback<TableColumn<Repair, String>, TableCell<Repair, String>> cellFactory = (TableColumn<Repair, String> param) -> {
            // make cell containing buttons
            final TableCell<Repair, String> cell = new TableCell<Repair, String>() {
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
                            repair=tblRepair.getSelectionModel().getSelectedItem();
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Deleting confirmation","Are You Sure ? ");
                            if (result==ButtonType.OK){
                                try {
                                    if (RepairModel.deleteRepair(repair.getRepairId())){
                                        reset();
                                        refreshTable();
                                        Project.showError(Alert.AlertType.INFORMATION, "Deleting Successfully",repair.getRepairId()+" was deleted");
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
                            repair=tblRepair.getSelectionModel().getSelectedItem();
                            txtRepairId.setText(repair.getRepairId());
                            cmbCustomerId.getSelectionModel().select(repair.getCustomerId());
                            cmbCustomerIdOnAction(new ActionEvent());
                            dpReceiveDate.setValue(repair.getReceiveDate());
                            dpReturnDate.setValue(repair.getReturnDate());
                            txtDescription.setText(repair.getDescription());
                            cmbState.getSelectionModel().select(repair.getState());
                            btnUpdate.setVisible(true);
                            btnAdd.setVisible(false);
                            hbState.setVisible(true);
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
        tblRepair.setItems(repairObservableList);
    }

    private void loadCmb() throws SQLException, ClassNotFoundException {
        String[] state={"repairing","repaired"};
        cmbState.getItems().addAll(state);
        ArrayList<String>customerIdList= CustomerModel.getAllCustomerId();
        cmbCustomerId.getItems().addAll(customerIdList);
        ArrayList<String>customerNameList= CustomerModel.getAllCustomerName();
        cmbCustomerName.getItems().addAll(customerNameList);
    }

    @FXML
    void addOnAction(ActionEvent event) {
        if (cmbCustomerId.getSelectionModel().isEmpty()) {
            cmbCustomerId.setStyle("-fx-border-color: #ff0000");
            cmbCustomerName.setStyle("-fx-border-color: #ff0000");
        } else if (dpReceiveDate.getValue()==null){
            dpReceiveDate.setStyle("-fx-border-color: #ff0000");
        } else if (dpReturnDate.getValue()==null) {
            dpReturnDate.setStyle("-fx-border-color: #ff0000");
        } else if (txtDescription.getText()==null) {
            txtDescription.setText("-fx-border-color: #ff0000");
        } else {
            String repairId=txtRepairId.getText();
            LocalDate receiveDate=dpReceiveDate.getValue();
            LocalDate returnDate=dpReturnDate.getValue();
            String state="repairing";
            String description=txtDescription.getText();
            String customerId=cmbCustomerId.getSelectionModel().getSelectedItem();
            try {
                repair = new Repair(repairId,receiveDate,returnDate,state,description,customerId);
                if (RepairModel.addRepair(repair)){
                    reset();
                    refreshTable();
                    Project.showError(Alert.AlertType.INFORMATION, "successfully","Repair data added.");
                }else {
                    Project.showError(Alert.AlertType.ERROR, "adding Error","data not added...!");
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    @FXML
    void cmbCustomerIdOnAction(ActionEvent event) {
        String name= null;
        try {
            name = CustomerModel.searchCustomerName(cmbCustomerId.getSelectionModel().getSelectedItem());
        } catch (SQLException | ClassNotFoundException e) {

        }
        if (name!=null)
            cmbCustomerName.getSelectionModel().select(name);
        cmbCustomerId.setStyle("-fx-border-color: #76ff03");
        cmbCustomerName.setStyle("-fx-border-color: #76ff03");
    }

    @FXML
    void cmbCustomerNameOnAction(ActionEvent event) {
        String customerId= null;
        try {
            customerId = CustomerModel.searchCustomerId(cmbCustomerName.getSelectionModel().getSelectedItem());
        } catch (SQLException | ClassNotFoundException e) {

        }
        if (customerId!=null)
            cmbCustomerId.getSelectionModel().select(customerId);
        cmbCustomerId.setStyle("-fx-border-color: #76ff03");
        cmbCustomerName.setStyle("-fx-border-color: #76ff03");
    }

    @FXML
    public void checkCustomerIsSelect(MouseEvent mouseEvent) {
        if (cmbCustomerId.getSelectionModel().isEmpty()){
            cmbCustomerId.setStyle("-fx-border-color: #ff0000");
            cmbCustomerName.setStyle("-fx-border-color: #ff0000");
        }
        if (dpReceiveDate.getValue() != null){
            dpReceiveDate.setStyle("-fx-border-color: #76ff03");
        }
        if (dpReturnDate.getValue() != null){
            dpReturnDate.setStyle("-fx-border-color: #76ff03");
        }
        if (txtDescription.getText() != null){
            txtDescription.setStyle("-fx-border-color: #76ff03");
        }
        if (!cmbState.getSelectionModel().isEmpty()){
            cmbState.setStyle("-fx-border-color: #76ff03");
        }
    }

    @FXML
    void resetOnAction(ActionEvent event) {
        reset();
    }

    @FXML
    void updateOnAction(ActionEvent event) {
        if (cmbCustomerId.getSelectionModel().isEmpty()) {
            cmbCustomerId.setStyle("-fx-border-color: #ff0000");
            cmbCustomerName.setStyle("-fx-border-color: #ff0000");
        } else if (dpReceiveDate.getValue()==null){
            dpReceiveDate.setStyle("-fx-border-color: #ff0000");
        } else if (dpReturnDate.getValue()==null) {
            dpReturnDate.setStyle("-fx-border-color: #ff0000");
        } else if (txtDescription.getText()==null) {
            txtDescription.setText("-fx-border-color: #ff0000");
        } else if (cmbState.getSelectionModel().isEmpty()) {
            cmbState.setStyle("-fx-border-color: #ff0000");
        }else {
            String repairId=txtRepairId.getText();
            LocalDate receiveDate=dpReceiveDate.getValue();
            LocalDate returnDate=dpReturnDate.getValue();
            String state=cmbState.getSelectionModel().getSelectedItem();
            String description=txtDescription.getText();
            String customerId=cmbCustomerId.getSelectionModel().getSelectedItem();
            try {
                repair = new Repair(repairId,receiveDate,returnDate,state,description,customerId);
                if (RepairModel.updateRepair(repair)){
                    reset();
                    refreshTable();
                    Project.showError(Alert.AlertType.INFORMATION, "successfully","Repair data updated.");
                }else {
                    Project.showError(Alert.AlertType.ERROR, "update Error","data not updated...!");
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }
    private void reset(){
        try {
            txtRepairId.setText(RepairModel.getNextRepairId());
        } catch (SQLException | ClassNotFoundException e) {

        }
        btnUpdate.setVisible(false);
        btnAdd.setVisible(true);
        hbState.setVisible(false);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbCustomerName.getSelectionModel().clearSelection();
        dpReceiveDate.setValue(null);
        dpReturnDate.setValue(null);
        txtDescription.clear();
        cmbState.getSelectionModel().clearSelection();
    }

    @FXML
    public void dpReceiveDateOnAction(ActionEvent actionEvent) {
        dpReturnDate.setDayCellFactory(d -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (dpReceiveDate.getValue()!=null) {
                    setDisable(item.isBefore(dpReceiveDate.getValue()));
                }
            }
        });
    }
    @FXML
    public void dpReturnDateOnAction(ActionEvent actionEvent) {
        dpReceiveDate.setDayCellFactory(d -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (dpReturnDate.getValue()!=null) {
                    setDisable(item.isAfter(dpReturnDate.getValue()));
                }
            }
        });
    }
}
