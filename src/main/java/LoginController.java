import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by alext on 2/13/2017.
 */
public class LoginController {

    @FXML private PasswordField passwordBox;
    @FXML private TextField textboxUsername;
    @FXML private Label labelWrongCreds;
    @FXML private AnchorPane loginPage;
    @FXML private Button loginButton;

    private String inputPassword = "";

    private ApplicationController applicationController;
    private Login login;

    private Stage stage;

    LoginController(ApplicationController a, Stage s){
        applicationController = a;
        stage = s;
        login = new Login();
    }

    private boolean getCredentials(){
        return true;
        //return login.signIn(textboxUsername.getText(), passwordBox.getText());
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
            stage.hide();
            applicationController.createDirectoryAdminDisplay(login);
        } else {
            passwordBox.clear();
            labelWrongCreds.setVisible(true);
        }
    }
}
