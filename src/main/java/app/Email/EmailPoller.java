package app.Email;

import javax.mail.*;

/**
 * Created by saahil claypool on 2/23/2017.
 */
public class EmailPoller extends Thread{
    String username;
    String password;
    Session session;
    EmailController emailController;
    volatile boolean isRunning = true;

    Store store;
    Folder folder;


    /**
     *
     * @param eController email controller that spawns the poller
     * @param username
     * @param password
     * @param session
     */
    public EmailPoller (EmailController eController, String username, String password, Session session){
        this.emailController = eController;
        this.username = username;
        this.password = password;
        this.session = session;
    }
    @Override
    public synchronized void run() {
        System.out.println("Started new thread");
        int i = 0;
        // start checking for new messages every second
        while ( isRunning) {
            try {
                login();
                Message[] new_messages = poll();
                System.out.println("Get Messages : " + new_messages.length);
                handleMessages (new_messages);
                System.out.println("handle messages");
                logout();
                sleep(1000);
                System.out.println("sleep");
            }
            catch (Exception e) {
                System.out.println(e.toString());
                e.printStackTrace();
            }
        }

        System.out.println("End stuff");
    }

    /**
     * Log into the system
     * @throws MessagingException
     */
    public void login () throws MessagingException {
        String host = "pop.gmail.com";// change accordingly
        String mailStoreType = "pop3";
        store = session.getStore("pop3s");
        System.out.println("Got Store");
        store.connect(host, username, password);
        //create the folder object and open it
        folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
    }

    /**
     * log out of the system
     * @throws MessagingException
     */
    public void logout () throws MessagingException {
        folder.close(false);
        store.close();

    }

    /**
     * Take the given set of messages and reply or handle them all
     * @param messages
     * @throws Exception
     */
    public void handleMessages (Message [] messages) throws Exception {
        for (int i = 0, n = messages.length; i < n; i++) {
            Message message = messages[i];
            handleMessage(message);
            System.out.println("message handled well");
        }
    }

    /**
     * create handler in new thread (maybe overkill)
     * @param message
     * @throws Exception
     */
    public void handleMessage (Message message) throws Exception {
        MessageHandler handler = new MessageHandler(emailController, message, session);
        handler.handleMessage();
    }


    /**
     * get the list of messages from the mailbox
     * @return
     * @throws MessagingException
     */
    public Message[] poll () throws MessagingException {
        return folder.getMessages();
    }
}
