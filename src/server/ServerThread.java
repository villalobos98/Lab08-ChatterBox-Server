package server;
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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

/**
 * The class that will know about the user.
 */
public class ServerThread extends Thread implements ChatterboxProtocol {

    private BufferedReader fromClient;
    private ChatterboxServer server;
    private PrintWriter writer;
    private String name;
    private Socket clientSocket;
    private boolean isRunning;

    /**
     * The constructor that will create the reader, writer, and boolean.
     * @param socket Socket
     * @param server server
     */
    public ServerThread(Socket socket, ChatterboxServer server){
        clientSocket = socket;
        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.isRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.server = server;
    }

    /**
     * Run the thread and reads the line from the client. If command message is sent then this funtion calls
     * the connect handler. Also makes sure that the loop is running and message from the user not null.
     */
    @Override
    public void run() {
        System.out.println("Thread started");

        String command;
        try {
            System.out.println("reading...");
            command = fromClient.readLine();
            System.out.println("raw input " + command);
            String[] info = command.split(SEPARATOR);
            if (info.length != 2) { //error checking for connect message
                System.out.println("FUCKED Up");
            } else if (!info[0].equals(CONNECT)) {//error checking
                System.out.println("You stupid");
            } else {
                //add to hash
                this.name = info[1];
                server.connectedHandler(info[1]);
                server.storeName(name,this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (!server.isDone() && isRunning) {
            String clientMessage = null;
            try {
                clientMessage = fromClient.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (clientMessage != null) {
                handleClientMessage(clientMessage);
            } else {
                server.disconnect(name);//terminates the loop
            }
        }
    }


    /**
     * Will read a message and call the daddy server, for broadcasting, disconnecting, or listing the users.
     * @param clientMessage the message the client sends.
     */
    private void handleClientMessage(String clientMessage) {
        System.out.println("Received " + clientMessage);

        System.out.print("<<" + name + ": ");
        String[] message = clientMessage.split(SEPARATOR);
        switch (message[0]){
            case CONNECT:
                break;
            case DISCONNECT:
                server.disconnect(name);
                isRunning= false;
                break;
            case SEND_CHAT:
                server.broadcast(CHAT_RECEIVED + SEPARATOR + this.name + SEPARATOR + message[1]);
                break;
            case SEND_WHISPER:
                server.whisper(message[1],SEND_WHISPER + SEPARATOR );
                break;
            case LIST_USERS:
                Set<String> users = server.listUser();
                break;

        }
    }

    /**
     * Write to the client
     * @param s message that will be written.
     */
    public void sendMessage(String s) {
        writer.println(s);
    }
}