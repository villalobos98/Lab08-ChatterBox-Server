package server;
/**
 *  * @author: Isaias Villalobos
 * @date: 3/25/18
 * @language: Java 9
 * @professor: James Heliotis
 *
 */

import common.ChatterboxProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * The server that will communicate with the client. Will start the server threads, and create the server socket.
 */
public class ChatterboxServer implements ChatterboxProtocol {
    private ServerSocket server;
    private Map<String, ServerThread> map;
    private boolean isDone;

    /**
     * Creates the map that will store all the
     *
     * @param port the port number that is passed into the function
     */
    public ChatterboxServer(int port) {
        isDone = false;
        map = new HashMap<>();
        try {
            server = new ServerSocket(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A loop that will begin the connection with the client and start the threads.
     */
    private void begin() {
        while (true) {
            try {
                Socket clientSocket = server.accept();
                System.out.println("ChatterBox connection received from " + clientSocket.getInetAddress().getHostAddress());

                //add to hash
                ServerThread thread = new ServerThread(clientSocket, this);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Handles only the message for the "connected" response.
     * Tells everyone on the server that the user has connected.
     *
     * @param username String name of user
     */
    public void connectedHandler(String username) {
        for (ServerThread n : map.values()) {
            n.sendMessage(CONNECTED + SEPARATOR + username);//assume message sent to client
        }
        System.out.println(username + "");
    }

    /**
     * Never used. Was used when needed to end the connectino.
     */
    public void setDone() {
        this.isDone = true;
    }

    /**
     * Will make sure that the while loop terminates
     * @return
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Main calls other methods.
     * @param args
     */
    public static void main(String[] args) {
        ChatterboxServer x = new ChatterboxServer(PORT);
        x.begin();
    }

    /**
     * The message that the user sends when they want to get off.
     * @param name
     */
    public void disconnect(String name) {
        map.get(name).sendMessage(DISCONNECTED);
        map.remove(name);
        broadcast(USER_LEFT + SEPARATOR + name);//assume message sent to client
    }

    /**
     * @param name Name of user
     * @param serverThread the thread that will be put ino the map
     */
    public void storeName(String name, ServerThread serverThread) {
        System.out.println("Got name as " + name);
        map.put(name, serverThread);
    }

    /**
     * @param user String name of user
     * @param message The message of the "whisper"
     */
    public void whisper(String user, String message) {
        map.get(user).sendMessage(message);

    }

    /**
     * Broadcast message to everyone. Should write to the client
     * @param message
     */
    public void broadcast(String message) {
        for (ServerThread n : map.values()) {
            n.sendMessage(message);//assume message sent to client
        }
    }

    /**
     * Get the name of the users that are on the server, so the keys of the map
     * @return
     */
    public Set<String> listUser() {
        return map.keySet();
    }
}
