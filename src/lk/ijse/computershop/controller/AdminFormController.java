package lk.ijse.computershop.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lk.ijse.computershop.model.AdminModel;
import lk.ijse.computershop.model.LoginRecordModel;
import lk.ijse.computershop.model.UserModel;
import lk.ijse.computershop.tm.LoginTM;
import lk.ijse.computershop.to.LoginRecord;
import lk.ijse.computershop.to.User;
import lk.ijse.computershop.util.Navigation;
import lk.ijse.computershop.util.Project;
import lk.ijse.computershop.util.Routes;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class AdminFormController {

    @FXML
    private AnchorPane anc;

    @FXML
    private TableColumn<LoginTM, LocalDateTime> colDateTime;

    @FXML
    private TableColumn<LoginTM, String> colLoginId;

    @FXML
    private TableColumn<LoginTM, String> colRank;

    @FXML
    private TableColumn<LoginTM, String> colTelNum;

    @FXML
    private TableColumn<LoginTM, String> colUserId;

    @FXML
    private TableColumn<LoginTM, String> colUserName;

    @FXML
    private TableView<LoginTM> tblLogin;
    public void initialize(){
        loadTable();
    }

    private void loadTable() {
        ObservableList<LoginTM>loginTMS= FXCollections.observableArrayList();
        colLoginId.setCellValueFactory((new PropertyValueFactory<>("loginId")));
        colUserId.setCellValueFactory((new PropertyValueFactory<>("userId")));
        colUserName.setCellValueFactory((new PropertyValueFactory<>("userName")));
        colDateTime.setCellValueFactory((new PropertyValueFactory<>("dateTime")));
        colTelNum.setCellValueFactory((new PropertyValueFactory<>("telephoneNumber")));
        colRank.setCellValueFactory((new PropertyValueFactory<>("rank")));
        try {
            ArrayList<LoginRecord>loginRecordArrayList= LoginRecordModel.getAllLogin();
            for (LoginRecord loginRecord:loginRecordArrayList) {
                User user=UserModel.searchUserId(loginRecord.getUserId());
                loginTMS.add(new LoginTM(
                        loginRecord.getLoginId(),
                        loginRecord.getUserId(),
                        user.getUserName(),
                        loginRecord.getDateTime(),
                        user.getTelephoneNumber(),
                        user.getRank()
                ));
            }
            tblLogin.setItems(loginTMS);
        } catch (SQLException | ClassNotFoundException e) {
            ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Data Not Loading", e + ",Data Not Loading !\nYou Want Exit ? ");
            if (result==ButtonType.OK){
                Platform.exit();
            }
        }
    }

    public void factoryResetOnAction(ActionEvent actionEvent) {
        ButtonType result = Project.showError(Alert.AlertType.CONFIRMATION, "Factory reset", "All data in the system will be lost\nare you sure ? ");
        if (result==ButtonType.OK){
            try {
                if(AdminModel.resetAll()){
                    Project.showError(Alert.AlertType.INFORMATION, "Factory reset successes","clear all data form system \nuser name password rested");
                    Navigation.navigate(Routes.LOGIN,Project.getPane());
                }else {
                    Project.showError(Alert.AlertType.ERROR, "Factory reset failed","something wrong");
                }
            } catch (IOException | SQLException | ClassNotFoundException e) {
                Project.showError(Alert.AlertType.ERROR, "something wrong",e+"");
            }
        }
    }
}
