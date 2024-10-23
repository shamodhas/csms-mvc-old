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

public class StockFormController {
    @FXML
    public JFXButton btnM;
    @FXML
    public JFXButton btnP;
    @FXML
    private AnchorPane ancStock;

    @FXML
    private ComboBox<String> cmbDescription;

    @FXML
    private ComboBox<String> cmbItem;

    @FXML
    private TableColumn<Item, String> colAction;

    @FXML
    private TableColumn<Item, String> colCode;

    @FXML
    private TableColumn<Item, String> colDescription;

    @FXML
    private TableColumn<Item, Integer> colQtyOnStock;

    @FXML
    private TableColumn<Item, String> colType;

    @FXML
    private TableColumn<Item, Double> colUnitPrice;

    @FXML
    private TableView<Item> tblItem;

    @FXML
    private TextField txtQty;

    @FXML
    private TextField txtSearch;
    ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
    Item item=null;

    public void initialize(){
        try {
            loadCmb();
            loadTable();
            reset();
        } catch (SQLException | ClassNotFoundException e) {

        }
    }

    private void loadTable() throws SQLException, ClassNotFoundException {
        refreshTable();
        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colType.setCellValueFactory(new PropertyValueFactory<>("itemType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnStock.setCellValueFactory(new PropertyValueFactory<>("qtyOnStock"));
        Callback<TableColumn<Item, String>, TableCell<Item, String>> cellFactory = (TableColumn<Item, String> param) -> {
            // make cell containing buttons
            final TableCell<Item, String> cell = new TableCell<Item, String>() {
                @Override
                public void updateItem(String item2, boolean empty) {
                    super.updateItem(item2, empty);
                    //that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        editIcon.setStyle(" -fx-cursor: hand ;" + "-glyph-size:28px;" + "-fx-fill:#00E676;");
                        editIcon.setOnMouseClicked((MouseEvent event) -> {
                            item=tblItem.getSelectionModel().getSelectedItem();
                            cmbItem.getSelectionModel().select(item.getItemCode());
                            cmbDescription.getSelectionModel().select(item.getDescription());
                            txtQty.setStyle("-fx-border-color: #76ff03");
                            btnM.setDisable(false);
                            btnP.setDisable(false);
                        });
                        HBox hBox = new HBox(editIcon);
                        hBox.setStyle("-fx-alignment:center");
                        HBox.setMargin(editIcon, new Insets(2, 0, 0, 0));
                        setGraphic(hBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        colAction.setCellFactory(cellFactory);
        tblItem.setItems(itemObservableList);
    }

    private void refreshTable() throws SQLException, ClassNotFoundException {
        ArrayList<Item>allItem=ItemModel.getAllItem();
        itemObservableList.clear();
        for (Item item:allItem){
            itemObservableList.add(item);
        }
        tblItem.setItems(itemObservableList);
    }

    private void loadCmb() throws SQLException, ClassNotFoundException {
        ArrayList<Item>allItem= ItemModel.getAllItem();
        for (Item item:allItem) {
            cmbItem.getItems().add(item.getItemCode());
            cmbDescription.getItems().add(item.getDescription());
        }
    }

    @FXML
    void addOnAction(ActionEvent event) {
        if (cmbItem.getSelectionModel().getSelectedItem()==null){
            cmbItem.setStyle("-fx-border-color: #ff0000");
            cmbDescription.setStyle("-fx-border-color: #ff0000");
        } else if (txtQty.getText().isEmpty()) {
            txtQty.setStyle("-fx-border-color: #ff0000");
        }else {
            String itemCode=cmbItem.getSelectionModel().getSelectedItem();
            int addingQty=Integer.parseInt(txtQty.getText());
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Adding Quantity ","\nAdd quantity for "+itemCode+" ? ");
            if (result==ButtonType.OK) {
                try {
                    if (ItemModel.addQty(itemCode, addingQty)) {
                        int newQty=ItemModel.getQty(itemCode);
                        if (newQty!=-1){
                            Project.showError(Alert.AlertType.INFORMATION, "Adding Successfully","Adding quantity for "+itemCode+"!\nnew quantity is "+newQty);
                            refreshTable();
                            reset();
                        }
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    ButtonType result2=Project.showError(Alert.AlertType.CONFIRMATION, "Adding error",e+"\nyou want exit program ? ");
                    if (result2==ButtonType.OK){
                        Platform.exit();
                    }
                }
            }
        }
    }

    @FXML
    void cmbDescriptionOnAction(ActionEvent event) {
        try {
            cmbItem.setStyle("-fx-border-color: #76ff03");
            cmbDescription.setStyle("-fx-border-color: #76ff03");
            btnP.setDisable(false);
            btnM.setDisable(false);
            String itemCode=ItemModel.searchDescriptionItem(cmbDescription.getSelectionModel().getSelectedItem());
            cmbItem.getSelectionModel().select(itemCode);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}

    }

    @FXML
    void cmbItemOnAction(ActionEvent event) {
        try {
            cmbItem.setStyle("-fx-border-color: #76ff03");
            cmbDescription.setStyle("-fx-border-color: #76ff03");
            btnP.setDisable(false);
            btnM.setDisable(false);
            String description=ItemModel.searchItemCode(cmbItem.getSelectionModel().getSelectedItem());
            cmbDescription.getSelectionModel().select(description);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }catch (NullPointerException e){}

    }

    @FXML
    void minusOnAction(ActionEvent event) {
        if (cmbItem.getSelectionModel().getSelectedItem()==null){
            cmbItem.setStyle("-fx-border-color: #ff0000");
            cmbDescription.setStyle("-fx-border-color: #ff0000");
        } else if (txtQty.getText().isEmpty()) {
            txtQty.setStyle("-fx-border-color: #ff0000");
        }else {
            String itemCode=cmbItem.getSelectionModel().getSelectedItem();
            int reductionQty=Integer.parseInt(txtQty.getText());
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Reduction Quantity ","\nReduction quantity for "+itemCode+" ? ");
            if (result==ButtonType.OK) {
                try {
                    int oldQty=ItemModel.getQty(itemCode);
                    if (oldQty-reductionQty>=0) {
                        if (ItemModel.reducesQty(itemCode, reductionQty)) {
                            int newQty = ItemModel.getQty(itemCode);
                            if (newQty != -1) {
                                Project.showError(Alert.AlertType.INFORMATION, "Reduction Successfully", "Reduced quantity for " + itemCode + "!\nnew quantity is " + newQty);
                                refreshTable();
                                reset();
                            }
                        }
                    }else {
                        Project.showError(Alert.AlertType.ERROR, "Reduction Unsuccessfully", "There is not enough quantity in the stock \nfor the quantity you tried to reduce");
                    }

                } catch (SQLException | ClassNotFoundException e) {
                    ButtonType result2=Project.showError(Alert.AlertType.CONFIRMATION, "Reduction error",e+"\nyou want exit program ? ");
                    if (result2==ButtonType.OK){
                        Platform.exit();
                    }
                }
            }
        }
    }

    private void reset() {
        cmbItem.getSelectionModel().clearSelection();
        cmbDescription.getSelectionModel().clearSelection();
        txtQty.clear();
        btnM.setDisable(true);
        btnP.setDisable(true);
    }

    @FXML
    void txtQtyOnClick(MouseEvent event) {
        if (cmbItem.getSelectionModel().getSelectedItem()==null){
            cmbItem.setStyle("-fx-border-color: #ff0000");
            cmbDescription.setStyle("-fx-border-color: #ff0000");
        }
        txtQty.setStyle("-fx-border-color: #76ff03");
    }

    @FXML
    void txtSearchMouseOnClick(MouseEvent event) {
        txtSearch.clear();
        FilteredList<Item> filteredList=new FilteredList<>(itemObservableList, b -> true);
        txtSearch.textProperty().addListener((observable,oldValue,newValue)->{
            filteredList.setPredicate(Item ->{
                if (newValue.isEmpty() || newValue==null ){
                    return true;
                }
                String searchKeyword=newValue.toLowerCase();
                if (Item.getItemCode().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Item.getItemType().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (Item.getDescription().toLowerCase().indexOf(searchKeyword) > -1){
                    return true;
                }else if (String.valueOf(Item.getUnitPrice()).indexOf(searchKeyword) > -1){
                    return true;
                }else if (String.valueOf(Item.getQtyOnStock()).indexOf(searchKeyword) > -1){
                    return true;
                }else {
                    return false;
                }
            });
        });
        SortedList<Item> sortedList=new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tblItem.comparatorProperty());
        tblItem.setItems(sortedList);
    }
    @FXML
    public void resetOnAction(ActionEvent actionEvent) {
        reset();
    }
    @FXML
    public void txtQtyOnStockKeyPressedOnAction(KeyEvent keyEvent) {
        if (!keyEvent.getCharacter().matches("\\d"))
            keyEvent.consume();
    }
}
