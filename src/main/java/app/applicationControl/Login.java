package app.applicationControl;

import java.util.HashMap;
import org.mindrot.jbcrypt.*;

/**
 * Created by alext on 2/12/2017.
 */
public class Login {

    private boolean requiredSignin;
    private boolean isSignedIn;

    private HashMap<String, String> passwordStorage = new HashMap<>();    //Stores all passwords and username combinations

    Login() {
        String password = "admin";
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        passwordStorage.put("admin", hashed);
        requiredSignin = true;     //change to true after development and when testing
        isSignedIn = false;
    }

    /**
     * Signs in the user
     * @param uname
     * @param password
     * @return
     */
    boolean signIn(String uname, String password) {
        if (!requiredSignin) {
            return true;
        }
        try {
            if (BCrypt.checkpw(password, passwordStorage.get(uname))) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * signs out the user
     * @return
     */
    boolean signOut(){
        isSignedIn = false;
        return true;
    }

    /**
     * returns if user is signed in
     * @return
     */
    boolean isSignedIn(){
        return isSignedIn;
    }

    /**
     * creates a new admin user
     * @param uname
     * @param passwd
     * @return
     */
    boolean addUser(String uname, String passwd){
        String hashed = BCrypt.hashpw(passwd, BCrypt.gensalt(12));
        passwordStorage.put(uname, hashed);
            return true;
    }

    /**
     * changes the password of a user
     * @param uname
     * @param oldPasswd
     * @param newPasswd
     * @return
     */
    boolean changePassword(String uname, String oldPasswd, String newPasswd){
        String oldHashed = BCrypt.hashpw(oldPasswd, BCrypt.gensalt(12));
        String newHashed = BCrypt.hashpw(newPasswd, BCrypt.gensalt(12));
        return passwordStorage.replace(uname, oldHashed, newHashed);
    }
}
