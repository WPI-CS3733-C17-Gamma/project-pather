package app.display;

import app.applicationControl.ApplicationController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * Created by alext on 2/20/2017.
 */
public class AdminToolsController {

    @FXML
    ToggleButton togglebuttonAddUser;
    @FXML
    ToggleButton togglebuttonChangePassword;
    @FXML
    TextField textfieldUsername;
    @FXML
    PasswordField passwordfieldCurrent;
    @FXML
    PasswordField passwordfieldNew;
    @FXML
    PasswordField passwordfieldNewAgain;

    ApplicationController applicationController;

    AdminToolsController(ApplicationController applicationController){
        this.applicationController = applicationController;
    }


}
