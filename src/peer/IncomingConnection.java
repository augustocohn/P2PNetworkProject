package peer;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import constants.GlobalConstants;

public class IncomingConnection extends Thread {

    private int peerID;
    private int connectedPeerID;
    private Socket portConnection;
    private ObjectInputStream inputStream;
    private byte[] message;

    public IncomingConnection(int peerID, Socket portConnection) {
        this.peerID = peerID;
        this.portConnection = portConnection;
    }

    private void receive_message(){

        try{

            message = (byte[])inputStream.readObject();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private int verifyHandshake(){

        if(message.length != GlobalConstants.HS_MESSAGE_LEN) { return -1; }

        int curr = 0;
        byte[] header = Arrays.copyOfRange(message, curr, curr + GlobalConstants.HS_HEADER_LEN);
        curr = GlobalConstants.HS_HEADER_LEN;
        byte[] zeros = Arrays.copyOfRange(message, curr, curr + GlobalConstants.HS_ZERO_BIT_LEN);
        curr = GlobalConstants.HS_ZERO_BIT_LEN;
        ByteBuffer peer = ByteBuffer.wrap(Arrays.copyOfRange(message, curr, curr + GlobalConstants.HS_PEER_ID_LEN));
        byte[] check_zeros = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        if(Arrays.equals(header, GlobalConstants.HS_HEADER.getBytes()) && Arrays.equals(zeros, check_zeros)) {
            return peer.getInt();
        }
        return -1;
    }


    public void run() {

        try {
            inputStream = new ObjectInputStream(portConnection.getInputStream());

            // handshake stuff
            receive_message();
            this.connectedPeerID = verifyHandshake();
            if(connectedPeerID == -1) { throw new Exception("Invalid handshake message received"); }

            // run an infinite loop that parses incoming messages until the connection closes
            /*
            while(portConnection.isConnected()){

            }
             */

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
