import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by alext on 2/13/2017.
 */
public class LoginController {

    @FXML
    private PasswordField passwordBox;
    @FXML
    private TextField textboxUsername;
    @FXML
    private Label labelWrongCreds;


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
        return login.signIn(textboxUsername.getText(), passwordBox.getText());
    }

    public void signInMap(){
        if (getCredentials()) {
            stage.hide();
            applicationController.createMapAdminDisplay(login);
        } else {
            passwordBox.clear();
            labelWrongCreds.setVisible(true);
        }
    }

    public void signInDirectory(){
        if (getCredentials()){
            stage.hide();
            applicationController.createDirectoryAdminDisplay(login);

        } else {
            passwordBox.clear();
            labelWrongCreds.setVisible(true);
        }
    }

    public void isSelected(){       //for auto-disapearing
            labelWrongCreds.setVisible(false);
    }



}