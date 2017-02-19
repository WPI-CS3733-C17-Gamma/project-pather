package texting;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


/**
 * Singleton class to send messages to the given number
 */
public class TextSender {
    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = "ACc66abeeb6305138885e7991748beed5b";
    public static final String AUTH_TOKEN = "39dcf1af88bab3d3dbc54a7188d6a3f0";

    private static TextSender instance = null;

    protected TextSender () {

    }

    /**
     * Get the singleton instance of the TextSender
     * @return
     */
    public static TextSender getInstance() {
	if (instance == null) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	    instance = new TextSender();
	}
	return instance;
    }

    /**
     * Send a message to the given number
     * @param receiverNumber : number in the format "+{country code}{number}
     * @param body : any string
     * @return
     */
    public String sendMessage (String receiverNumber , String body) {
        // create and send message
        Message message = Message
            .creator(new PhoneNumber(receiverNumber), new PhoneNumber("+19788505030"),
                body)
            .create();
       return message.getSid();

    }

    public static void main(String[] args) {
        TextSender sender = TextSender.getInstance();
        sender.sendMessage("+19787601330", "hello world");

    }
}
