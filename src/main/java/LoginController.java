import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Created by alext on 2/13/2017.
 */
public class LoginController {

    @FXML
    private PasswordField passwordBox;
    @FXML
    private TextField textboxUsername;


    private String inputPassword = "";

    private ApplicationController applicationController;
    private Login login;

    LoginController(ApplicationController a){
        applicationController = a;
        login = new Login();
    }

    private boolean getCredentials(){
        return login.signIn(textboxUsername.getText(), passwordBox.getText());
    }

    public void signInMap(){
        if (getCredentials()) {
            applicationController.createMapAdminDisplay(login);
        }
    }

    public void signInDirectory(){
        if (getCredentials()){
            applicationController.createDirectoryAdminDisplay(login);
        }
    }



}
