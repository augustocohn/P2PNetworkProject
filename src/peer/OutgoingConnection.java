package peer;


import constants.GlobalConstants;
import messages.HandshakeMessage;
import messages.Message;
import parsers.PeerConfigParser;
import utils.FileUtility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;

public class OutgoingConnection extends Thread {

    private final int peerID;
    private final int connectedPeerID;
    private final String destinationHost;
    private final int destinationPortNum;
    private Socket portConnection;
    private ObjectOutputStream out;    //stream write to the socket

    public int getPeerID(){
        return this.peerID;
    }

    public int getConnectedPeerID(){
        return this.connectedPeerID;
    }
    public String getDestinationHost(){
        return this.destinationHost;
    }
    public int getDestinationPortNum(){
        return this.destinationPortNum;
    }

    public OutgoingConnection(int peerID, int destinationPeerID, String destinationHost, int destinationPortNum) {
        this.peerID = peerID;
        this.connectedPeerID = destinationPeerID;
        this.destinationHost = destinationHost;
        this.destinationPortNum = destinationPortNum;
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

    public byte[] convertIntToByte(int val){
        return BigInteger.valueOf(val).toByteArray();
    }

    public void sendPieceMessage(int index){
        try{
            FileUtility fileUtil = new FileUtility();
            byte[] payload = fileUtil.getFilePieceBytes(peerID, index);
            Message message = new Message(GlobalConstants.MSG_TYPE_PIECE, payload.length, payload);
            sendMessage(message.getByteMessage());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendHaveMessage(int index){
        try{
            Message message = new Message(GlobalConstants.MSG_TYPE_HAVE, 4, convertIntToByte(index));
            sendMessage(message.getByteMessage());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessage(byte[] msg){
        try{
            out.writeObject(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }





}
