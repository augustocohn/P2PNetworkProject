package peer;


import constants.GlobalConstants;
import messages.HandshakeMessage;
import messages.Message;
import parsers.PeerConfigParser;
import utils.FileUtility;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLOutput;

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

            if(peerID == 1006){
                int x = 0;
            }
            //bitfield stuff
            if(PeerConfigParser.getPeerMetaData(peerID).hasFile()) {
                sendBitfieldMessage(peer);
            }

        } catch(Exception e){
            e.printStackTrace();
        }

        // while connection is open, do some periodic messaging
        while(!Peer.getCanCloseConnection()) {
            //do periodic communcications
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // once connection is allowed to be closed (boolean will break while loop above), close the outgoing connections

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            out.close();
            portConnection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Outgoing connection thread for " + this.peerID + " has ended");

    }

    public byte[] convertIntToByte(int val){
        byte[] temp = new byte[4];
        ByteBuffer buff = ByteBuffer.wrap(temp);
        buff.putInt(val);
        return buff.array();
    }

    public void sendChokeMessage(){
        Message mes = new Message(GlobalConstants.MSG_TYPE_CHOKE, 0, new byte[]{});
        sendMessage(mes.getByteMessage());
    }

    public void sendUnchokeMessage(){
        Message mes = new Message(GlobalConstants.MSG_TYPE_UNCHOKE, 0, new byte[]{});
        sendMessage(mes.getByteMessage());
    }

    public void sendInterestedMessage(){
        Message mes = new Message(GlobalConstants.MSG_TYPE_INTERESTED, 0, new byte[]{});
        sendMessage(mes.getByteMessage());
    }

    public void sendNotInterestedMessage(){
        Message mes = new Message(GlobalConstants.MSG_TYPE_NOT_INTERESTED, 0, new byte[]{});
        sendMessage(mes.getByteMessage());
    }

    public void sendHaveMessage(int index){
        Message mes = new Message(GlobalConstants.MSG_TYPE_HAVE, 4, convertIntToByte(index));
        sendMessage(mes.getByteMessage());
    }

    public void sendBitfieldMessage(Peer peer){
        Message mes = new Message(GlobalConstants.MSG_TYPE_BITFIELD, peer.getLocalBitField().length, peer.getLocalBitField());
        sendMessage(mes.getByteMessage());
    }

    public void sendRequestMessage(int index){
        Message mes = new Message(GlobalConstants.MSG_TYPE_REQUEST, 4, convertIntToByte(index));
        sendMessage(mes.getByteMessage());
    }

    public void sendPieceMessage(int index){
        FileUtility fileUtil = new FileUtility();
        byte[] payload = fileUtil.getFilePieceBytes(peerID, index);
        Message message = new Message(GlobalConstants.MSG_TYPE_PIECE, payload.length, payload);
        sendMessage(message.getByteMessage());
    }

    private void sendMessage(byte[] msg){;

        try{
            out.writeObject(msg);
            out.flush();
        }
        catch(Exception Exception){
//            Exception.printStackTrace();

        }
    }





}
