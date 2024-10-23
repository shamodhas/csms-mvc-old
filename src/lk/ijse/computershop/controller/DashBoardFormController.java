package lk.ijse.computershop.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lk.ijse.computershop.model.CustomerModel;
import lk.ijse.computershop.model.RepairModel;
import lk.ijse.computershop.model.TransactionModel;
import lk.ijse.computershop.to.Customer;
import lk.ijse.computershop.to.Transaction;
import lk.ijse.computershop.util.Project;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class DashBoardFormController {
    @FXML
    private AnchorPane anc;
    @FXML
    public NumberAxis yAxis;
    @FXML
    public CategoryAxis xAxis;
    @FXML
    private BarChart barChart;
    @FXML
    private Label lblTotalCostomer;

    @FXML
    private Label lblTotalIncomeToDay;

    @FXML
    private Label lblTotalRepairsmustbeDelivered;

    @FXML
    private Label lblTotalTransaction;

    @FXML
    private Label lblTotalTransactionToDay;

    @FXML
    private ScrollPane scPane;

    public void initialize(){
        loadLabel();
        loadBarChart();
        loadScrollPane();
    }

    private void loadScrollPane() {
        VBox vBox=new VBox();
        Label nameLabel=new Label("");
        nameLabel.setStyle("-fx-text-fill: #000000;" +
                "-fx-font-size:16px;"+
                "-fx-font-family: Arial Rounded MT Bold;"
        );
        nameLabel.setPrefWidth(200);
        Label priceLabel=new Label("");
        priceLabel.setStyle("-fx-text-fill: #000000;"+
                "-fx-font-size: 16px;"+
                "-fx-font-family: Arial Rounded MT Bold;"
        );
        priceLabel.setPrefWidth(80);
        priceLabel.setAlignment(Pos.CENTER);
        Label typeLabel=new Label("");
        typeLabel.setStyle("-fx-text-fill: #000000;"+
                "-fx-font-size: 16px;"+
                "-fx-font-family: Arial Rounded MT Bold;"
        );
        typeLabel.setPrefWidth(95);
        typeLabel.setAlignment(Pos.CENTER_RIGHT);
        try {
            ArrayList<Customer>allCustomerList=CustomerModel.getAllCustomer();
            for (int i=allCustomerList.size()-1;i>=0;i--){
                Transaction transaction=TransactionModel.searchCustomer(allCustomerList.get(i).getCustomerId());
                if (transaction==null){
                    continue;
                }
                nameLabel.setText("   "+allCustomerList.get(i).getName());
                priceLabel.setText(String.valueOf(transaction.getTotal()));
                typeLabel.setText(transaction.getType());
                HBox hBox=new HBox(nameLabel,priceLabel,typeLabel);
                hBox.setStyle("-fx-border-width: 0px 0px 1px 0px;"+
                        "-fx-border-color: #000000");
                vBox.getChildren().add(hBox);

            }

        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
        scPane.setContent(vBox);
    }
    private void loadBarChart() {
        try {
            XYChart.Series<String, Integer> series = new XYChart.Series();
            for (int i=1;i<16;i++) {
                LocalDate date=Project.sub(-(15-i));
                int count = TransactionModel.searchDateTransactionCount(date);
                series.getData().add(new XYChart.Data<>(date.toString(), count));
            }
            barChart.getData().addAll(series);

        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }

    }
    private void loadLabel() {
        try {
            lblTotalCostomer.setText(String.valueOf(CustomerModel.getCustomerCount()));
            lblTotalRepairsmustbeDelivered.setText(String.valueOf(RepairModel.getNonReturnRepairCount()));
            lblTotalTransaction.setText(String.valueOf(TransactionModel.getAllTransactionCount()));
            lblTotalIncomeToDay.setText(NumberFormat.getCurrencyInstance(new Locale("en","in")).format(TransactionModel.getTodayAllIncome()));
            lblTotalTransactionToDay.setText(String.valueOf(TransactionModel.searchDateTransactionCount(LocalDate.now())));
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }
}
