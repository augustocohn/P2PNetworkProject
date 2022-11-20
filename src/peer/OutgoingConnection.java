package peer;


import constants.GlobalConstants;
import messages.HandshakeMessage;
import messages.Message;
import parsers.PeerConfigParser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class OutgoingConnection extends Thread {

    private int peerID;
    private int destinationPeerID;
    private String destinationHost;
    private int destinationPortNum;
    private Socket portConnection;
    private ObjectOutputStream out;    //stream write to the socket

    public OutgoingConnection(int peerID, int destinationPeerID, String destinationHost, int destinationPortNum) {
        this.peerID = peerID;
        this.destinationPeerID = destinationPeerID;
        this.destinationHost = destinationHost;
        this.destinationPortNum = destinationPortNum;
    }

    private void sendMessage(String msg)
    {
        try{
            out.writeObject(msg);
            out.flush();
            System.out.println("Send message: " + msg + " to Client " + destinationPeerID);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    private void sendMessage(byte[] msg){
        try{
            out.writeObject(msg);
            out.flush();

            if(msg[0] == 'P') {
                //Converts byte array to int for interpretation
                ByteBuffer pID_buff = ByteBuffer.wrap(Arrays.copyOfRange(msg, 28, 32));
                int pID = pID_buff.getInt();

                String str_msg = new String(Arrays.copyOfRange(msg, 0, 28));
                System.out.println("Peer: " + this.peerID + " sent message: " + str_msg + pID + " to Client " + destinationPeerID);
            } else {
                System.out.println("Peer: " + this.peerID + " sent message type: " + (int)msg[4] + " to Client " + destinationPeerID);
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void run() {

        Peer peer = Peer.getPeerByID(peerID);
        // get that connection
        while(true) {

            try {
                portConnection = new Socket(this.destinationHost, this.destinationPortNum);
                out = new ObjectOutputStream(portConnection.getOutputStream());
                break;
            } catch(Exception e) {
                System.out.println("Waiting: trying to connect");
            }
        }

        try{

            // handshake stuff
            HandshakeMessage hsm = new HandshakeMessage(this.peerID);
            sendMessage(hsm.getByteMessage());

            //bitfield stuff
            if(PeerConfigParser.getPeerMetaData(peerID).hasFile()) {
                Message mes = new Message(GlobalConstants.MSG_TYPE_BITFIELD, peer.getLocalBitField().length, peer.getLocalBitField());
                sendMessage(mes.getByteMessage());
            }


        } catch(Exception e){
            e.printStackTrace();
        }


        // send bit field message stuff

        // while connection is open, do some periodic messaging
        while(!Peer.getCanCloseConnection()) {
            //do periodic communcications
        }

        // once connection is allowed to be closed (boolean will break while loop above), close the outgoing connections

        System.out.println("Outgoing connection thread for " + this.peerID + " has ended");
    }

}
