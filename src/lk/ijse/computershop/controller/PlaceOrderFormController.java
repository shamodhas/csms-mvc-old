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
import lk.ijse.computershop.model.*;
import lk.ijse.computershop.tm.ItemTm;
import lk.ijse.computershop.tm.RepairTm;
import lk.ijse.computershop.to.*;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class PlaceOrderFormController {

    public TextField txtType;
    @FXML
    public JFXButton btnItemUpdate;
    @FXML
    public JFXButton btnRepairUpdate;
    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnItemAdd;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private JFXButton btnRepairAdd;

    @FXML
    private ComboBox<String> cmbCustomerId;

    @FXML
    private ComboBox<String> cmbItemCode;

    @FXML
    private ComboBox<String> cmbItemDescription;

    @FXML
    private ComboBox<String> cmbRepairId;

    @FXML
    private TableColumn<ItemTm, String> colItemAction;

    @FXML
    private TableColumn<RepairTm, String> colRepairActon;

    @FXML
    private TableColumn<ItemTm, String> colItemCode;

    @FXML
    private TableColumn<ItemTm, String> colItemDescription;

    @FXML
    private TableColumn<ItemTm, Integer> colItemQty;

    @FXML
    private TableColumn<ItemTm, Double> colItemTotal;

    @FXML
    private TableColumn<ItemTm, Double> colItemUnitPrice;

    @FXML
    private TableColumn<RepairTm, String> colRepairDescription;

    @FXML
    private TableColumn<RepairTm, String> colRepairId;

    @FXML
    private TableColumn<RepairTm, Double> colRepairPrice;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<ItemTm> tblItem;

    @FXML
    private TableView<RepairTm> tblRepair;

    @FXML
    private TextField txtCustomerName;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtQtyOnStock;

    @FXML
    private TextField txtRepairDescription;

    @FXML
    private TextField txtRepairPrice;

    @FXML
    private TextField txtTransactionId;

    @FXML
    private TextField txtUnitPrice;

    ObservableList<ItemTm> itemTmObservableList = FXCollections.observableArrayList();
    ObservableList<RepairTm> repairTmObservableList = FXCollections.observableArrayList();
    double netTotal;
    boolean isValidQuantity;
    boolean isValidPrice;
    boolean isItemUpdate;
    boolean isRepairUpdate;

    public void initialize(){
        btnItemAdd.setDisable(true);
        btnItemUpdate.setDisable(true);
        isItemUpdate =false;
        btnItemUpdate.setVisible(false);

        btnRepairAdd.setDisable(true);
        btnRepairUpdate.setDisable(true);
        isRepairUpdate=false;
        btnRepairUpdate.setVisible(false);

        txtQty.setDisable(true);
        txtRepairPrice.setDisable(true);
        try {
            loadTable();
            loadCmb();
            reset();
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void loadTable() {
        //item table
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItemDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colItemUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        Callback<TableColumn<ItemTm, String>, TableCell<ItemTm, String>> itemCellFactory = (TableColumn<ItemTm, String> param) -> {
            // make cell containing buttons
            final TableCell<ItemTm, String> cell = new TableCell<ItemTm, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        deleteIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#ff1744;");
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        editIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#00E676;");
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Removing item from cart", "Are You Sure ?");
                            if (result==ButtonType.OK){
                                ItemTm itemTm =tblItem.getSelectionModel().getSelectedItem();
                                double total= itemTm.getTotal();
                                netTotal-=total;
                                lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
                                tblItem.getItems().removeAll(tblItem.getSelectionModel().getSelectedItem());
                                cmbItemCode.setValue(null);
                                if (repairTmObservableList.isEmpty() && itemTmObservableList.isEmpty()){
                                    btnPlaceOrder.setDisable(true);
                                }
                            }
                        });
                        editIcon.setOnMouseClicked((MouseEvent)->{
                            ItemTm tm=tblItem.getSelectionModel().getSelectedItem();
                            cmbItemCode.setValue(null);
                            isItemUpdate =true;
                            cmbItemCode.setValue(tm.getItemCode());
                            txtQty.setText(String.valueOf(tm.getQuantity()));
                            btnItemAdd.setVisible(false);
                            btnItemUpdate.setVisible(true);
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
        colItemAction.setCellFactory(itemCellFactory);
        tblItem.setItems(itemTmObservableList);

        //repair table
        colRepairId.setCellValueFactory(new PropertyValueFactory<>("repairId"));
        colRepairDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRepairPrice.setCellValueFactory(new PropertyValueFactory<>("repairPrice"));
        Callback<TableColumn<RepairTm, String>, TableCell<RepairTm, String>> repairCellFactory = (TableColumn<RepairTm, String> param) -> {
            // make cell containing buttons
            final TableCell<RepairTm, String> cell = new TableCell<RepairTm, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        deleteIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#ff1744;");
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        editIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#00E676;");
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Removing item from cart", "Are You Sure ?");
                            if (result==ButtonType.OK){
                                RepairTm repairTm =tblRepair.getSelectionModel().getSelectedItem();
                                double total= repairTm.getRepairPrice();
                                netTotal-=total;
                                lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
                                tblRepair.getItems().removeAll(tblRepair.getSelectionModel().getSelectedItem());
                                cmbRepairId.getItems().add(repairTm.getRepairId());
                                if (repairTmObservableList.isEmpty() && itemTmObservableList.isEmpty()){
                                    btnPlaceOrder.setDisable(true);
                                }
                            }
                        });
                        editIcon.setOnMouseClicked((MouseEvent)->{
                            isRepairUpdate=true;
                            RepairTm tm=tblRepair.getSelectionModel().getSelectedItem();
                            cmbRepairId.setValue(tm.getRepairId());
                            txtRepairPrice.setText(String.valueOf(tm.getRepairPrice())+"0");
                            btnRepairAdd.setVisible(false);
                            btnRepairUpdate.setVisible(true);
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
        colRepairActon.setCellFactory(repairCellFactory);
        tblRepair.setItems(repairTmObservableList);
    }

    private void reset() throws SQLException, ClassNotFoundException {
        txtTransactionId.setText(TransactionModel.getNextTransactionId());
        cmbCustomerId.setValue(null);
        txtCustomerName.clear();
        btnPlaceOrder.setDisable(true);
    }

    private void loadCmb() throws SQLException, ClassNotFoundException {
        cmbCustomerId.getItems().clear();
        cmbItemCode.getItems().clear();
        cmbItemDescription.getItems().clear();
        cmbRepairId.getItems().clear();
        ArrayList<Customer> customerArrayList= CustomerModel.getAllCustomer();
        for (Customer customer:customerArrayList){
            cmbCustomerId.getItems().add(customer.getCustomerId());
        }
        ArrayList<Item>itemArrayList= ItemModel.getAllItem();
        for (Item item:itemArrayList){
            cmbItemCode.getItems().add(item.getItemCode());
            cmbItemDescription.getItems().add(item.getDescription());
        }
        ArrayList<Repair>repairArrayList= RepairModel.getAllNonReturnRepair();
        for (Repair repair:repairArrayList){
            cmbRepairId.getItems().add(repair.getRepairId());
        }
    }

    @FXML
    void PlaceOrderOnAction(ActionEvent event) {
        ButtonType btnResult=Project.showError(Alert.AlertType.CONFIRMATION, "Place Payment","Total cost : "+NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal)+"\nAre you confirm ? ");
        if (btnResult==ButtonType.OK) {
            String transactionId = txtTransactionId.getText();
            String customerId = String.valueOf(cmbCustomerId.getValue());
            ArrayList<ItemTransactionDetails> itemDetails = new ArrayList<>();
            ArrayList<RepairTransactionDetails> repairDetails = new ArrayList<>();
            for (int i = 0; i < tblItem.getItems().size(); i++) {
                ItemTm itemTm = itemTmObservableList.get(i);
                itemDetails.add(new ItemTransactionDetails(transactionId, itemTm.getItemCode(), itemTm.getQuantity(), itemTm.getUnitPrice()));
            }
            for (int i = 0; i < tblRepair.getItems().size(); i++) {
                RepairTm repairTm = repairTmObservableList.get(i);
                repairDetails.add(new RepairTransactionDetails(transactionId, repairTm.getRepairId(), repairTm.getRepairPrice()));
            }
            String type=itemDetails.size()>0 && repairDetails.size()>0?"items/repair":itemDetails.size()>0?"items":repairDetails.size()>0?"repair":"";
            Transaction transaction = new Transaction(transactionId, LocalDateTime.now().toString(), customerId, type, netTotal,itemDetails,repairDetails);
            try {
                if (TransactionModel.addTransaction(transaction)) {
                    loadCmb();
                    reset();
                    resetTableOnAction(event);
                    cmbItemCode.setValue(null);
                    cmbItemDescription.setValue(null);
                    cmbRepairId.setValue(null);
                    txtType.clear();
                    txtUnitPrice.clear();
                    txtQtyOnStock.clear();
                    txtQty.clear();
                    txtRepairDescription.clear();
                    txtRepairPrice.clear();
                    txtQty.setDisable(true);
                    txtRepairPrice.setDisable(true);
                    btnItemAdd.setDisable(true);
                    btnRepairAdd.setDisable(true);
                    lblTotal.setText("Rs.0.00");
                    netTotal = 0.00;
                    Project.showError(Alert.AlertType.INFORMATION, "Successfully", "Payment added!");
                }else {
                    Project.showError(Alert.AlertType.ERROR, "unsuccessfully", "Payment adding fail!");
                }
            } catch (SQLException | ClassNotFoundException e) {
                ButtonType result2=Project.showError(Alert.AlertType.CONFIRMATION, "Payment error",e+"\nyou want exit program ? ");
                if (result2==ButtonType.OK){
                    Platform.exit();
                }
            }
        }
    }

    @FXML
    void repairQtyKeyReleasedOnAction(KeyEvent event) {
        if ((!txtRepairPrice.getText().isEmpty()) && Project.isValidPrice(txtRepairPrice.getText())){
            txtRepairPrice.setStyle("-fx-border-color: #76ff03");
            isValidPrice =true;
            btnRepairAdd.setDisable(false);
            btnRepairUpdate.setDisable(false);
        }else {
            txtRepairPrice.setStyle("-fx-border-color: #ff0000");
            isValidPrice =false;
            btnRepairAdd.setDisable(true);
            btnRepairUpdate.setDisable(true);
        }
    }

    @FXML
    void cmbCustomerIdOnAction(ActionEvent event) {
        try {
            String cusId=cmbCustomerId.getValue();
            String name=CustomerModel.searchCustomerName(cusId);
            txtCustomerName.setText(name);
            if ((!itemTmObservableList.isEmpty()) || (!repairTmObservableList.isEmpty()))
                btnPlaceOrder.setDisable(false);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    @FXML
    void cmbItemCodeOnAction(ActionEvent event) {
        try {
            Item item=ItemModel.search(cmbItemCode.getSelectionModel().getSelectedItem());
            itemPane(item);
            cmbItemDescription.setValue(item.getDescription());
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}
    }
    private void itemPane(Item item) {
        int qtyOnStock=item.getQtyOnStock();
        if (!isItemUpdate){
            if (!tblItem.getItems().isEmpty()) {
                for (int i = 0; i < tblItem.getItems().size(); i++) {
                    if (colItemCode.getCellData(i).equals(item.getItemCode())) {
                        qtyOnStock -= colItemQty.getCellData(i);
                        break;
                    }

                }
            }
        }
        isItemUpdate =false;
        txtQtyOnStock.setText(String.valueOf(qtyOnStock));
        txtType.setText(item.getItemType());
        txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        btnItemAdd.setDisable(true);
        btnItemUpdate.setDisable(true);
        txtQty.setDisable(false);
        btnItemAdd.setVisible(true);
        btnItemUpdate.setVisible(false);
        txtQty.clear();
        txtQty.requestFocus();
    }
    @FXML
    void cmbItemDescriptionOnAction(ActionEvent event) {
        try {
            Item item=ItemModel.search(cmbItemDescription.getSelectionModel().getSelectedItem());
            cmbItemCode.setValue(item.getItemCode());
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}
    }

    @FXML
    void cmbRepairIdOnActon(ActionEvent event) {
        try {
            String repairId=cmbRepairId.getValue();
            String description=RepairModel.searchRepairId(repairId);
            txtRepairDescription.setText(description);
            btnRepairAdd.setDisable(true);
            btnRepairAdd.setVisible(true);
            btnRepairUpdate.setDisable(true);
            btnRepairUpdate.setVisible(false);
            txtRepairPrice.setDisable(false);
            txtRepairPrice.clear();
            txtRepairPrice.requestFocus();
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }
    @FXML
    void resetOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        reset();

    }
    @FXML
    void itemAddOnAction(ActionEvent event) {
        String code = String.valueOf(cmbItemCode.getValue());
        String description = String.valueOf(cmbItemDescription.getValue());
        String type=txtType.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        int qtyOnStock = Integer.parseInt(txtQtyOnStock.getText())-qty;
        txtQtyOnStock.setText(String.valueOf(qtyOnStock));
        double total = unitPrice * qty;
        netTotal +=total;
        txtQty.clear();
        lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
        btnItemAdd.setDisable(true);
        if (!cmbCustomerId.getSelectionModel().isEmpty())
            btnPlaceOrder.setDisable(false);
        else
            btnPlaceOrder.setDisable(true);
        if (!itemTmObservableList.isEmpty()){
            for ( int i=0; i<tblItem.getItems().size(); i++){
                if (colItemCode.getCellData(i).equals(code)){
                    qty += colItemQty.getCellData(i);
                    total = unitPrice * qty;

                    itemTmObservableList.get(i).setQuantity(qty);
                    itemTmObservableList.get(i).setTotal(total);
                    tblItem.refresh();
                    return;
                }
            }
        }
        itemTmObservableList.add(new ItemTm(code,description,qty,unitPrice,total));
        tblItem.setItems(itemTmObservableList);
    }

    @FXML
    void repairAddOnAction(ActionEvent event) {
        String id = String.valueOf(cmbRepairId.getValue());
        String description = String.valueOf(txtRepairDescription.getText());
        double repairPrice=Double.parseDouble(txtRepairPrice.getText());
        netTotal +=repairPrice;
        txtRepairPrice.clear();
        lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
        btnRepairAdd.setDisable(true);
        if (!cmbCustomerId.getSelectionModel().isEmpty())
            btnPlaceOrder.setDisable(false);
        else
            btnPlaceOrder.setDisable(true);
        repairTmObservableList.add(new RepairTm(id,description,repairPrice));
        tblRepair.setItems(repairTmObservableList);
        cmbRepairId.getItems().remove(id);
        cmbRepairId.setValue(null);
        txtRepairPrice.setDisable(true);
    }

    @FXML
    void itemQtyKeyReleasedOnAction(KeyEvent event) {
        int qtyOnStock=Integer.parseInt(txtQtyOnStock.getText());
        int qty=txtQty.getText().isEmpty()?0:Integer.parseInt(txtQty.getText());
        if (txtQty.getText().isEmpty() || 0 > qtyOnStock-qty){
            btnItemAdd.setDisable(true);
            btnItemUpdate.setDisable(true);
            txtQty.setStyle("-fx-border-color: #ff0000");
            isValidQuantity =false;
        }else {
            btnItemAdd.setDisable(false);
            btnItemUpdate.setDisable(false);
            txtQty.setStyle("-fx-border-color: #76ff03");
            isValidQuantity =true;
        }
    }

    @FXML
    void qtyKeyTypedOnAction(KeyEvent event) {
        if (!event.getCharacter().matches("[\\d]")) {
            event.consume();
        }
    }

    public void priceKeyTypedOnAction(KeyEvent keyEvent) {
        if (!keyEvent.getCharacter().matches("[\\d\\.]")) {
            keyEvent.consume();
        }
    }



    @FXML
    void resetTableOnAction(ActionEvent event) {
        itemTmObservableList.clear();
        tblItem.refresh();
        repairTmObservableList.clear();
        tblRepair.refresh();
    }

    @FXML
    void txtQtyOnAction(ActionEvent event) {
        if (isValidQuantity) {
            if (isItemUpdate)
                itemUpdateOnAction(event);
            else
                itemAddOnAction(event);
        }
    }

    @FXML
    void txtRepairPriceOnAction(ActionEvent event) {
        if (isValidPrice) {
            if (isRepairUpdate)
                repairUpdateOnAction(event);
            else
                repairAddOnAction(event);
        }
    }

    public void itemUpdateOnAction(ActionEvent actionEvent) {
        isItemUpdate=false;
        double unitPrice=Double.parseDouble(txtUnitPrice.getText());
        ItemTm tm=tblItem.getSelectionModel().getSelectedItem();
        int qty=tm.getQuantity();
        netTotal-=qty*unitPrice;
        qty=Integer.parseInt(txtQty.getText());
        tm.setQuantity(qty);
        tm.setTotal(qty*unitPrice);
        netTotal+=qty*unitPrice;
        lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
        tblItem.refresh();
        cmbItemCode.setValue(null);
    }

    public void repairUpdateOnAction(ActionEvent actionEvent) {
        isRepairUpdate=false;
        RepairTm tm=tblRepair.getSelectionModel().getSelectedItem();
        double price=tm.getRepairPrice();
        netTotal-=price;
        price=Double.parseDouble(txtRepairPrice.getText());
        netTotal+=price;
        tm.setRepairPrice(price);
        tblRepair.refresh();
        lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
        cmbRepairId.setValue(null);
        txtRepairPrice.setDisable(true);
    }

    public void resetItem(ActionEvent actionEvent) {
        cmbItemCode.setValue(null);
        cmbItemDescription.setValue(null);
        txtQty.setDisable(true);
    }

    public void resetRepair(ActionEvent actionEvent) {
        cmbRepairId.setValue(null);
        txtRepairPrice.setDisable(true);
    }
}
