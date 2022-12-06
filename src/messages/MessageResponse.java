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
        if(connectedPeerID == 1006){
            int x = 0;
        }
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

    public void sendRequestMessage(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        for(OutgoingConnection ogc : peer.getOutgoingConnections()){
            if(ogc.getConnectedPeerID() == connectedPeerID){
                BitFieldUtility bitUtil = new BitFieldUtility();
                Integer index = bitUtil.getRequestIndex(peerID, connectedPeerID); //TODO need to test to make sure it is getting a valid request index
                if(index == null) {
                    return;
                }
                peer.getRequested_pieces().put(connectedPeerID, index);
                ogc.sendRequestMessage(index);
            }
        }
    }

    public void sendAnotherRequest(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        Peer connectedPeer = Peer.getPeerByID(connectedPeerID);
        BitFieldUtility bitUtil = new BitFieldUtility();

        //condition should ensure the bitfield is not full
        if(!bitUtil.isBitFieldFull(peerID) && connectedPeer.getInterestedNeighbors().contains(peerID) && !peer.getChokedby().contains(connectedPeerID)) {
            sendRequestMessage(peerID, connectedPeerID);
        }

    }


}
