package peer;

import parsers.PeerConfigParser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue; //implemented with heap ( log(n) operations)

import java.net.ServerSocket;

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

    private ServerSocket serverSocket;
    private final int port_num;
    private final int peer_id;

    public peerProcess(int port_num, int peer_id) {

        this.port_num = port_num;
        this.peer_id = peer_id;

    }


    public void run() {

        try {
            serverSocket = new ServerSocket(this.port_num);

            PeerConfigParser.loadPeerMetaData();
            ArrayList<PeerMetaData> peerCfgInfo = PeerConfigParser.getPeersMetaData();

            int connection_count = 0;

            while(connection_count != peerCfgInfo.size()) {

                // wait for (k - 1) incoming connections


                connection_count++;
            }


        } catch(Exception e) {
            e.printStackTrace();
        }




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





