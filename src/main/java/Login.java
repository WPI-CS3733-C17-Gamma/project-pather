import java.net.PasswordAuthentication;

/**
 * Created by alext on 2/12/2017.
 */
public class Login {
    private String uname = "admin";
    private String passwd = "admin";//will change eventually
    private char[] charPassword = passwd.toCharArray();

    private boolean requiredSignin;
    private boolean isSignedIn;

    private PasswordAuthentication passwordAuthentication;


    Login(){
        System.out.println("login made");
        passwordAuthentication = new PasswordAuthentication(uname, charPassword);
        requiredSignin = true;     //change to true after development and when testing
        isSignedIn = false;
    }

    public boolean signIn(String uname, String password){
        System.out.println(uname + password);
        if (!requiredSignin){
            return true;
        }
        if (uname.equals(this.uname) && password.equals(this.passwd)){
            System.out.println("is a real fucker");
            return true;
        }
        System.out.println("is wrong");
        return false;
    }

    public boolean signOut(){
        isSignedIn = false;
        return true;
    }
}
