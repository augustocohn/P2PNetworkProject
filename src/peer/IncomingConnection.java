package peer;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import constants.GlobalConstants;
import messages.Message;
import messages.MessageParser;
import messages.MessageResponse;

public class IncomingConnection extends Thread {

    private final int peerID;
    private int connectedPeerID;
    private final Socket portConnection;
    private ObjectInputStream inputStream;
    private byte[] message;

    public IncomingConnection(int peerID, Socket portConnection) {
        this.peerID = peerID;
        this.portConnection = portConnection;
    }

    // actually receives byte array from input port
    private void receiveMessage() {
        try{

            message = (byte[])inputStream.readObject();

        } catch(Exception e){
//            e.printStackTrace();
            message = GlobalConstants.MESSAGE_UNPROCESSED; // so that we don't reprocess messages
        }

    }

    // verifies fields in the handshake message are as expected and returns the integer representation of the peer ID of incoming message
    private int verifyHandshake(){ // TODO can make cleaner if one uses the HandshakeMessage class

        if(message == GlobalConstants.MESSAGE_UNPROCESSED || message.length != GlobalConstants.HS_MESSAGE_LEN) {
            return -1;
        }

        int curr = 0;
        byte[] header = Arrays.copyOfRange(message, curr, curr + GlobalConstants.HS_HEADER_LEN);
        curr += GlobalConstants.HS_HEADER_LEN;
        byte[] zeros = Arrays.copyOfRange(message, curr, curr + GlobalConstants.HS_ZERO_BIT_LEN);
        curr += GlobalConstants.HS_ZERO_BIT_LEN;
        ByteBuffer peer = ByteBuffer.wrap(Arrays.copyOfRange(message, curr, curr + GlobalConstants.HS_PEER_ID_LEN));
        byte[] check_zeros = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // runs an equals to make sure the handshake header and zeros in the handshake are as expected
        if(Arrays.equals(header, GlobalConstants.HS_HEADER.getBytes()) && Arrays.equals(zeros, check_zeros)) {
            return peer.getInt();
        }
        return -1;
    }


    private void processMessage() {

        if(message == GlobalConstants.MESSAGE_UNPROCESSED) {
            return;
        }


        if(peerID == 1002){
            int x = 0; //PLACE BREAKPOINT ON THIS LINE IF WANTING TO DEBUG ON SPECIFIC THREAD
        }

        int curr = 0;
        byte[] length = Arrays.copyOfRange(message, curr, curr + GlobalConstants.MESSAGE_LENGTH_LEN);
        curr += GlobalConstants.MESSAGE_LENGTH_LEN;
        int type = message[curr];
        curr += GlobalConstants.MESSAGE_TYPE_LEN;
        byte[] payload = null;

        int messageLength = ByteBuffer.wrap(length).getInt();

        if(messageLength != 0) {
            payload = Arrays.copyOfRange(message, curr, curr + messageLength);
        }

        Message message = new Message(type, messageLength, payload);

        System.out.println("Peer " + peerID + " received message of type " + type + " from " + connectedPeerID);

        MessageParser.ParseMessage(message, peerID, connectedPeerID);

    }

    private void clearMessage(){
        message = GlobalConstants.MESSAGE_UNPROCESSED;
    }

    public void run() {

        try {

            inputStream = new ObjectInputStream(portConnection.getInputStream());

            // handshake stuff
//            while(message != GlobalConstants.MESSAGE_UNPROCESSED) { // need to loop and keep trying until the message is processed
//                receive_message();
//            }
            receiveMessage();
            this.connectedPeerID = verifyHandshake();
            System.out.println(peerID + " received handshake from " + connectedPeerID);
            if(connectedPeerID == -1) { throw new Exception("Invalid handshake message received"); }

            Peer.getPeerByID(peerID).getLogger().tcpConnectionFromLog(connectedPeerID);

            MessageResponse mr = new MessageResponse();
            // run an infinite loop that parses incoming messages until the connection closes
            while(portConnection.isConnected()) {
                // call method - passes peer id as param and returns the boolean memb
                if(Peer.getCanCloseConnection()) {
                    // SHUT EVERYTHING THE FUCK DOWN (close connection port)
                    Thread.sleep(50);
                    inputStream.close();
                    portConnection.close();
                    break;
                }

                receiveMessage();

                // need to have functionality so that a previous message isn't processed (or would that make a difference?)
                // I think it would, so in the receive_message() function, I am going to introduce functionality to set message to a NOT_PROCESSED
                if (message == GlobalConstants.MESSAGE_UNPROCESSED) {
                    continue;
                }

                processMessage(); // sends message type, length, and payload along with the peerID of the

            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Incoming connection thread for " + this.peerID + " has ended");

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
