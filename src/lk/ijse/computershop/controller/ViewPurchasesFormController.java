package lk.ijse.computershop.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lk.ijse.computershop.model.*;
import lk.ijse.computershop.tm.ItemTm;
import lk.ijse.computershop.tm.TransactionTm;
import lk.ijse.computershop.tm.RepairTm;
import lk.ijse.computershop.to.ItemTransactionDetails;
import lk.ijse.computershop.to.RepairTransactionDetails;
import lk.ijse.computershop.to.Transaction;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class ViewPurchasesFormController {

    @FXML
    private AnchorPane ancViewPurchase;

    @FXML
    private TableColumn<TransactionTm, String> colAction;

    @FXML
    private TableColumn<TransactionTm, String> colCusId;

    @FXML
    private TableColumn<TransactionTm, String> colCusName;

    @FXML
    private TableColumn<TransactionTm, String> colDateTime;

    @FXML
    private TableColumn<ItemTm, String> colItemCode;

    @FXML
    private TableColumn<ItemTm, String> colItemDiscription;

    @FXML
    private TableColumn<ItemTm, Integer> colItemQty;

    @FXML
    private TableColumn<ItemTm, Double> colItemTotal;

    @FXML
    private TableColumn<RepairTm, String> colRepairDescription;

    @FXML
    private TableColumn<RepairTm, String> colRepairId;

    @FXML
    private TableColumn<RepairTm, Double> colRepairTotal;

    @FXML
    private TableColumn<TransactionTm, String> colTelNumber;

    @FXML
    private TableColumn<TransactionTm, String> colTransactionId;

    @FXML
    private TableColumn<TransactionTm, Double> colTransactionTotal;

    @FXML
    private TableColumn<TransactionTm, String> colType;

    @FXML
    private TableColumn<ItemTm, Double> colUnitPrice;

    @FXML
    private Label lblTransactionId;

    @FXML
    private TableView<ItemTm> tblItem;

    @FXML
    private TableView<RepairTm> tblRepair;

    @FXML
    private TableView<TransactionTm> tblTransaction;

    public void initialize(){
        setCellTableFactory();
        loadTransactionTable();
    }

    private void loadTransactionTable() {
        try {
            ObservableList<TransactionTm> suppliesObservableList= FXCollections.observableArrayList();
            ArrayList<Transaction>transactionArrayList= TransactionModel.getAllTransaction();
            for (Transaction transaction:transactionArrayList){
                String cusId=transaction.getCustomerId();
                String tranId=transaction.getTransactionId();
                String dateTime=transaction.getDateTime();
                String type=transaction.getType();
                String cusName= CustomerModel.searchCustomerName(cusId);
                String telNumber=CustomerModel.searchTelNumber(cusId);
                double total=transaction.getTotal();
                suppliesObservableList.add(new TransactionTm(cusId,tranId,dateTime,type,cusName,telNumber,total));
            }
            tblTransaction.setItems(suppliesObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void setCellTableFactory() {
        //item table
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItemDiscription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        //repair table
        colRepairId.setCellValueFactory(new PropertyValueFactory<>("repairId"));
        colRepairDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRepairTotal.setCellValueFactory(new PropertyValueFactory<>("repairPrice"));

        //transaction table
        colCusId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colTransactionId.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dataTime"));
        colType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        colCusName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colTelNumber.setCellValueFactory(new PropertyValueFactory<>("telNumber"));
        colTransactionTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        Callback<TableColumn<TransactionTm, String>, TableCell<TransactionTm, String>> cellFactory = (TableColumn<TransactionTm, String> param) -> {
            // make cell containing buttons
            final TableCell<TransactionTm, String> cell = new TableCell<TransactionTm, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView show = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                        show.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#f1c40f;");
                        show.setOnMouseClicked((MouseEvent event) -> {
                            TransactionTm transactionTm =tblTransaction.getSelectionModel().getSelectedItem();
                            lblTransactionId.setText(transactionTm.getTransactionId());
                            refreshTable(transactionTm.getTransactionId());
                        });
                        HBox hBox = new HBox(show);
                        hBox.setStyle("-fx-alignment:center");
                        HBox.setMargin(show, new Insets(2, 0, 2, 0));
                        setGraphic(hBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        colAction.setCellFactory(cellFactory);

    }

    private void refreshTable(String transactionId) {
        try {
            ArrayList<ItemTransactionDetails> itemDetails = ItemTransactionDetailsModel.search(transactionId);
            ArrayList<RepairTransactionDetails> repairDetails = RepairTransactionDetailsModel.search(transactionId);
            ObservableList<ItemTm> itemTmObservableList = FXCollections.observableArrayList();
            ObservableList<RepairTm> repairTmObservableList = FXCollections.observableArrayList();
            for (ItemTransactionDetails details : itemDetails) {
                String itemCode = details.getItemCode();
                String description = ItemModel.searchItemCode(itemCode);
                int qty = details.getQuantity();
                double unitPrice = details.getUnitPrice();
                double total = qty * unitPrice;
                itemTmObservableList.add(new ItemTm(
                    itemCode,description,qty,unitPrice,total
                ));
            }
            for (RepairTransactionDetails details : repairDetails) {
                String repairId=details.getRepairId();
                String description=RepairModel.searchRepairId(repairId);
                double price=details.getRepairPrice();
                repairTmObservableList.add(new RepairTm(repairId,description,price));
            }
            tblItem.setItems(itemTmObservableList);
            tblRepair.setItems(repairTmObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }
}
