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
import lk.ijse.computershop.model.ItemModel;
import lk.ijse.computershop.model.SuppliesDetailsModel;
import lk.ijse.computershop.model.SuppliesModel;
import lk.ijse.computershop.tm.ItemTm;
import lk.ijse.computershop.tm.SuppliesTm;
import lk.ijse.computershop.to.Supplies;
import lk.ijse.computershop.to.SuppliesDetails;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.util.ArrayList;

public class ViewSuppliesFormController {
    @FXML
    public Label lblSuppliesId;
    @FXML
    private AnchorPane ancViewPurchase;

    @FXML
    private TableColumn<Supplies, String> colDateTime;

    @FXML
    private TableColumn<ItemTm, String> colItemCode;

    @FXML
    private TableColumn<ItemTm, String> colItemDiscription;

    @FXML
    private TableColumn<ItemTm, Integer> colItemQty;

    @FXML
    private TableColumn<ItemTm, Double> colItemTotal;

    @FXML
    private TableColumn<SuppliesTm, String> colSupplierId;

    @FXML
    private TableColumn<SuppliesTm, String> colSuppliesId;

    @FXML
    private TableColumn<SuppliesTm, Double> colTotal;

    @FXML
    private TableColumn<ItemTm, Double> colItemUnitPrice;

    @FXML
    private TableColumn<SuppliesTm, String> colAction;

    @FXML
    private TableView<ItemTm> tblItem;

    @FXML
    private TableView<SuppliesTm> tblSupplies;

    public void initialize(){
        setCellTableFactory();
        loadSuppliesTable();
    }

    private void loadSuppliesTable() {
        try {
            ObservableList<SuppliesTm>suppliesObservableList= FXCollections.observableArrayList();
            ArrayList<Supplies>suppliesArrayList= SuppliesModel.getAllSupplies();
            for (Supplies supplies:suppliesArrayList){
                double total= SuppliesDetailsModel.getTotal(supplies.getSuppliesId());
                suppliesObservableList.add(new SuppliesTm(supplies.getSuppliesId(),supplies.getDateTime(),supplies.getSupplierId(),total));
            }
            tblSupplies.setItems(suppliesObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    private void setCellTableFactory() {
        colSuppliesId.setCellValueFactory(new PropertyValueFactory<>("suppliesId"));
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colItemDiscription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colItemUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        Callback<TableColumn<SuppliesTm, String>, TableCell<SuppliesTm, String>> cellFactory = (TableColumn<SuppliesTm, String> param) -> {
            // make cell containing buttons
            final TableCell<SuppliesTm, String> cell = new TableCell<SuppliesTm, String>() {
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
                            SuppliesTm suppliesTm=tblSupplies.getSelectionModel().getSelectedItem();
                            lblSuppliesId.setText(suppliesTm.getSuppliesId());
                            refreshItemTable(suppliesTm.getSuppliesId());
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

    private void refreshItemTable(String suppliesId) {
        try {
            ObservableList<ItemTm>itemObservableList= FXCollections.observableArrayList();
            ArrayList<SuppliesDetails>details=SuppliesDetailsModel.search(suppliesId);
            for (SuppliesDetails detail:details){
                String itemCode=detail.getItemCode();
                String description= ItemModel.searchItemCode(itemCode);
                int quantity=detail.getQuantity();
                double unitPrice=detail.getUnitPrice();
                double total=quantity*unitPrice;
                itemObservableList.add(new ItemTm(itemCode,description,quantity,unitPrice,total));
            }
            tblItem.setItems(itemObservableList);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }
}
