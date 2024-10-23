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
import lk.ijse.computershop.model.ItemModel;
import lk.ijse.computershop.model.SupplierModel;
import lk.ijse.computershop.model.SuppliesModel;
import lk.ijse.computershop.tm.SuppliesCartTm;
import lk.ijse.computershop.to.Item;
import lk.ijse.computershop.to.Supplier;
import lk.ijse.computershop.to.Supplies;
import lk.ijse.computershop.to.SuppliesDetails;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class SuppliesFormController {
    public Label lblTotal;


    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnPlaceOrder;

    @FXML
    private ComboBox<String> cmbCode;

    @FXML
    private ComboBox<String> cmbDescription;

    @FXML
    private ComboBox<String> cmbSupplierId;

    @FXML
    private ComboBox<String> cmbSupplierName;

    @FXML
    public TableColumn<SuppliesCartTm, String> colType;

    @FXML
    public TableColumn<SuppliesCartTm, String> colDescription;
    @FXML
    private TableColumn<SuppliesCartTm, String> colAction;

    @FXML
    private TableColumn<SuppliesCartTm, String> colCode;

    @FXML
    private TableColumn<SuppliesCartTm, Integer> colNewStockQty;

    @FXML
    private TableColumn<SuppliesCartTm, Integer> colOldSctockQty;

    @FXML
    private TableColumn<SuppliesCartTm, Integer> colQty;
    @FXML
    public TableColumn<SuppliesCartTm, Double> colTotal;

    @FXML
    private TableColumn<SuppliesCartTm, Double> colUnitPrice;

    @FXML
    private TableView<SuppliesCartTm> tblSupplies;

    @FXML
    private TextField txtQty;

    @FXML
    public TextField txtType;

    @FXML
    public TextField txtQtyOnStock;

    @FXML
    private TextField txtSuppliesId;

    @FXML
    private TextField txtUnitPrice;
    ObservableList<SuppliesCartTm> tmObservableArray = FXCollections.observableArrayList();
    double netTotal;
    boolean isValidQuantity;

    public void initialize(){
        btnAdd.setDisable(true);
        txtQty.setDisable(true);
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

    private void loadCmb() throws SQLException, ClassNotFoundException {
        ArrayList<Supplier>supplierArrayList= SupplierModel.getAllSupplier();
        for (Supplier supplier:supplierArrayList){
            cmbSupplierId.getItems().add(supplier.getSupplierId());
            cmbSupplierName.getItems().add(supplier.getName());
        }
        ArrayList<Item>itemArrayList= ItemModel.getAllItem();
        for (Item item:itemArrayList){
            cmbCode.getItems().add(item.getItemCode());
            cmbDescription.getItems().add(item.getDescription());
        }
    }

    private void reset() throws SQLException, ClassNotFoundException {

        txtSuppliesId.setText(SuppliesModel.getNextSuppliesId());
        cmbSupplierId.setValue(null);
        cmbSupplierName.setValue(null);
        btnPlaceOrder.setDisable(true);
    }

    private void loadTable() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOldSctockQty.setCellValueFactory(new PropertyValueFactory<>("oldQuantity"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colNewStockQty.setCellValueFactory(new PropertyValueFactory<>("newQuantity"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        Callback<TableColumn<SuppliesCartTm, String>, TableCell<SuppliesCartTm, String>> cellFactory = (TableColumn<SuppliesCartTm, String> param) -> {
            // make cell containing buttons
            final TableCell<SuppliesCartTm, String> cell = new TableCell<SuppliesCartTm, String>() {
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
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Removing item from cart", "Are You Sure ?");
                            if (result==ButtonType.OK){
                                SuppliesCartTm suppliesCartTm =tblSupplies.getSelectionModel().getSelectedItem();
                                double total= suppliesCartTm.getTotal();
                                netTotal-=total;
                                lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
                                tblSupplies.getItems().removeAll(tblSupplies.getSelectionModel().getSelectedItem());
                                if (tmObservableArray.isEmpty()){
                                    btnPlaceOrder.setDisable(true);
                                }
                            }
                        });
                        HBox hBox = new HBox(deleteIcon);
                        hBox.setStyle("-fx-alignment:center");
                        HBox.setMargin(deleteIcon, new Insets(2, 0, 2, 0));
                        setGraphic(hBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        colAction.setCellFactory(cellFactory);
        tblSupplies.setItems(tmObservableArray);
    }

    @FXML
    void PlaceOrderOnAction(ActionEvent event) {
        ButtonType result=Project.showError(Alert.AlertType.CONFIRMATION, "Place Supplies","Total cost : "+NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal)+"\nAre you confirm ? ");
        if (result==ButtonType.OK) {
            String suppliesId = txtSuppliesId.getText();
            String supplierId = cmbSupplierId.getSelectionModel().getSelectedItem();
            ArrayList<SuppliesDetails> suppliesDetails = new ArrayList<>();
            for (int i = 0; i < tblSupplies.getItems().size(); i++) {
                SuppliesCartTm tm = tmObservableArray.get(i);
                suppliesDetails.add(new SuppliesDetails(suppliesId, tm.getItemCode(), tm.getQuantity(), tm.getUnitPrice()));
            }
            Supplies supplies = new Supplies(suppliesId, LocalDateTime.now().toString(), supplierId, suppliesDetails);
            try {
                if (SuppliesModel.addSupplies(supplies)) {
                    reset();
                    resetTable();
                    cmbCode.setValue(null);
                    cmbDescription.setValue(null);
                    txtType.clear();
                    txtUnitPrice.clear();
                    txtQtyOnStock.clear();
                    txtQty.clear();
                    txtQty.setDisable(true);
                    btnAdd.setDisable(true);
                    lblTotal.setText("Rs.0.00");
                    netTotal = 0.00;
                    Project.showError(Alert.AlertType.INFORMATION, "Successfully", "supplies added!");
                }else {
                    Project.showError(Alert.AlertType.ERROR, "unsuccessfully", "supplies adding fail!");
                }
            } catch (SQLException | ClassNotFoundException e) {
                ButtonType result2=Project.showError(Alert.AlertType.CONFIRMATION, "Reduction error",e+"\nyou want exit program ? ");
                if (result2==ButtonType.OK){
                    Platform.exit();
                }
            }
        }
    }

    @FXML
    void addOnAction(ActionEvent event) {
        String code = String.valueOf(cmbCode.getValue());
        String description = String.valueOf(cmbDescription.getValue());
        String type=txtType.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int oldQty = Integer.parseInt(txtQty.getText());
        int qty = Integer.parseInt(txtQty.getText());
        int newQty = qty + Integer.parseInt(txtQty.getText());
        double total = unitPrice * qty;
        netTotal +=total;
        txtQty.clear();
        lblTotal.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(netTotal));
        btnAdd.setDisable(true);
        if (!cmbSupplierId.getSelectionModel().isEmpty())
            btnPlaceOrder.setDisable(false);
        if (!tmObservableArray.isEmpty()){
            for ( int i=0; i<tblSupplies.getItems().size(); i++){
                if (colCode.getCellData(i).equals(code)){
                    qty += colQty.getCellData(i);
                    newQty += colNewStockQty.getCellData(i);
                    total = unitPrice * qty;

                    tmObservableArray.get(i).setQuantity(qty);
                    tmObservableArray.get(i).setNewQuantity(newQty);
                    tmObservableArray.get(i).setTotal(total);
                    tblSupplies.refresh();
                    return;
                }
            }
        }
        tmObservableArray.add(new SuppliesCartTm(code,description,type,unitPrice,oldQty,qty,newQty,total));
        tblSupplies.setItems(tmObservableArray);
    }

    @FXML
    void cmbCodeOnAction(ActionEvent event) {
        try {
            Item item=ItemModel.search(cmbCode.getSelectionModel().getSelectedItem());
            itemPane(item);
            cmbDescription.setValue(item.getDescription());
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}
    }

    private void itemPane(Item item) {
        txtType.setText(item.getItemType());
        txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        txtQtyOnStock.setText(String.valueOf(item.getQtyOnStock()));
        btnAdd.setDisable(true);
        txtQty.setDisable(false);
        txtQty.requestFocus();
    }

    @FXML
    void cmbDescriptionOnAction(ActionEvent event) {
        try {
            Item item=ItemModel.search(cmbDescription.getSelectionModel().getSelectedItem());
            cmbCode.setValue(item.getItemCode());
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}
    }

    @FXML
    void cmbSupplierIdOnAction(ActionEvent event) {
        try {
            String supplierName= SupplierModel.searchId(cmbSupplierId.getSelectionModel().getSelectedItem());
            cmbSupplierName.getSelectionModel().select(supplierName);
            if (!tmObservableArray.isEmpty())
                btnPlaceOrder.setDisable(false);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}
    }

    @FXML
    void cmbSupplierNameOnAction(ActionEvent event) {
        try {
            String supplierId = SupplierModel.searchName(cmbSupplierName.getSelectionModel().getSelectedItem());
            cmbSupplierId.getSelectionModel().select(supplierId);
            if (!tmObservableArray.isEmpty())
                btnPlaceOrder.setDisable(false);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}
    }

    @FXML
    void resetOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        reset();

    }
    @FXML
    public void resetTableOnAction(ActionEvent actionEvent) {
        resetTable();
    }

    private void resetTable() {
        tmObservableArray.clear();
        tblSupplies.refresh();
    }

    @FXML
    public void qtyKeyReleasedOnAction(KeyEvent keyEvent) {
        if (txtQty.getText().isEmpty()){
            btnAdd.setDisable(true);
            txtQty.setStyle("-fx-border-color: #ff0000");
            isValidQuantity =false;
        }else {
            btnAdd.setDisable(false);
            txtQty.setStyle("-fx-border-color: #76ff03");
            isValidQuantity =true;
        }
    }
    @FXML
    public void qtyKeyTypedOnAction(KeyEvent keyEvent) {
        if (!keyEvent.getCharacter().matches("\\d")) {
            keyEvent.consume();
        }
    }

    @FXML
    public void qtyOnAction(ActionEvent actionEvent) {
        if (isValidQuantity)
            addOnAction(actionEvent);
    }
}
