package app.applicationControl.email;

import app.applicationControl.ApplicationController;
import app.dataPrimitives.GraphNode;
import app.dataPrimitives.Room;
import app.dataPrimitives.SubPath;
import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.BodyPart;
    import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMultipart;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class MessageHandler {
    EmailController emailController;
    Message message;
    Session session;

    final org.slf4j.Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    public MessageHandler(EmailController eController, Message message, Session session) {
        this.emailController = eController;
        this.message = message;
        this.session = session;
    }

    public void handleRealContent (String content) {

    }

    /**
     *  handle a given message
     * @throws Exception
     */
    public void handleMessage () throws Exception {
        Logger.logMsg(Logger.DEBUG, "Handling message");
        System.out.printf("Handling message");
        String from = message.getFrom()[0].toString();
        String realContent = getCleanMessage();
        String currentState = emailController.getState(from);
        System.out.println("realContent : " + realContent);

        if (realContent.toLowerCase().contains("help")) {
            // reply
            emailController.sendReply(message, "Hi " + from + " " + getHelp());
            return;
        }
        // give them directions again
        System.out.printf("not a help request");
        if (currentState != null && ! currentState.equals("help"))  {

            GraphNode start;
            Room closeRoom = emailController.map.getRoomFromName(realContent);
            if (closeRoom == null) {
                System.out.println("Close room is null --> search");
                List<String> closeRooms = emailController.map.subStringSearchRoom(realContent);
                for (String room : closeRooms) {
                    System.out.println("Close room : " + room);
                }
                if (closeRooms.size() > 0 ){
                    start = emailController.map.getRoomFromName(closeRooms.get(0)).getLocation();
                }
            }
            if (closeRoom == null) {
                System.out.println("Could not find the ");
                start = emailController.map.getKioskLocation();
            }

            else {
                start = closeRoom.getLocation();
            }

            GraphNode end = emailController.map.getRoomFromName(currentState).getLocation();
            List<GraphNode> path = emailController.map.getPath(start,end);
            List<String> textDirections = emailController.map.getTextualDirections(path);
            String content = getReadableDirections(textDirections);
            emailController.sendReply(message, content);
        }
        // TEMP
        else {
            System.out.println("Adding state");
            emailController.addState(from, "destination");
        }
    }

    public String getReadableDirections (List<String> unreadableDirs) {
        String content = unreadableDirs.stream().reduce("", (acu, el) -> {
            return acu += el + "\n";
        });
        return content;
    }

    /**
     * Get the help string
     * @return
     */
    public String getHelp() {
        return "Heres how to use this." ;
    }

    public String getCleanMessage () throws Exception {
        String messageContent = getTextFromMessage(message);
        // clean the garbage
        String realContent = getRealContent(messageContent);
        // if there is no garbage
        if (realContent.equals("")) {
            realContent = messageContent;
        }
        System.out.println("real content : " + realContent);
        Logger.logMsg(1,"Message content : " + messageContent);
        System.out.println("end of content");
        System.out.flush();
        return realContent;
    }

    /**
     * get plain text for message
     * @param message
     * @return
     * @throws Exception
     */
    private String getTextFromMessage(Message message) throws Exception {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    /**
     * Messages are complex. This does the parsing for you
     * @param mimeMultipart
     * @return message as text
     * @throws Exception
     */
    private String getTextFromMimeMultipart(
        MimeMultipart mimeMultipart) throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

    /**
     * Cleans a message for just the recent response
     * @param unparsedString
     * @return
     */
    public String getRealContent (String unparsedString) {
        String [] lines = unparsedString.split("\\n");
        String clean = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            // cut off the bottom
            if (line.contains("<") || line.contains("--")) {
                break;
            }
            clean += line;
        }
        return clean.replaceAll("\\s+","");
    }

}
