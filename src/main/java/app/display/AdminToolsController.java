package app.display;

import app.applicationControl.ApplicationController;
import app.datastore.Map;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * Created by alext on 2/20/2017.
 */
public class AdminToolsController extends DisplayController{

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

    public AdminToolsController(Map map, ApplicationController applicationController){
        super(map, applicationController);
        this.applicationController = applicationController;
    }


}
