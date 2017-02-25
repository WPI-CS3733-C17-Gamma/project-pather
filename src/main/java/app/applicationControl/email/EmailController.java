package app.applicationControl.email;

import app.applicationControl.ApplicationController;
import app.datastore.Map;
import com.sun.media.jfxmedia.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
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

    public enum phoneCompanies{
        ATT,            //AT&T: number@txt.att.net
        TMOBILE,        //T-Mobile: number@tmomail.net
        VERIZON,        //Verizon: number@vzwpix.com (text + photo)
        SPRINT,         //Sprint: number@messaging.sprintpcs.com
        VIRGIN,         //Virgin Mobile: number@vmobl.com
        TRAC,           //Tracfone: number@mmst5.tracfone.com
        METRO,          //Metro PCS: number@mymetropcs.com
        BOOST,          //Boost Mobile: number@myboostmobile.com
        CRICKET,        //Cricket: number@mms.cricketwireless.net
        PTEL,           //Ptel: number@ptel.com
        REPUBLIC,       //Republic Wireless: number@text.republicwireless.com
        GOOGLE,         //Google Fi (Project Fi): number@msg.fi.google.com
        SUNCOM,         //Suncom: number@tms.suncom.com
        TING,           //Ting: number@message.ting.com
        US,             //U.S. Cellular: number@email.uscc.net
        CONSUMER,       //Consumer Cellular: number@cingularme.com
        CSPIRE,         //C-Spire: number@cspire1.com
        PAGE            //Page Plus: number@vtext.com
    }

    private HashMap<phoneCompanies, String> phone2Email = new HashMap<>();

    private void addCompanies(){
        phone2Email.put(phoneCompanies.ATT, "@txt.att.net");
        phone2Email.put(phoneCompanies.TMOBILE, "@tmomail.net");
        phone2Email.put(phoneCompanies.VERIZON, "@vzwpix.com");
        phone2Email.put(phoneCompanies.SPRINT, "@messaging.sprintpcs.com");
        phone2Email.put(phoneCompanies.VIRGIN, "@vmobl.com");
        phone2Email.put(phoneCompanies.TRAC, "@mmst5.tracfone.com");
        phone2Email.put(phoneCompanies.METRO, "@mymetropcs.com");
        phone2Email.put(phoneCompanies.BOOST, "@myboostmobile.com");
        phone2Email.put(phoneCompanies.CRICKET, "@mms.cricketwireless.net");
        phone2Email.put(phoneCompanies.PTEL, "@ptel.com");
        phone2Email.put(phoneCompanies.REPUBLIC, "@text.republicwireless.com");
        phone2Email.put(phoneCompanies.GOOGLE, "@msg.fi.google.com");
        phone2Email.put(phoneCompanies.SUNCOM, "@tms.suncom.com");
        phone2Email.put(phoneCompanies.TING, "@message.ting.com");
        phone2Email.put(phoneCompanies.US, "@email.uscc.net");
        phone2Email.put(phoneCompanies.CONSUMER, "@cingularme.com");
        phone2Email.put(phoneCompanies.CSPIRE, "@cspire1.com");
        phone2Email.put(phoneCompanies.PAGE, "@vtext.com");
    }


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
        addCompanies();
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


    boolean sendText(double number, phoneCompanies carrier,  String message){

        String email = getPhoneEmail(carrier);
        return send(number+email, message);

    }

    private String getPhoneEmail(phoneCompanies carrier){
        return phone2Email.get(carrier);
    }




}
