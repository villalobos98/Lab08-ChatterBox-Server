package client;
/**
 *  * @author: Isaias Villalobos
 * @date: 3/25/18
 * @language: Java 9
 * @professor: James Heliotis
 *
 */

import common.ChatterboxProtocol;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * The class that is going to listen for the server commands. A thread constantly running to get the input of the server
 */
public class ClientListener extends Thread implements ChatterboxProtocol {

    private BufferedReader fromServer;
    private ChatterboxClient client;

    /**
     * @param fromServer The string that the server will send to this method
     * @param client     The client that is typing in messages.
     */
    public ClientListener(BufferedReader fromServer, ChatterboxClient client) {

        this.fromServer = fromServer;
        this.client = client;
    }

    /**
     * Run method that is reading the server line, server messages
     * A loop that will keep going so long as the user is still not done.
     */
    public void run() {
        while (!client.isDone()) {

            String serverMessage = null;
            try {
                serverMessage = fromServer.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serverMessage != null) {
                handleServerMessage(serverMessage);
            } else {
                client.setDone();
            }
        }
    }

    /**
     * Method designed specifically for the handling of the server messages.
     * @param message String message
     */
    private void handleServerMessage(String message) {
        System.out.println("Received from server:  " + message);
        String[] data = message.split(SEPARATOR);
        switch (data[0]) {
            case DISCONNECTED:
                System.out.println("disconnected.");
                break;
            case CONNECTED:
                System.out.println("connected");
                break;
            case CHAT_RECEIVED:
                System.out.println("Chat received from:: " + data[1] + " message is: " + data[2]);
                break;
            case WHISPER_RECEIVED:
                System.out.println("chat received:: ");
                break;
            case WHISPER_SENT:
                System.out.println("whisper_received:: ");
                break;
            case USERS:
                System.out.println("users:: ");
                break;
            case USER_JOINED:
                System.out.println("user_joined:: ");
                break;
            case ERROR:
                System.out.println("fatal_error:: ");
                break;
        }
    }


}
