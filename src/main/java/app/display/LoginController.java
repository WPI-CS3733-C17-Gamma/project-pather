package app.display;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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

    /**
     * Checks credentials
     * @return
     * true if credentials are correct
     * false if credentials are incorrect
     */
    private boolean getCredentials(){
        return true;//login(textboxUsername.getText(), passwordBox.getText());
    }

    public void isSelected(){       //for auto-disapearing
        labelWrongCreds.setVisible(false);
    }

    /**
     * Displays options for map and directory admin
     */
    public void showAdminMenu(){
        if (getCredentials()) {
            loginPage.setVisible(false);
            hideStage(stage);
            createAdminDisplay();
        } else {
            passwordBox.clear();
            labelWrongCreds.setVisible(true);
        }
    }
}
