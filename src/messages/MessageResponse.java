package messages;

import messages.Message;
import peer.OutgoingConnection;
import peer.Peer;
import utils.BitFieldUtility;

import java.io.ObjectOutputStream;
import java.net.Socket;

// this class is meant to act as a means through which to send messages to certain neighbors depending on the behavior of the
//-methods called in the MessageAction and MessageParser classes (and other methods across the classes of interest)
public class MessageResponse {

    public void addToUnchokedNeighbors(int peerID, int connectPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getUnchoked_neighbors().add(connectPeerID);
    }

//    public void updateChokeSets(int peerID, int connectedPeerID) {
//        //addToChokedBy(peerID, connectedPeerID);
//    }
//
//    public void updateUnchokedSets(int peerID, int connectedPeerID) {
//        //removeFromChokedBy(peerID, connectedPeerID);
//    }

    public void updateBitFields(int peerID) {
        BitFieldUtility bitUtil = new BitFieldUtility();
        bitUtil.compareBitfields(peerID);
    }

    public void updateBitField(int peerID, int connectedPeerID) {
        BitFieldUtility bitUtil = new BitFieldUtility();
        boolean bitFieldDiff = bitUtil.compareBitfield(Peer.getPeerByID(peerID), Peer.getPeerByID(connectedPeerID));
        if(bitFieldDiff) {
            sendNotInterestedMessage(peerID, connectedPeerID);
        } else {
            sendInterestedMessage(peerID, connectedPeerID);
        }
    }

    public void sendPieceMessage(int peerID, int connectedPeerID, int index){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendPieceMessage(index);
            }
        }
    }

    public void sendHaveMessage(int peerID, int index){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            ogc.sendHaveMessage(index);
        }
    }

    public void sendChokeMessage(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendChokeMessage();
            }
        }
    }

    public void sendUnchokeMessage(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendUnchokeMessage();
            }
        }
    }

    public void sendInterestedMessage(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendInterestedMessage();
            }
        }
    }

    public void sendNotInterestedMessage(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendNotInterestedMessage();
            }
        }
    }

    public void sendRequestMessage(int peerID, int connectedPeerID, int index){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                ogc.sendRequestMessage(index);
            }
        }
    }


}
