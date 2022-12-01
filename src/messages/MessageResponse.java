package messages;

import messages.Message;
import peer.OutgoingConnection;
import peer.Peer;

import java.io.ObjectOutputStream;
import java.net.Socket;

// this class is meant to act as a means through which to send messages to certain neighbors depending on the behavior of the
//-methods called in the MessageAction and MessageParser classes (and other methods across the classes of interest)
public class MessageResponse {


    public void sendPieceMessage(int peerID, int connectedPeerID, int piece){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendPieceMessage();
            }
        }
    }


}
