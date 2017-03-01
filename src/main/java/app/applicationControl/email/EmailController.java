package app.applicationControl.email;

import app.applicationControl.ApplicationController;
import app.datastore.Map;
import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class EmailController {

    final org.slf4j.Logger logger = LoggerFactory.getLogger(EmailController.class);
    ApplicationController applicationController;
    Map map;
    String username;
    String password;
    Session session;
    EmailPoller poller;
    int runs;

    volatile HashMap <String, String > conversationState = new HashMap();

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
        PAGE,           //Page Plus: number@vtext.com
        EMAIL           //default decision
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
        this.applicationController = applicationController;
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

    /**
     * Send a message to the given email
     * @param to
     * @param messageContents
     * @return
     */
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
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parse string and check for errors in email/phone
     * @param to
     * @return
     */
    private String checkInput(String to){
       return to;
    }

    /**
     * Send direcitons to the given person
     * @param to email (email + number) of given person
     * @param directions direcitons as a list of strings to be sent for each floor
     * @param destination destination as a string
     * @return
     */
    public boolean sendDirections (String to, List<String> directions, String destination) {
        String content = directions.stream().reduce("", (acummulator, element) -> {
            acummulator += element + "\n";
            return acummulator;
        });
        addState(to, destination);
        return send(to, content);
    }

    public boolean sendTextDirections (String number, phoneCompanies carrier,
            List<String> dir,String Destination ) {
        return sendDirections(number + getPhoneEmail(carrier), dir, Destination);
    }


    /**
       @param message message to reply to
       @param contents contents to send to
       @return true if the send is successful
    */
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

    /**
	@param person person who was sent a messaeg
	@param state state the person is in.
    */
    public synchronized void addState (String person, String state) {
	    conversationState.put(person, state);
    }

    /**
       get the state map for the given person
       @param person the email of the person
    */
    public String getState (String person ) {
        return conversationState.get(person);
    }

    /**
     * Send text
     * @param number
     * @param carrier
     * @param message
     * @return
     */
    public boolean sendText(double number, phoneCompanies carrier,  String message){
        String email = getPhoneEmail(carrier);
        return send(number+email, message);

    }

    /**
     * Get phone number email based on carrier
     * @param carrier
     * @return
     */
    public String getPhoneEmail(phoneCompanies carrier){
        return phone2Email.get(carrier);
    }




}
