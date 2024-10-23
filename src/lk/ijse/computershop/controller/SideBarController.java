package lk.ijse.computershop.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lk.ijse.computershop.util.Navigation;
import lk.ijse.computershop.util.Project;
import lk.ijse.computershop.util.Routes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
public class SideBarController{
    @FXML
    public Button btnLoginRecord;
    @FXML
    public Button btnUser;
    @FXML
    public Button btnEmployee;
    @FXML
    public Button btnSupplier;
    @FXML
    public Button btnSupplies;
    @FXML
    public Button btnViewSupplies;
    @FXML
    public Button btnItem;
    @FXML
    public Button btnCustomer;
    @FXML
    public Button btnPlaceorder;
    @FXML
    public Button btnRepair;
    @FXML
    public Button btnViewPurchases;
    @FXML
    public Button btnStock;
    @FXML
    public Button btnReport;
    @FXML
    public Pane rightPane;
    public FontAwesomeIconView btnLoginRecordIcon;
    public FontAwesomeIconView btnUserIcon;
    public FontAwesomeIconView btnEmployeeIcon;
    public FontAwesomeIconView btnSupplierIcon;
    public FontAwesomeIconView btnSuppliesIcon;
    public FontAwesomeIconView btnViewSuppliesIcon;
    public FontAwesomeIconView btnItemIcon;
    public FontAwesomeIconView btnDashBoardIcon;
    public FontAwesomeIconView btnCustomerIcon;
    public FontAwesomeIconView btnPlaceorderIcon;
    public FontAwesomeIconView btnRepairIcon;
    public FontAwesomeIconView btnViewPurchasesIcon;
    public FontAwesomeIconView btnStockIcon;
    public FontAwesomeIconView btnReportIcon;
    @FXML
    private AnchorPane anc;
    @FXML
    private BorderPane mainPane;
    @FXML
    private Button btnAdmin;
    @FXML
    private Button btnDashBoard;
    @FXML
    private Label date;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserRank;
    @FXML
    private VBox vbAdmin;
    @FXML
    private VBox vbCashier;
    private ArrayList<Button> buttonArrayList=new ArrayList<>();
    private ArrayList<FontAwesomeIconView>fxView=new ArrayList<>();
    public void initialize(){
        Project.setPane(mainPane);
        setProfileDetails();
        setButtonsArray();
        ActionEvent actionEvent=new ActionEvent();
        if (Project.isAdmin){
            btnAdmin.setVisible(true);
            adminOnAction(actionEvent);
        }else {
            btnAdmin.setVisible(false);
            goDashBoardOnAction(actionEvent);
        }
    }
    private void setProfileDetails() {
        lblUserName.setText(Project.loggerUserName);
        lblUserRank.setText(Project.loggerUserRank);
        date.setText(LocalDate.now().toString());
    }
    private void setButtonsArray() {
        buttonArrayList.add(btnLoginRecord);
        fxView.add(btnLoginRecordIcon);
        buttonArrayList.add(btnUser);
        fxView.add(btnUserIcon);
        buttonArrayList.add(btnEmployee);
        fxView.add(btnEmployeeIcon);
        buttonArrayList.add(btnSupplier);
        fxView.add(btnSupplierIcon);
        buttonArrayList.add(btnSupplies);
        fxView.add(btnSuppliesIcon);
        buttonArrayList.add(btnViewSupplies);
        fxView.add(btnViewSuppliesIcon);
        buttonArrayList.add(btnItem);
        fxView.add(btnItemIcon);
        buttonArrayList.add(btnDashBoard);
        fxView.add(btnDashBoardIcon);
        buttonArrayList.add(btnCustomer);
        fxView.add(btnCustomerIcon);
        buttonArrayList.add(btnPlaceorder);
        fxView.add(btnPlaceorderIcon);
        buttonArrayList.add(btnRepair);
        fxView.add(btnRepairIcon);
        buttonArrayList.add(btnViewPurchases);
        fxView.add(btnViewPurchasesIcon);
        buttonArrayList.add(btnStock);
        fxView.add(btnStockIcon);
        buttonArrayList.add(btnReport);
        fxView.add(btnReportIcon);
    }
    @FXML
    void adminOnAction(ActionEvent event) {
        vbCashier.setVisible(false);
        vbAdmin.setVisible(true);
        goLoginRecordAction(event);
        try {
            Navigation.navigate(Routes.ADMIN, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goCustomerOnAction(ActionEvent event) {
        changeSelectedButton(btnCustomer);
        try {
            Navigation.navigate(Routes.CUSTOMER, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goDashBoardOnAction(ActionEvent event) {
        vbCashier.setVisible(true);
        vbAdmin.setVisible(false);
        changeSelectedButton(btnDashBoard);
        try {
            Navigation.navigate(Routes.DASHBOARD, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goPlaceOrderOnAction(ActionEvent event) {
        changeSelectedButton(btnPlaceorder);
        try {
            Navigation.navigate(Routes.PLACE_ORDER, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goRepairOnAction(ActionEvent event) {
        changeSelectedButton(btnRepair);
        try {
            Navigation.navigate(Routes.REPAIR, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goReportsOnAction(ActionEvent event) {
        changeSelectedButton(btnReport);
        try {
            Navigation.navigate(Routes.REPORTS, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goStockOnAction(ActionEvent event) {
        changeSelectedButton(btnStock);
        try {
            Navigation.navigate(Routes.STOCK, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    public void goViewPurchasesOnAction(ActionEvent event) {
        changeSelectedButton(btnViewPurchases);
        try {
            Navigation.navigate(Routes.VIEW_PURCHASES, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goEmployerOnAction(ActionEvent event) {
        changeSelectedButton(btnEmployee);
        try {
            Navigation.navigate(Routes.EMPLOYEE, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goItemOnAction(ActionEvent event) {
        changeSelectedButton(btnItem);
        try {
            Navigation.navigate(Routes.ITEM, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goLoginRecordAction(ActionEvent event) {
        changeSelectedButton(btnLoginRecord);
        try {
            Navigation.navigate(Routes.ADMIN, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goSupplierOnAction(ActionEvent event) {
        changeSelectedButton(btnSupplier);
        try {
            Navigation.navigate(Routes.SUPPLIER, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goSuppliesOnAction(ActionEvent event) {
        changeSelectedButton(btnSupplies);
        try {
            Navigation.navigate(Routes.SUPPLIES, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void goUserOnAction(ActionEvent event) {
        changeSelectedButton(btnUser);
        try {
            Navigation.navigate(Routes.USER, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    public void goViewSuppliesOnAction(ActionEvent actionEvent) {
        changeSelectedButton(btnViewSupplies);
        try {
            Navigation.navigate(Routes.VIEW_SUPPLIES, anc);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    @FXML
    void logOutOnAction(ActionEvent event) throws IOException {
        try {
            Navigation.navigate(Routes.LOGIN,mainPane);
        } catch (IOException e) {
            Project.showError(Alert.AlertType.ERROR,"Ui Error!","check your Forms");
        }
    }
    public void exitOnAction(ActionEvent actionEvent) {
        Platform.exit();
    }
    void changeSelectedButton(Button selectedButton){
        for (Button button:buttonArrayList) {
            if (button.getId().equals(selectedButton.getId())){
                selectedButton.getStyleClass().removeAll("navigation-button");
                selectedButton.getStyleClass().addAll("selected-navigation-button");
            }else {
                button.getStyleClass().removeAll("selected-navigation-button");
                button.getStyleClass().addAll("navigation-button");
            }
        }
        for ( int i=0; i<buttonArrayList.size(); i++) {
            Button button=buttonArrayList.get(i);
            FontAwesomeIconView fxIcon=fxView.get(i);
            if (button.getId().equals(selectedButton.getId())){
                selectedButton.getStyleClass().removeAll("navigation-button");
                selectedButton.getStyleClass().addAll("selected-navigation-button");
                fxIcon.getStyleClass().removeAll("navigation-button");
                fxIcon.getStyleClass().addAll("selected-navigation-button");

            }else {
                button.getStyleClass().removeAll("selected-navigation-button");
                button.getStyleClass().addAll("navigation-button");
                fxIcon.getStyleClass().removeAll("selected-navigation-button");
                fxIcon.getStyleClass().addAll("navigation-button");
            }
        }

    }
}
