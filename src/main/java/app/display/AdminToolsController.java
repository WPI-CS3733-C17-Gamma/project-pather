package app.display;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 * Created by alext on 2/20/2017.
 */
public class AdminToolsController extends DisplayController{

    @FXML ToggleButton togglebuttonAddUser;
    @FXML ToggleButton togglebuttonChangePassword;
    @FXML TextField textfieldUsername;
    @FXML PasswordField passwordfieldCurrent;
    @FXML PasswordField passwordfieldNew;
    @FXML PasswordField passwordfieldNewAgain;
    @FXML Button buttonSave;
    @FXML Label labelErrors;

    /**
     * runs when Change Password button is pressed
     */
    public void togglebuttonPressedChange() {
        if(togglebuttonChangePassword.isSelected()){
            clearInputs();
            textfieldUsername.setDisable(false);
            passwordfieldCurrent.setDisable(false);
            passwordfieldNew.setDisable(false);
            passwordfieldNewAgain.setDisable(false);
            togglebuttonAddUser.setSelected(false);

        } else {
            disableInputs();
        }
    }

    /**
     * runs when Add Admin button is pressed
     */
    public void togglebuttonPressedAdd(){
        if (togglebuttonAddUser.isSelected()){
            clearInputs();
            textfieldUsername.setDisable(false);
            passwordfieldCurrent.setDisable(true);
            passwordfieldNew.setDisable(false);
            passwordfieldNewAgain.setDisable(false);
            togglebuttonChangePassword.setSelected(false);

        } else {
            disableInputs();
        }
    }

    /**
     * clears inputs
     */
    private void clearInputs(){
        textfieldUsername.clear();
        passwordfieldCurrent.clear();
        passwordfieldNew.clear();
        passwordfieldNewAgain.clear();
    }

    /**
     * disables inputs
     */
    private void disableInputs(){
        textfieldUsername.setDisable(true);
        passwordfieldCurrent.setDisable(true);
        passwordfieldNew.setDisable(true);
        passwordfieldNewAgain.setDisable(true);
    }

    /**
     * Enables the submit button if all necessary fields are not full
     */
    public void checkifFull(){
        if (togglebuttonAddUser.isSelected()){
            if( !textfieldUsername.getText().equals("")&&
                !passwordfieldNew.getText().equals("")&&
                !passwordfieldNewAgain.getText().equals("")){
                buttonSave.setDisable(false);
            }
        } else if (togglebuttonChangePassword.isSelected()){
            if (!textfieldUsername.getText().equals("")&&
                !passwordfieldCurrent.getText().equals("")&&
                !passwordfieldNew.getText().equals("")&&
                !passwordfieldNewAgain.getText().equals("")){
                buttonSave.setDisable(false);
            }
        }
    }

    /**
     * Submits changes
     */
    public void submit(){
        if (togglebuttonAddUser.isSelected()){
            addUser();
        } else if(togglebuttonChangePassword.isSelected()){
            changePassword();
        } else {
            buttonSave.setDisable(true);
            clearInputs();
        }
    }

    /**
     * Adds user
     */
    public void addUser(){
        String uname = textfieldUsername.getText();
        String passwd;
        if (passwordfieldNew.getText().equals(passwordfieldNewAgain.getText())) {
            passwd = passwordfieldNew.getText();
            if(applicationController.addUser(uname, passwd)){
                labelErrors.setText("User added Successfully");
                labelErrors.setTextFill(Color.GREEN);
                labelErrors.setVisible(true);
            }
        } else {
            labelErrors.setText("Passwords not matching");
            labelErrors.setTextFill(Color.RED);
            labelErrors.setVisible(true);
        }
    }

    /**
     * changes passwords given a username
     */
    public void changePassword(){
        String uname = textfieldUsername.getText();
        String passwd = passwordfieldCurrent.getText();
        String passwdNew = passwordfieldNew.getText();
        String passwdNewAgain = passwordfieldNewAgain.getText();
        boolean correctLogin = applicationController.login(uname, passwd);
        if (!correctLogin){
            labelErrors.setText("Incorrect Username or Password");
            labelErrors.setTextFill(Color.RED);
            labelErrors.setVisible(true);
        } else if (!passwdNew.equals(passwdNewAgain)){
            labelErrors.setText("Passwords not matching");
            labelErrors.setTextFill(Color.RED);
            labelErrors.setVisible(true);
        } else if (passwdNew.equals("") || passwdNewAgain.equals("")){
            labelErrors.setText("Password field cannot be empty");
            labelErrors.setTextFill(Color.RED);
            labelErrors.setVisible(true);
        } else if (correctLogin){
            applicationController.changePassword(uname, passwd, passwdNew);
            labelErrors.setText("New Password Saved Successfully");
            labelErrors.setTextFill(Color.GREEN);
            labelErrors.setVisible(true);
        }
    }

    /**
     * hides the label and disables the save button
     */
    public void hideLabel(){
        labelErrors.setText("");
        labelErrors.setTextFill(Color.RED);
        labelErrors.setVisible(false);
        buttonSave.setDisable(true);
    }


}
