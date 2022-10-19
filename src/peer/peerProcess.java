package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue; //implemented with heap ( log(n) operations)
// this file will essentially be called by another file and all it does is create a process (or thread) for a particular
// peer to start running


public class peerProcess extends Thread {

    private static final int listening_port = 8000;

    private String message;    //message received from the client
    private String MESSAGE;    //uppercase message send to the client
    private Socket connection;
    private ObjectInputStream in;	//stream read from the socket
    private ObjectOutputStream out;    //stream write to the socket
    private int clientID;		//The index number of the client

    public peerProcess(Socket connection, int clientID) {

        this.connection = connection;
        this.clientID = clientID;
    }


    public void run() {


        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());

            try {

                while(true) {
                    message = (String)in.readObject();
                    System.out.println("Received ur mom: " + message);
                    MESSAGE = message.toUpperCase();
                    sendMessage(MESSAGE); //implement later
                }
            } catch(ClassNotFoundException classnot) {
                System.out.println("Class not found: " + classnot);
            }

        } catch(IOException ioException){
            System.out.println("Disconnect with Client " + clientID);
        }
        finally{
            //Close connections
            try{
                in.close();
                out.close();
                connection.close();
            }
            catch(IOException ioException){
                System.out.println("Disconnect with Client " + clientID);
            }
        }
    }


    //send a message to the output stream
    public void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("Send message: " + msg + " to Client " + clientID);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }



}





