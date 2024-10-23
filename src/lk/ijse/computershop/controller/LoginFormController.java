package lk.ijse.computershop.controller;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import lk.ijse.computershop.model.LoginRecordModel;
import lk.ijse.computershop.model.UserModel;
import lk.ijse.computershop.to.LoginRecord;
import lk.ijse.computershop.to.User;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lk.ijse.computershop.util.Navigation;
import lk.ijse.computershop.util.Project;
import lk.ijse.computershop.util.Routes;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class LoginFormController{
    @FXML
    public AnchorPane loginForm;
    public TextField txtUsername;
    public TextField txtPassword;
    public PasswordField psPassword;
    public Pane loginPane;
    public Pane mainPane;
    public Label lblAdmin;
    public Label lblCashier;
    public JFXButton btnUser;
    public Label lblErrors;
    public FontAwesomeIconView fxIconEye;
    public HBox lblAdminPasswordReset;
    public HBox lblCashierSignUp;
    public Pane signUpPane;
    public PasswordField txtSignUpOnePassword;
    public Label lblUserId;
    public TextField txtSignUpUserName;
    public TextField txtSignUpNic;
    public TextField txtSignUpTelNum;
    public TextField txtSignUpEmail;
    public Label lblSignUpError;
    public PasswordField txtSignUpVerifyPassword;
    boolean isAdmin = true;
    private User user = null;
    private boolean isPasswordHide = true;

    public void initialize() {
        signUpPane.setVisible(false);
        lblCashier.setVisible(false);
        txtPassword.setVisible(false);
        lblAdminPasswordReset.setVisible(false);
        lblCashierSignUp.setVisible(false);
        btnUserOnAction(new ActionEvent());
    }

    public void logInOnAction(ActionEvent actionEvent) {
        lblErrors.setText("");
        String userName = txtUsername.getText();
        String userPassword = isPasswordHide ? psPassword.getText() : txtPassword.getText();
        String error = "";
        if (userName.isEmpty() && userPassword.isEmpty()){
            error = "Please Enter User Name and Password";
            txtUsername.setStyle("-fx-border-color:  #ff0000;");
            psPassword.setStyle("-fx-border-color:  #ff0000;");
            txtPassword.setStyle("-fx-border-color:  #ff0000;");
            txtUsername.requestFocus();
        }else if (userName.isEmpty()) {
            error = "Please Enter User Name";
            txtUsername.setStyle("-fx-border-color:  #ff0000;");
            txtUsername.requestFocus();
        }else if (userPassword.isEmpty()) {
            error = "Please Enter Password";
            psPassword.setStyle("-fx-border-color:  #ff0000;");
            txtPassword.setStyle("-fx-border-color:  #ff0000;");
            if (isPasswordHide) {
                psPassword.requestFocus();
            } else {
                txtPassword.requestFocus();
            }
        }else{
            if (isAdmin) {
                if (verification(userName, userPassword, "ADMIN")) {
                    try {
                        if (LoginRecordModel.saveLogin(new LoginRecord(LoginRecordModel.getNextLoginId(), LocalDateTime.now(), user.getUserId()))) {
                            Project.loggerUserName = userName;
                            Project.loggerUserRank = "Admin";
                            Project.isAdmin = true;
                            Navigation.navigate(Routes.MAIN, loginForm);
                        } else {
                            Project.showError(Alert.AlertType.ERROR, "Data Base Error", "check your databases");
                        }
                    } catch (IOException | SQLException | ClassNotFoundException e) {
                        Project.showError(Alert.AlertType.ERROR, "Data Base or Forms Error", e.toString());
                    }
                } else if (verification(userName, userPassword, "CASHIER")) {
                    error = "Your Rank is Cashier,Please Select Cashier and Try again";
                    btnUser.setStyle("-fx-border-color: #ff0000;");
                } else {
                    error = "User Name or Password Wrong";
                    txtUsername.setStyle("-fx-border-color:  #ff0000;");
                    psPassword.setStyle("-fx-border-color:  #ff0000;");
                    txtPassword.setStyle("-fx-border-color:  #ff0000;");
                }
            } else {
                if (verification(userName, userPassword, "CASHIER")) {
                    try {
                        if (LoginRecordModel.saveLogin(new LoginRecord(LoginRecordModel.getNextLoginId(), LocalDateTime.now(), user.getUserId()))) {
                            Project.loggerUserName = userName;
                            Project.loggerUserRank = "Cashier";
                            Project.isAdmin = false;
                            Navigation.navigate(Routes.MAIN, loginForm);
                        } else {
                            Project.showError(Alert.AlertType.ERROR, "Data Base Error", "Check Data Base");
                        }
                    } catch (IOException | SQLException | ClassNotFoundException e) {
                        Project.showError(Alert.AlertType.ERROR, "Data Base or Forms Error", e.toString());
                    }
                } else if (verification(userName, userPassword, "ADMIN")) {
                    error = "Your Rank is Admin,Please Select Admin and Try again";
                    btnUser.setStyle("-fx-border-color: #ff0000;");
                } else {
                    error = "User Name or Password Wrong";
                    txtUsername.setStyle("-fx-border-color:  #ff0000;");
                    psPassword.setStyle("-fx-border-color:  #ff0000;");
                    txtPassword.setStyle("-fx-border-color:  #ff0000;");
                }
            }
        }
        lblErrors.setText(error);
    }

    private boolean verification(String userName, String userPassword, String rank) {
        try {
            user = UserModel.searchUserName(userName);
        } catch (SQLException | ClassNotFoundException e) {
            Project.showError(Alert.AlertType.ERROR,"Data Base or Forms Error",e.toString() );
        }
        if (user != null && user.getUserPassword().equals(userPassword) && user.getRank().equals(rank)) {
            return true;
        } else {
            return false;
        }
    }

    public void btnUserOnAction(ActionEvent actionEvent) {
        btnUser.setStyle("-fx-border-color: #f3f2f2;");

        if (isAdmin) {
            btnUser.setText("Admin");
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(loginPane);
            slide.play();
            slide.setOnFinished((e -> {
                loginPane.setStyle("-fx-background-radius: 20px 0px 0px 20px;");
                isAdmin = false;
                lblAdmin.setVisible(false);
                lblCashier.setVisible(true);
                lblCashierSignUp.setVisible(true);
                lblAdminPasswordReset.setVisible(false);
                change();
            }));
            TranslateTransition slide2 = new TranslateTransition();
            slide2.setDuration(Duration.seconds(0.8));
            slide2.setNode(loginPane);
            slide2.setToX(-800);
            slide2.play();
        } else {
            btnUser.setText("Cashier");
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(loginPane);
            slide.play();
            slide.setOnFinished((e -> {
                loginPane.setStyle("-fx-background-radius: 0px 20px 20px 0px;");
                isAdmin = true;
                lblAdmin.setVisible(true);
                lblCashier.setVisible(false);
                lblAdminPasswordReset.setVisible(true);
                lblCashierSignUp.setVisible(false);
                change();
            }));
            TranslateTransition slide2 = new TranslateTransition();
            slide2.setDuration(Duration.seconds(0.8));
            slide2.setNode(loginPane);
            slide2.setToX(0);
            slide2.play();
        }
    }
    void change(){
        txtUsername.setStyle("-fx-border-color:  #38b000;");
        psPassword.setStyle("-fx-border-color:  #38b000;");
        txtPassword.setStyle("-fx-border-color:  #38b000;");
        lblErrors.setText(null);
    }
    public void exitOnAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void userNameKeyPressedOnAction(KeyEvent keyEvent) {
       if (!txtUsername.getText().isEmpty()){
           txtUsername.setStyle("-fx-border-color:  #38b000;");
       }
    }
    public void passwordKeyPressedOnAction(KeyEvent keyEvent) {
        if (isPasswordHide) {
            if (!psPassword.getText().isEmpty()){
                psPassword.setStyle("-fx-border-color:  #38b000;");
            }
        } else {
            if (!txtPassword.getText().isEmpty()){
                txtPassword.setStyle("-fx-border-color:  #38b000;");
            }
        }
    }
    public void click(MouseEvent event) {
        btnUser.setStyle("-fx-border-color: #f3f2f2;");
    }

    public void showHidePasswordOnAction(ActionEvent actionEvent) {
        if (isPasswordHide){
            isPasswordHide=false;
            txtPassword.setText(psPassword.getText());
            fxIconEye.setStyle("-glyph-name:EYE;");
            psPassword.setVisible(false);
            txtPassword.setVisible(true);
        }else {
            isPasswordHide=true;
            psPassword.setText(txtPassword.getText());
            fxIconEye.setStyle("-glyph-name:EYE_SLASH;");
            txtPassword.setVisible(false);
            psPassword.setVisible(true);
        }
    }

    public void passwordOnAction(ActionEvent actionEvent) {
        logInOnAction(actionEvent);
    }

    public void userNameOnAction(ActionEvent actionEvent) {
        if (isPasswordHide){
            psPassword.requestFocus();
        }else {
            txtPassword.requestFocus();
        }
    }

    public void ResetPasswordOnAction(MouseEvent mouseEvent) {
        System.out.println("reset");
    }

    public void signUpOnAction(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        lblUserId.setText(UserModel.getNextUserId());
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(signUpPane);
        slide.play();
        slide.setOnFinished((e -> {
            loginPane.setDisable(true);
            signUpPane.setVisible(true);
            btnUser.setVisible(false);
        }));
        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(0.8));
        slide2.setNode(signUpPane);
        slide2.setToX(400);
        slide2.play();
    }

    public void txtSignUpOnePasswordOnACtion(ActionEvent actionEvent) {
    }

    public void txtSignUpUserNameOnAction(ActionEvent actionEvent) {
    }

    public void txtSignUpNicOnAction(ActionEvent actionEvent) {
    }

    public void txtSignUpTelNumOnAction(ActionEvent actionEvent) {
    }

    public void goBackOnAction(MouseEvent mouseEvent) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(signUpPane);
        slide.play();
        slide.setOnFinished((e -> {
            loginPane.setDisable(false);
            signUpPane.setVisible(false);
            btnUser.setVisible(true);
        }));
        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(0.8));
        slide2.setNode(signUpPane);
        slide2.setToX(-400);
        slide2.play();
    }

    public void txtSignUpVerifyPasswordOnAction(ActionEvent actionEvent) {
    }

    public void txtSignUpVerifyPasswordKey(KeyEvent keyEvent) {
    }

    public void txtSignUpEmailKey(KeyEvent keyEvent) {
    }

    public void txtSignUpEmailOnAction(ActionEvent actionEvent) {
    }

    public void txtSignUpTelNumKey(KeyEvent keyEvent) {
    }

    public void txtSignUpNicKey(KeyEvent keyEvent) {
    }

    public void txtSignUpUserNameKey(KeyEvent keyEvent) {

    }

    public void txtSignUpOnePasswordKey(KeyEvent keyEvent) {
    }
}
