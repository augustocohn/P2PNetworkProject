package messages;

import peer.Peer;
import utils.BitFieldUtility;
import utils.Download;

public class MessageAction {

    // this class contains all of the methods to be called for each action
    // choke, unchoke, interested, uninterested, bitfield, have, request, piece


    // these following two methods might be better off in the Peer class itself
    public void addToChokedBy(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getChokedby().add(connectedPeerID);
    }

    public void removeFromChokedBy(int peerID, int connectedPeerID) {
        Peer peer = Peer.getPeerByID(peerID);
        peer.getChokedby().remove(connectedPeerID);
    }

    public void addToInterestedNeighbors(int peerID, int connectPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        peer.getInterestedNeighbors().add(connectPeerID);
    }

    public void removeFromInterestedNeighbors(int peerID, int connectedPeerID){
        Peer peer = Peer.getPeerByID(peerID);
        peer.getInterestedNeighbors().remove(connectedPeerID);
    }

    public void updateNeighborBitFields(int peerID, int connectedPeerID, byte[] bitfield){
        Peer peer = Peer.getPeerByID(peerID);
        peer.getNeighborBitFields().put(connectedPeerID, bitfield);
    }

    public void updateNeighborBitField(int peerID, int connectedPeerID, int piece){
        BitFieldUtility bitUtil = new BitFieldUtility();
        bitUtil.updateNeighborBitField(peerID, connectedPeerID, piece);
    }

    public void placePiece(int peerID, int connectedPeerID, int index, byte[] piece){
        BitFieldUtility bitUtil = new BitFieldUtility();
        Peer peer = Peer.getPeerByID(peerID);
        bitUtil.placePiece(peerID, index, piece);
        peer.getRequested_pieces().remove(connectedPeerID);
    }

    public void updateBitField(int peerID, int index){
        BitFieldUtility bitUtil = new BitFieldUtility();
        bitUtil.updateBitfield(peerID, index);
    }

    public void updateDownloadSpeed(int peerID, int connectedPeerID) {

        Peer peer = Peer.getPeerByID(peerID);
        for(Download download : peer.getPriorityNeighbors()) {
            if(download.getPeerID() == connectedPeerID) {
                download.incrementCount();
                return;
            }
        }

        peer.getPriorityNeighbors().add((new Download(connectedPeerID, 1)));

    }

}
