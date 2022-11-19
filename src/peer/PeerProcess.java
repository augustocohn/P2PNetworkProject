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


public class PeerProcess extends Thread {

    private static final int listening_port = 8000;

    private String message;    //message received from the client
    private String MESSAGE;    //uppercase message send to the client
    private Socket connection;
    private int clientID;		//The index number of the client

    private ServerSocket serverSocket;
    private final int port_num;
    private final int peer_id;

    public PeerProcess(int port_num, int peer_id) {

        this.port_num = port_num;
        this.peer_id = peer_id;

    }

    public void listenAndParseMessage() {

    }

    public void run() {

        try {
            serverSocket = new ServerSocket(this.port_num);

            int connection_count = 0;
            Socket acceptedConnection = null;

            //TODO
            //Since it is a requirement that peers be started on command, we must
            //read in the PeerConfigParser for each peer since we may not have access
            //to the StartUp.java process. For now we will continue using StartUp
            //for development but we will have to use individual calls for each
            //peer as specified in the project description

            //PeerConfigParser.loadPeerMetaData();


            while(connection_count < PeerConfigParser.getPeersMetaData().size()-1) {
                IncomingConnection incoming = new IncomingConnection(peer_id, serverSocket.accept());
                //System.out.println("Peer: " + this.peer_id + " | Client connected: " + acceptedConnection);
                incoming.start();
                connection_count++;
                //System.out.println("Peer: " + this.peer_id + " | Has " + connection_count + " current connections");
            }

            System.out.println(this.peer_id + " has connected to all peers");


//            while(!Peer.get_can_close_connection()) {
//                // process the messages incoming - or do something to keep thread alive, or do we have to, I actually don't think we want this
//                // we actually may want the thread to close so that the rest of the run() method in Peer runs (I think this is the case)
//
//            }


        } catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server thread " + this.peer_id + " has ended");
    }
}





