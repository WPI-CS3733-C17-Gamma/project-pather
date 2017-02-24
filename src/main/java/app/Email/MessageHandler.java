package app.Email;

import javax.mail.BodyPart;
    import javax.mail.Message;
    import javax.mail.Session;
    import javax.mail.internet.MimeMultipart;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class MessageHandler extends Thread {
    EmailController emailController;
    Message message;
    Session session;

    public MessageHandler(EmailController eController, Message message, Session session) {
        this.emailController = eController;
        this.message = message;
        this.session = session;
    }

    @Override
    public synchronized void start() {
        super.start();
        try {
            handleMessage();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("handle message thread failed");
        }
    }

    public void handleRealContent (String content) {

    }
    public void handleMessage () throws Exception {
        String from = message.getFrom()[0].toString();
        System.out.println("From: " + from);
        String messageContent = getTextFromMessage(message);
        System.out.println("raw content: " + messageContent);

        String realContent = getRealContent(messageContent);
        if (realContent.equals("")) {
            realContent = messageContent;
        }
        System.out.println("real content : " + realContent);
        System.out.println("end of content");
        System.out.flush();

        // reply
//        SendEmail.send(from, realContent, session);
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

    public String getRealContent (String unparsedString) {
        String [] lines = unparsedString.split("\\n");
        String clean = "";
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.contains("<") || line.contains("--")) {
                break;
            }
            clean += line;
        }
        return clean;
    }

}
