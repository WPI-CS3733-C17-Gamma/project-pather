package app.Email;

import app.applicationControl.ApplicationController;
import app.datastore.Map;
import com.sun.media.jfxmedia.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class EmailController {

    ApplicationController applicationController;
    Map map;
    String username;
    String password;

    Session session;

    /**
     * create new application
     * @param applicationController
     * @param map
     * @param username
     * @param password
     */
    public EmailController (ApplicationController applicationController, Map map, String username, String password){
        this.applicationController =applicationController;
        this.map = map;
        this.username = username;
        this.password = password;
    }

    /**
     * simpler constructor defaults email and password
     * @param applicationController
     * @param map
     */
    public EmailController (ApplicationController applicationController, Map map){
        this (applicationController, map, "projectpather@gmail.com", "ProjectPatherGamma");
    }

    public void start() {

    }

    public boolean send (String to, String messageContents) {

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("projectpather@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
            message.setSubject("");
            message.setText(messageContents);
            Transport.send(message);
            System.out.println("Sent");
            return true;
        }
        catch (Exception e) {
            Logger.logMsg(0,e.toString());
            return false;
        }
    }

    public boolean sendReply (Message message, String contents) {
        try {
            Message reply = message.reply(false);
            reply.setFrom(new InternetAddress("projectpather@gmail.com"));
            reply.setText(contents);
            Transport.send(reply);
            return true;
    }
        catch   (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
