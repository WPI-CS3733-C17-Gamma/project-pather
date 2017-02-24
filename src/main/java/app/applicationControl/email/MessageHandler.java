package app.applicationControl.email;

import com.sun.media.jfxmedia.logging.Logger;

import javax.mail.BodyPart;
    import javax.mail.Message;
    import javax.mail.Session;
    import javax.mail.internet.MimeMultipart;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class MessageHandler {
    EmailController emailController;
    Message message;
    Session session;

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
        String from = message.getFrom()[0].toString();
        Logger.logMsg(1, "From: " + from);
        System.out.println("From: " + from);
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

        // reply
        emailController.sendReply(message, realContent);

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
        return clean;
    }

}
