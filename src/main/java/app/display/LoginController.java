package app.display;

import app.applicationControl.ApplicationController;
import app.applicationControl.Login;
import app.datastore.Map;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by alext on 2/13/2017.
 */
public class LoginController extends DisplayController {

    @FXML private PasswordField passwordBox;
    @FXML private TextField textboxUsername;
    @FXML private Label labelWrongCreds;
    @FXML private AnchorPane loginPage;
    @FXML private Button loginButton;
    @FXML private Button buttonAdminTools;

    private String inputPassword = "";

    private ApplicationController applicationController;

    private Stage stage;

    public LoginController(Map map, ApplicationController a, Stage s){
        super(map, a);
        applicationController = a;
        stage = s;
    }

    /**
     * Checks credentials
     * @return
     * true if credentials are correct
     * false if credentials are incorrect
     */
    private boolean getCredentials(){
        return login(textboxUsername.getText(), passwordBox.getText());
    }

    public void isSelected(){       //for auto-disapearing
        labelWrongCreds.setVisible(false);
    }

    public void showAdminTools(){
        if (getCredentials()){
            loginPage.setVisible(false);
            hideStage(stage);
            createAdminTools();
        } else {
            passwordBox.clear();
            labelWrongCreds.setVisible(true);
        }
    }

    /**
     * Displays options for map and directory admin
     */
    public void showAdminMenu(){
        if (getCredentials()) {
            loginPage.setVisible(false);
            hideStage(stage);
            createDirectoryAdminDisplay();
        } else {
            passwordBox.clear();
            labelWrongCreds.setVisible(true);
        }
    }
}
