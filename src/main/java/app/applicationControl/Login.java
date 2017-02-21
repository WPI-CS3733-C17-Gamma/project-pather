package app.applicationControl;

import java.net.PasswordAuthentication;
import java.util.HashMap;

/**
 * Created by alext on 2/12/2017.
 */
public class Login {
    private boolean requiredSignin;
    private boolean isSignedIn;

    private HashMap<String, String> passwordStorage;    //Stores all passwords and username combinations

    Login(){
        passwordStorage = new HashMap<>();
        passwordStorage.put("admin", "admin");
        requiredSignin = true;     //change to true after development and when testing
        isSignedIn = false;
    }

    boolean signIn(String uname, String password){

        if (!requiredSignin){
            return true;
        } else if (passwordStorage.get(uname).equals(password)){
            return true;
        }
        return false;
    }

    boolean signOut(){
        isSignedIn = false;
        return true;
    }

    boolean isSignedIn(){
        return isSignedIn;
    }

    boolean addUser(String uname, String passwd){
        if (passwordStorage.put(uname, passwd).equals(passwd)) {
            return true;
        }
        return false;
    }

    boolean changePassword(String uname, String oldPasswd, String newPasswd){
        return passwordStorage.replace(uname, oldPasswd, newPasswd);
    }
}
