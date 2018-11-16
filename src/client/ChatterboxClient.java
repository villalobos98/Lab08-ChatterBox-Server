package client;
/**
 * @author: Isaias Villalobos
 * @date: 3/25/18
 * @language: Java 9
 * @professor: James Heliotis
 *
 * This class is designed for implementation of a client. The client will send messages
 * to the server. This class will use a client listener to listen for all the messages that the
 * server is sending.
 */

import common.ChatterboxProtocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatterboxClient implements ChatterboxProtocol {

    private Socket clientServer;
    private ClientListener fromServer;
    private PrintWriter toServer;
    private String username;
    private Scanner fromUser;
    private Boolean isDone;

    /**
     * Constructor to make a client, will use a local host for host and PORT given in ChatterBox Protocol
     * @param username String
     */
    public ChatterboxClient(String username) {
        this.username = username;

        try {
            clientServer = new Socket("localhost", PORT);

            fromServer = new ClientListener(new BufferedReader(new InputStreamReader(clientServer.getInputStream())),this);
            toServer = new PrintWriter (clientServer.getOutputStream(), true);

            fromUser = new Scanner(System.in);
            isDone = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * run method used to start the client listener.
     * if the message is not done then the we get the next line
     * else a flag is set and the program terminates.
     */
    void run(){
        toServer.println(CONNECT + SEPARATOR + username);
        System.out.println("Message sent");
        fromServer.start();
        while (!isDone) {
            String message = fromUser.nextLine();

            if (message != null) {//FUNKY
                handleClientMessage(message);
            }
            else{
                isDone = true;
            }
        }
    }

    /**
     *
     * @param message string that the user enters.
     */
    private void handleClientMessage(String message) {
        char c = message.charAt(1);
        switch (c) {
            case 'h':
                System.out.println("  /help - displays this message\n" +
                        " /quit - quit Chatterbox\n" +
                        " /c <message> - send a message to all currently connected users\n" +
                        " /w <recipient> <message> - send a private message to the recipient\n" +
                        " /list - display a list of currently connected users");
                break;
            case 'q':
                System.out.println("Are you sure? (y/n)");
                String userInput = fromUser.nextLine();
                if (userInput.equals("y")) {
                    System.out.println("Goodbye");
                    toServer.println(DISCONNECT);
                    isDone = true;
                    return;
                }
                break;
            case 'c':
                toServer.println(SEND_CHAT + SEPARATOR + message.substring(3));
                break;
            case 'w':
                String[] stuff = message.split(" ", 2);//possible error
                toServer.println(SEND_WHISPER + SEPARATOR + stuff[1] + SEPARATOR + stuff[2]);
                break;
            case 'l':
                toServer.println(LIST_USERS);
        }

    }

    /**
     * @return boolean that tells us whether the user is done sending messages
     */
    public boolean isDone(){
        return isDone;
    }

    /**
     * sets the flag to terminate the while loop
     *
     */
    public void setDone(){
        isDone = true;
        System.out.println();//WHY IS THIS HERE?
    }

    /**
     * Given a name wil make a client and run the program.
     * @param args command line args entered
     */
    public static void main(String[] args) {
        System.out.println("Chatterbox server host: localhost");
        System.out.println("Chatterbox server port: "+PORT);
        System.out.println("Username: "+args[0]);

        ChatterboxClient client  = new ChatterboxClient(args[0]);
        client.run();
    }

}

