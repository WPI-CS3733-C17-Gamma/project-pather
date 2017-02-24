package app.applicationControl.email;

import app.applicationControl.ApplicationController;
import app.datastore.Map;
import com.sun.media.jfxmedia.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class EmailController {

    ApplicationController applicationController;
    Map map;
    String username;
    String password;
    Session session;
    EmailPoller poller;
    int runs;

    /**
     * create new application
     * @param applicationController
     * @param map
     * @param username
     * @param password
     */
    public EmailController (ApplicationController applicationController, Map map, String username, String password, int runs){
        this.applicationController =applicationController;
        this.map = map;
        this.username = username;
        this.password = password;
        this.runs = runs;
    }

    /**
     * simpler constructor defaults email and password
     * @param applicationController
     * @param map
     */
    public EmailController (ApplicationController applicationController, Map map){
        this (applicationController, map, "projectpather@gmail.com", "ProjectPatherGamma", 30);
    }


    /**
     * Start the polling system
     */
    public void start() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);

                }

            });
        poller = new EmailPoller(this, username, password, session);
        poller.start();
        System.out.println("did return");
    }

    /**
     * interupt the child thread
     */
    public void stop() {
        poller.isRunning = false;
        poller.interrupt();
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
