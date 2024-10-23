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
import lk.ijse.computershop.model.ItemModel;
import lk.ijse.computershop.to.Item;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class ItemFormController {

    @FXML
    private AnchorPane anc;

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private ComboBox<String> cmbType;

    @FXML
    private TableColumn<Item, String> colAction;

    @FXML
    private TableColumn<Item, String> colDescription;

    @FXML
    private TableColumn<Item, String> colItemCode;

    @FXML
    private TableColumn<Item, Integer> colQtyOnStock;

    @FXML
    private TableColumn<Item, String> colType;

    @FXML
    private TableColumn<Item, Double> colUnitPrice;

    @FXML
    private TableView<Item> tbl;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtItemCode;

    @FXML
    private TextField txtQtyOnStock;

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtUnitPrice;

    ObservableList<Item>itemObservableList= FXCollections.observableArrayList();
    Item item=null;
    boolean isValidPrice=false;
    boolean isUpdate=false;
    public void initialize(){
        btnUpdate.setDisable(true);
        loadCmb();
        loadTable();
        resetOnAction(new ActionEvent());
    }

    private void loadCmb() {
        String[] type={"Laptop","Desktop","Accessories","Parts"};
        cmbType.getItems().addAll(type);
    }

    private void loadTable() {
        refreshTable();
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnStock.setCellValueFactory(new PropertyValueFactory<>("qtyOnStock"));
        Callback<TableColumn<Item, String>, TableCell<Item, String>> cellFactory = (TableColumn<Item, String> param) -> {
            // make cell containing buttons
            final TableCell<Item, String> cell = new TableCell<Item, String>() {
                @Override
                public void updateItem(String uItem, boolean empty) {
                    super.updateItem(uItem, empty);
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
                            item=tbl.getSelectionModel().getSelectedItem();
                            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Deleting a Item code "+item.getItemCode(),"When the item is deleted,The order is also deleted\nAre You Sure ? ");
                            if (result==ButtonType.OK){
                                try {
                                    if (ItemModel.deleteItem(item.getItemCode())){
                                        clearAll();
                                        refreshTable();
                                        Project.showError(Alert.AlertType.INFORMATION, "Deleting Successfully",item.getDescription()+" was deleted");
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
                            item=tbl.getSelectionModel().getSelectedItem();
                            txtItemCode.setText(item.getItemCode());
                            cmbType.getSelectionModel().select(item.getItemType());
                            txtDescription.setText(item.getDescription());
                            txtUnitPrice.setText(String.format("%.2f",item.getUnitPrice()));
                            txtQtyOnStock.setText(String.valueOf(item.getQtyOnStock()));
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
        tbl.setItems(itemObservableList);
    }

    private void refreshTable() {
        itemObservableList.clear();
        try {
            ArrayList<Item> itemArrayList= ItemModel.getAllItem();
            for (Item item:itemArrayList){
                itemObservableList.add(item);
            }
            tbl.setItems(itemObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    @FXML
    void addOnAction(ActionEvent event) {
        priceValidation();
        if (cmbType.getSelectionModel().isEmpty())
            cmbType.setStyle("-fx-border-color: #ff0000");
        else if (txtDescription.getText().isEmpty())
            txtDescription.requestFocus();
        else if ((!isValidPrice) || txtUnitPrice.getText().isEmpty())
            txtUnitPrice.requestFocus();
        else if (txtQtyOnStock.getText().isEmpty())
            txtQtyOnStock.requestFocus();
        else {
            String itemCode=txtItemCode.getText();
            String type=cmbType.getSelectionModel().getSelectedItem();
            String description=txtDescription.getText();
            double unitPrice=Double.parseDouble(txtUnitPrice.getText());
            int qtyOnStock=Integer.parseInt(txtQtyOnStock.getText());
            try {
                String existsItemCode=ItemModel.searchDescriptionItem(description);
                if (existsItemCode==null){
                    item = new Item(itemCode,type,description,unitPrice,qtyOnStock);
                    if (ItemModel.addItem(item)){
                        clearAll();
                        refreshTable();
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","item data added.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "adding Error","data not added...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","item exists,item code  "+existsItemCode);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }

    @FXML
    void cmbTypeOnAction(ActionEvent event) {
        cmbType.setStyle("-fx-border-color: #76ff03");
        txtDescription.requestFocus();
    }

    @FXML
    void descriptionOnAction(ActionEvent event) {
        if (!txtDescription.getText().isEmpty())
            txtUnitPrice.requestFocus();
    }

    @FXML
    void qtyOnStockOnAction(ActionEvent event) {
        if (isUpdate){
            updateOnAction(event);
        }else {
            addOnAction(event);
        }

    }

    @FXML
    void resetOnAction(ActionEvent event) {
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        clearAll();
    }

    private void clearAll() {
        try {
            txtItemCode.setText(ItemModel.getNextItemCode());
            cmbType.getSelectionModel().clearSelection();
            txtDescription.clear();
            txtUnitPrice.clear();
            txtQtyOnStock.clear();
        } catch (SQLException | ClassNotFoundException e) {
            Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
        }
    }

    @FXML
    void txtSearchMouseOnClick(MouseEvent event) {
        txtSearch.clear();
        FilteredList<Item> filteredList=new FilteredList<>(itemObservableList, b -> true);
        txtSearch.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(itemSearchModel ->{
                if (newValue.isEmpty() || newValue==null ){
                    return true;
                }
                String searchKeyword=newValue.toLowerCase();
                if (itemSearchModel.getItemCode().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (itemSearchModel.getItemType().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (itemSearchModel.getDescription().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (String.valueOf(itemSearchModel.getUnitPrice()).indexOf(searchKeyword) > -1){
                    return true;
                }else if (String.valueOf(itemSearchModel.getQtyOnStock()).indexOf(searchKeyword) > -1){
                    return true;
                }else {
                    return false;
                }
            });
        });
        SortedList<Item> sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tbl.comparatorProperty());
        tbl.setItems(sortedList);
    }

    @FXML
    void unitPriceOnAction(ActionEvent event) {
        if (isValidPrice)
            txtQtyOnStock.requestFocus();
    }

    @FXML
    void updateOnAction(ActionEvent event) {
        priceValidation();
        if (cmbType.getSelectionModel().isEmpty())
            cmbType.setStyle("-fx-border-color: #ff0000");
        else if (txtDescription.getText().isEmpty())
            txtDescription.requestFocus();
        else if ((!isValidPrice) || txtUnitPrice.getText().isEmpty())
            txtUnitPrice.requestFocus();
        else if (txtQtyOnStock.getText().isEmpty())
            txtQtyOnStock.requestFocus();
        else {
            String itemCode=txtItemCode.getText();
            String type=cmbType.getSelectionModel().getSelectedItem();
            String description=txtDescription.getText();
            double unitPrice=Double.parseDouble(txtUnitPrice.getText());
            int qtyOnStock=Integer.parseInt(txtQtyOnStock.getText());
            try {
                String existsItemCode=ItemModel.searchDescriptionItem(description);
                if (existsItemCode==null || existsItemCode.equals(itemCode)){
                    item = new Item(itemCode,type,description,unitPrice,qtyOnStock);
                    if (ItemModel.updateItem(item)){
                        btnUpdate.setDisable(true);
                        btnAdd.setDisable(false);
                        clearAll();
                        refreshTable();
                        isUpdate=false;
                        Project.showError(Alert.AlertType.INFORMATION, "successfully","item data updated.");
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "update Error","data not updated...!");
                    }
                }else {
                    Project.showError(Alert.AlertType.INFORMATION, "exists","item exists,item code "+existsItemCode);
                }
            } catch (SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "DataBase Error",e+"");
            }
        }
    }
    @FXML
    public void txtUnitPriceKeyPressedOnAction(KeyEvent keyEvent) {
        if (!keyEvent.getCharacter().matches("[0-9\\.]"))
            keyEvent.consume();
    }
    @FXML
    public void txtQtyOnStockKeyPressedOnAction(KeyEvent keyEvent) {
        if (!keyEvent.getCharacter().matches("[0-9]"))
            keyEvent.consume();
    }
    private void priceValidation(){
        if (Project.isValidPrice(txtUnitPrice.getText())){
            txtUnitPrice.setStyle("-fx-border-color: #76ff03");
            isValidPrice=true;
        }else {
            txtUnitPrice.setStyle("-fx-border-color: #ff0000");
            isValidPrice=false;
        }
    }
}
